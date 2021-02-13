package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.FinisherMatch;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ElementWorker {
    public long currentBufferSize = 0;
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

    public long lastCriteriaTimestamp = 0;
    private int lastRemovedNumber= 0;
    private  int currentBackoff = 0;
    private  int backoffStep = 1;
    private long maxElements = 0;
    private static int id_global = 0;
    private int id = 0;

    public ElementWorker(TypedNFAState eventState,
                         List<ThreadContainers> oppositeBuffers)
    {
        this.eventState = eventState;
        this.oppositeBuffers = oppositeBuffers;
        transition = (LazyTransition) eventState.getActualNextTransition();
        id = id_global++;
    }

    public void handleElement(ContainsEvent newElement, List<Worker> workersNeededToFinish, ParallelQueue<? extends  ContainsEvent> input) {
        ContainsEvent removingCriteria = null;
        long latestTimeStamp = Long.MIN_VALUE;
        recordMaxElements(dataStorage.addEventToOwnBuffer(newElement));
        Iterator<ThreadContainers> iterator = oppositeBuffers.iterator();
        while (iterator.hasNext()) {
            ThreadContainers buffer = iterator.next();
            long time = System.nanoTime();
            ContainsEvent ce = iterateOnSubList(newElement, buffer.getBufferSubListWithReadLock());
            iteratingBufferTime += System.nanoTime() - time;
            buffer.releaseReadLock();
            if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
                latestTimeStamp = ce.getEarliestTimestamp();
                removingCriteria = ce;
            }
        }
        if (removingCriteria != null) {
            if ( currentBackoff <= 0)  {
                lastCriteriaTimestamp = removingCriteria.getEarliestTimestamp();
                long time =  System.nanoTime();
                lastRemovedNumber = dataStorage.removeExpiredElements(lastCriteriaTimestamp, isBufferSorted(), removingCriteria);
                innerCondTime += System.nanoTime() - time;
                currentBackoff = 1;
            }
            else {
                currentBackoff--;
            }
        }
    }

    private void recordMaxElements(long currentNumberOfElements) {
        currentBufferSize = currentNumberOfElements;
        if (currentNumberOfElements > maxElements) {
            maxElements = currentNumberOfElements;
        }
    }

    protected abstract boolean oppositeTaskNotFinished(List<BufferWorker> workersNeededToFinish);

    public void finishRun() {
        sendToNextState(new FinisherMatch());
        System.out.println("Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " has finished at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                " Compared to " + numberOfOppositeItems + " Max Elements "+ maxElements +  " items Condition time " + conditionTime / 1 +
                " Iterating buffer time " + iteratingBufferTime / 1000000 + " Slice time " + sliceTime / 1 + " Actual Slice time " + sliceTimeActual / 1000000 + " Send sync time " + sendMatchingTime / 1000000 +
                " Calculation time " + actualCalcTime / 1000000 + " Window verify time " + windowverifyTime / 1000000 + " Cond 1 " + innerCondTime / 1000000 + " Cond 2 " + innerWindowTime / 1000000);
    }
    protected abstract boolean isBufferSorted();


    protected boolean isEventCompatibleWithPartialMatch(Match partialMatch, List<Event> partialMatchEvents, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness
        boolean b;
        long time;
//        if (numberOfOppositeItems % 1000 == 0) {
            time = System.nanoTime();
             b =  transition.verifyFirstStepCondition(partialMatchEvents);
            actualCalcTime += System.nanoTime() - time;
//        }
//        else {
//            b =  transition.verifyFirstStepCondition(partialMatchEvents);
//        }

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
//        long time = System.nanoTime();
        partialMatchEvents.add(event);
        if (isEventCompatibleWithPartialMatch(match, partialMatchEvents, event)) {
//            windowverifyTime += System.nanoTime() - time;
            long time = System.nanoTime();
            sendToNextState(match.createNewPartialMatchWithEvent(event));
            sendMatchingTime += System.nanoTime() - time;
        }
//        else {
//            windowverifyTime += System.nanoTime() - time;
//        }
//        time = System.nanoTime();
        partialMatchEvents.remove(partialMatchEvents.size() - 1);
//        actualCalcTime += System.nanoTime() - time;
    }
    private int batchSize = 0;
    public static final int maxBatchSize = 100;
    private List<Match> partialMatchesBatch = new ArrayList<>();
    protected void sendToNextState(Match newPartialMatchWithEvent) {
        batchSize++;
        partialMatchesBatch.add(newPartialMatchWithEvent);
        sliceTime++;
        if (batchSize == maxBatchSize) {
            conditionTime++;
            batchSize = 0;
            List<ParallelQueue<Match>> matchesQueues = dataStorage.getNextStateOutput();
//            long time = System.nanoTime();
            for (ParallelQueue<Match> queue : matchesQueues) {
                queue.put(partialMatchesBatch, id);
            }
//            sendMatchingTime += System.nanoTime() - time;
            partialMatchesBatch.clear();
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

    protected abstract long sizeOfElement();

    public long size() {
        return sizeOfElement() * maxElements;
    }

    public void forwardIncompleteBatch() {
        if (partialMatchesBatch.isEmpty()) {
            return;
        }
        List<ParallelQueue<Match>> matchesQueues = dataStorage.getNextStateOutput();
        for (ParallelQueue<Match> queue : matchesQueues) {
            queue.put(partialMatchesBatch, id);
        }
    }
}
