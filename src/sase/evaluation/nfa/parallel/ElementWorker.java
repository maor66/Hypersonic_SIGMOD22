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

public abstract class ElementWorker {
    final ThreadContainers dataStorage;
    private final LazyTransition transition;
    final TypedNFAState eventState;
    private final ParallelQueue<Match> outputQueue;
    private final List<ThreadContainers> oppositeBuffers;
    private boolean isSecondaryAddToList = false;
    private boolean isFirstHandle = true;

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

    public long lastCriteriaTimestamp = 0;
    private int lastRemovedNumber= 0;
    private  int currentBackoff = 0;
    private  int backoffStep = 1;

    public ElementWorker(TypedNFAState eventState, ThreadContainers dataStorage, List<ThreadContainers> oppositeBuffers, ParallelQueue<Match> outputQueue)
    {
        this.eventState = eventState;
        this.dataStorage = dataStorage;
        this.oppositeBuffers = oppositeBuffers;
        this.outputQueue = outputQueue;
        transition = (LazyTransition) eventState.getActualNextTransition();
    }

    public void handleElement(ContainsEvent newElement) {
        if (isFirstHandle) {
            isFirstHandle = false;
            dataStorage.setContainerActive();
        }
        numberOfHandledItems++;
        ContainsEvent removingCriteria = null;
        long latestTimeStamp = Long.MIN_VALUE;
        dataStorage.addEventToOwnBuffer(newElement);
        Iterator<ThreadContainers> iterator = oppositeBuffers.iterator();
        while (iterator.hasNext()) {
            long time = System.nanoTime();

            ThreadContainers buffer = iterator.next();
//            long time = System.nanoTime();
            if (!buffer.isContainerActive()) {
                sliceTime += System.nanoTime() - time;

                continue; // skip empty sub-lists (that were always empty)
            }
            sliceTime += System.nanoTime() - time;

            time = System.nanoTime();
            List<ContainsEvent>  l  = buffer.getBufferSubListWithReadLock();
            sliceTimeActual += System.nanoTime() - time;
            time = System.nanoTime();
            ContainsEvent ce = iterateOnSubList(newElement, l);
            iteratingBufferTime += System.nanoTime() - time;
//            iteratingBufferTime += System.nanoTime() - time;
            buffer.releaseReadLock();
            if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
                latestTimeStamp = ce.getEarliestTimestamp();
                removingCriteria = ce;
            }
        }
        long time = System.nanoTime();

        if (removingCriteria != null) {
            if ( currentBackoff <= 0)  {
//            long time =  System.nanoTime();
                lastCriteriaTimestamp = removingCriteria.getEarliestTimestamp();
                lastRemovedNumber = dataStorage.removeExpiredElements(lastCriteriaTimestamp, isBufferSorted(), removingCriteria);
                backoffStep = lastRemovedNumber > 0 ? 1: backoffStep*2;
                currentBackoff  = lastRemovedNumber > 0 ? 0: backoffStep ;
//            innerCondTime += System.nanoTime() - time;
            }
            else {
                currentBackoff--;
            }
        }
        innerCondTime += System.nanoTime() - time;
    }

    protected abstract boolean oppositeTaskNotFinished(List<BufferWorker> workersNeededToFinish);

    public void finishRun() {
        System.out.println("Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " Of state "+ eventState + " has finished at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                " Handled " + numberOfHandledItems + " Compared to " + numberOfOppositeItems + " items Condition time " + conditionTime / 1000000 +
                " Iterating buffer time " + iteratingBufferTime / 1000000 + " Slice time " + sliceTime / 1000000 + " Actual Slice time " + sliceTimeActual / 1000000 + " Send sync time " + sendMatchingTime / 1000000 +
                " Calculation time " + actualCalcTime / 1000000 + " Window verify time " + windowverifyTime / 1000000 + " Cond 1 " + innerCondTime / 1000000 + " Cond 2 " + innerWindowTime / 1000000);
    }
    protected abstract boolean isBufferSorted();


    protected boolean isEventCompatibleWithPartialMatch(Match partialMatch, List<Event> partialMatchEvents, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness
//        long time = System.nanoTime();
        boolean b =  transition.verifyFirstStepCondition(partialMatchEvents);
//        actualCalcTime += System.nanoTime() - time;
        numberOfOppositeItems++;
        if (b) {
//            time = System.nanoTime();
            boolean s =  transition.verifySecondStepCondition(partialMatchEvents);
//            conditionTime += System.nanoTime() - time;
            if (!s) return false;
            boolean w = verifyTimeWindowConstraint(partialMatch, event);
            return  w;
        }
        return  false;
    }

    private boolean verifyTimeWindowConstraint(Match partialMatch, Event event) {

        return (partialMatch.getLatestEventTimestamp() <= event.getTimestamp() + dataStorage.getTimeWindow()) &&
                (partialMatch.getEarliestEvent() + dataStorage.getTimeWindow() >= event.getTimestamp());
    }

    protected void checkAndSendToNextState(Event event, List<Event> partialMatchEvents, Match match) {
        long time = System.nanoTime();
        partialMatchEvents.add(event);
        if (isEventCompatibleWithPartialMatch(match, partialMatchEvents, event)) {
//            windowverifyTime += System.nanoTime() - time;
//             time = System.nanoTime();
            sendToNextState(match.createNewPartialMatchWithEvent(event));
//            sendMatchingTime += System.nanoTime() - time;
        }
//        else {
//            windowverifyTime += System.nanoTime() - time;
//        }
//        time = System.nanoTime();
        partialMatchEvents.remove(partialMatchEvents.size() - 1);
        innerWindowTime += System.nanoTime() - time;
    }

    protected void sendToNextState(Match newPartialMatchWithEvent) {

        long time = System.nanoTime();
        outputQueue.put(newPartialMatchWithEvent);
            windowverifyTime += System.nanoTime() - time;
    }

    protected abstract ContainsEvent iterateOnSubList(ContainsEvent newElement, List<ContainsEvent> bufferSubList);

    public void updateOppositeWorkers(ElementWorker secondaryTask) {
        if (!isSecondaryAddToList) {
            oppositeBuffers.add(secondaryTask.dataStorage);
            isSecondaryAddToList = true;
        }
    }
}
