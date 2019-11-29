package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class ElementWorker {
    ThreadContainers dataStorage;
    private LazyTransition transition;
    TypedNFAState eventState;
    private List<ThreadContainers> oppositeBuffers;
    private boolean isSecondaryAddToList = false;

    public long actualCalcTime = 0;
    public long windowverifyTime = 0;
    public long numberOfHandledItems = 0;
    public long numberOfOppositeItems = 0;
    public  long idleTime = 0;
    public long iteratingBufferTime = 0;
    public long sliceTime = 0;
    public long sliceTimeActual = 0;
    public long sendMatchingTime = 0;
    public long conditionTime = 0;
    public Long innerCondTime = 0L;
    public Long innerWindowTime = 0L;

    public ElementWorker(TypedNFAState eventState,
                         List<ThreadContainers> oppositeBuffers)
    {
        this.eventState = eventState;
        this.oppositeBuffers = oppositeBuffers;
        transition = (LazyTransition) eventState.getActualNextTransition();
    }

    public void handleElement(ContainsEvent newElement) {
        ContainsEvent removingCriteria = null;
        long latestTimeStamp = Long.MIN_VALUE;
        dataStorage.addEventToOwnBuffer(newElement);
        Iterator<ThreadContainers> iterator = oppositeBuffers.iterator();
        while (iterator.hasNext()) {
            ThreadContainers buffer = iterator.next();
//            long time = System.nanoTime();
            ContainsEvent ce = iterateOnSubList(newElement, buffer.getBufferSubListWithReadLock());
//            iteratingBufferTime += System.nanoTime() - time;
            buffer.releaseReadLock();
            if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
                latestTimeStamp = ce.getEarliestTimestamp();
                removingCriteria = ce;
            }
        }
        if (removingCriteria != null) {
//            long time =  System.nanoTime();
            dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted(), removingCriteria);
//            innerCondTime += System.nanoTime() - time;
        }
    }
    public void finishRun() {
        System.out.println("Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " has finished at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                " Compared to " + numberOfOppositeItems + " items Condition time " + conditionTime / 1000000 +
                " Iterating buffer time " + iteratingBufferTime / 1000000 + " Slice time " + sliceTime / 1000000 + " Actual Slice time " + sliceTimeActual / 1000000 + " Send sync time " + sendMatchingTime / 1000000 +
                " Calculation time " + actualCalcTime / 1000000 + " Window verify time " + windowverifyTime / 1000000 + " Cond 1 " + innerCondTime / 1000000 + " Cond 2 " + innerWindowTime / 1000000);
    }
    protected abstract boolean isBufferSorted();


    protected boolean isEventCompatibleWithPartialMatch(Match partialMatch, List<Event> partialMatchEvents, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness
        long time = System.nanoTime();
        boolean b =  transition.verifyCondition(partialMatchEvents);
//        actualCalcTime += System.nanoTime() - time;
        numberOfOppositeItems++;
        if (b) {
//            time = System.nanoTime();
            boolean w = verifyTimeWindowConstraint(partialMatch, event);
//            conditionTime += System.nanoTime() - time;
            return  w;
        }
        return  false;
    }

    private boolean verifyTimeWindowConstraint(Match partialMatch, Event event) {

        return (partialMatch.getLatestEventTimestamp() <= event.getTimestamp() + dataStorage.getTimeWindow()) &&
                (partialMatch.getEarliestEvent() + dataStorage.getTimeWindow() >= event.getTimestamp());
    }

    protected void checkAndSendToNextState(Event event, List<Event> partialMatchEvents, Match match) {
//        long time = System.nanoTime();
        partialMatchEvents.add(event);
        if (isEventCompatibleWithPartialMatch(match, partialMatchEvents, event)) {
//            windowverifyTime += System.nanoTime() - time;
//            long time = System.nanoTime();
            sendToNextState(match.createNewPartialMatchWithEvent(event));
//            sendMatchingTime += System.nanoTime() - time;
        }
//        else {
//            windowverifyTime += System.nanoTime() - time;
//        }
//        time = System.nanoTime();
        partialMatchEvents.remove(partialMatchEvents.size() - 1);
//        actualCalcTime += System.nanoTime() - time;
    }

    protected void sendToNextState(Match newPartialMatchWithEvent) {

        BlockingQueue<Match> matchesQueue = dataStorage.getNextStateOutput();
        try {
//            long time = System.nanoTime();
            matchesQueue.put(newPartialMatchWithEvent);
//            windowverifyTime += System.nanoTime() - time;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initializeDataStorage(ThreadContainers dataStorage) {
        this.dataStorage = dataStorage;
    }


    protected abstract ContainsEvent iterateOnSubList(ContainsEvent newElement, List<ContainsEvent> bufferSubList);

    public void updateOppositeWorkers(ElementWorker secondaryTask) {
        if (!isSecondaryAddToList) {
            oppositeBuffers.add(secondaryTask.dataStorage);
            isSecondaryAddToList = true;
        }
    }
}
