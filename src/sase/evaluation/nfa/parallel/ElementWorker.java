package sase.evaluation.nfa.parallel;

import sase.base.AggregatedEvent;
import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

    public long lastCriteriaTimestamp = 0;
    private int lastRemovedNumber= 0;
    private  int currentBackoff = 0;
    private  int backoffStep = 1;

    public ElementWorker(TypedNFAState eventState,
                         List<ThreadContainers> oppositeBuffers)
    {
        this.eventState = eventState;
        this.oppositeBuffers = oppositeBuffers;
        transition = (LazyTransition) eventState.getActualNextTransition();
    }

    public void handleElement(ContainsEvent newElement, List<BufferWorker> workersNeededToFinish, ParallelQueue<? extends  ContainsEvent> input) {
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
    }

    protected abstract boolean oppositeTaskNotFinished(List<BufferWorker> workersNeededToFinish);

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
        if (event.isAggregatedEvent()) { //Aggregated event so need to check if the latest event is too "late" for the events in the match
            Event removed = partialMatchEvents.remove(partialMatchEvents.size() - 1); // Remove last as it must be the aggregate
            partialMatchEvents.add(event);
            boolean isTemporalConditionValid = transition.verifySecondStepCondition(partialMatchEvents);
            // restoring the partial match events
            partialMatchEvents.remove(partialMatchEvents.size() - 1);
            partialMatchEvents.add(removed);
            if (!isTemporalConditionValid) {
                return false;
            }
        }
        boolean b =  transition.verifyFirstStepCondition(partialMatchEvents);
        actualCalcTime += System.nanoTime() - time;
        numberOfOppositeItems++;
        if (b) {
            time = System.nanoTime();
            boolean s =  transition.verifySecondStepCondition(partialMatchEvents);
            conditionTime += System.nanoTime() - time;
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

    private Event getLastListElement(List<Event> list) {
        return list.get(list.size() - 1);
    }

    protected void checkAndSendToNextState(Event event, List<Event> partialMatchEvents, Match match) {
//        long time = System.nanoTime();

        Event removedAggregate = null;
        if(getLastListElement(partialMatchEvents).isAggregatedEvent() && event.isAggregatedEvent()) {
            AggregatedEvent aggEvent = (AggregatedEvent) getLastListElement(partialMatchEvents).clone();
            if (aggEvent.containsLaterEvent(event)) {
                return;
            }
            if (!aggEvent.isPrimitiveNotInAggregate(((AggregatedEvent)event).getPrimitiveEvents().get(0))) {
                return;
            }
            aggEvent.addAggregatedEvent((AggregatedEvent) event);
            removedAggregate = partialMatchEvents.remove(partialMatchEvents.size() - 1);
            partialMatchEvents.add(aggEvent);
        }
        else {
            partialMatchEvents.add(event);
        }
//        if (isEventShouldAggregate(event) && isEventTypeAlreadyInMatch(partialMatchEvents, event)) {
//            AggregatedEvent aggregatedEvent = (AggregatedEvent) partialMatchEvents.get(partialMatchEvents.size() - 1);
//            if (aggregatedEvent.isPrimitiveNotInAggregate(((AggregatedEvent)event).getPrimitiveEvents().get(0))) {
////                aggregatedEvent.addPrimitiveEvent(((AggregatedEvent) event).getPrimitiveEvents().get(0));
//                partialMatchEvents.add(aggregatedEvent);
//            }
//            else {
//                return;
//            }
//        }
//        else {
//            partialMatchEvents.add(event);
//        }
        if (isEventCompatibleWithPartialMatch(match, partialMatchEvents, event)) {
            List<Event> primitiveEvents = new ArrayList<>(partialMatchEvents);
            sendToNextState(new Match(primitiveEvents), event.isAggregatedEvent());
            }
//            windowverifyTime += System.nanoTime() - time;
//            long time = System.nanoTime();

//            sendMatchingTime += System.nanoTime() - time;

//        else {
//            windowverifyTime += System.nanoTime() - time;
//        }
//        time = System.nanoTime();
        partialMatchEvents.remove(partialMatchEvents.size() - 1);
        if (removedAggregate != null) {
            partialMatchEvents.add(removedAggregate); //The previous remove discards the whole aggregated event, so we need to add the "original" part of it
        }
//        actualCalcTime += System.nanoTime() - time;
    }

    private boolean isEventShouldAggregate(Event event) {
        return event.isAggregatedEvent();
    }

    private boolean isEventTypeAlreadyInMatch(List<Event> partialMatchEvents, Event event) {
        for (Event eventInPartialMatch : partialMatchEvents) {
            if (event.getType() == eventInPartialMatch.getType()) {
                return true;
            }
        }
        return false;
    }

    protected void sendToNextState(Match newPartialMatchWithEvent, boolean isAggregatedEvent) {

        List<ParallelQueue<Match>> matchesQueue = dataStorage.getMatchOutputs();
        long time = System.nanoTime();
        for (ParallelQueue<Match> output : matchesQueue) {
            if (isAggregatedEvent) {
                output.putAtHead(newPartialMatchWithEvent);
            }
            else {
                output.put(newPartialMatchWithEvent);
            }
        }
        windowverifyTime += System.nanoTime() - time;
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
