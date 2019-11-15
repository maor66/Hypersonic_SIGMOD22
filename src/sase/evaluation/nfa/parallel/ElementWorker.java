package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class ElementWorker {
    ThreadContainers dataStorage;
    private LazyTransition transition;
    TypedNFAState eventState;
    private List<ElementWorker> oppositeWorkers;
    private boolean isSecondaryAddToList = false;

    public ElementWorker(TypedNFAState eventState,
                         BlockingQueue<Match> nextStateOutput,
                         long timeWindow) {
        this.eventState = eventState;
        transition = (LazyTransition) eventState.getActualNextTransition();
        dataStorage = new ThreadContainers(nextStateOutput, eventState.getEventType(), timeWindow);
    }

    void initializeOppositeWorkers(List<ElementWorker> oppositeWorkers) {
        this.oppositeWorkers = oppositeWorkers;
    }



    public void handleElement(ContainsEvent newElement) {
        ContainsEvent removingCriteria = null;
        long latestTimeStamp = Long.MIN_VALUE;
        dataStorage.addEventToOwnBuffer(newElement);
        Iterator<ElementWorker> iterator = oppositeWorkers.iterator();
        while (iterator.hasNext()) {
            ElementWorker worker = iterator.next();
            ContainsEvent ce = iterateOnSubList(newElement, worker.dataStorage.getBufferSubListWithReadLock());
            worker.dataStorage.releaseReadLock();
            if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
                latestTimeStamp = ce.getEarliestTimestamp();
                removingCriteria = ce;
            }
        }
        if (removingCriteria != null) {
            dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted(), removingCriteria);
        }
    }

    protected abstract boolean isBufferSorted();


    protected boolean isEventCompatibleWithPartialMatch(Match partialMatch, List<Event> partialMatchEvents, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness
        long time = System.nanoTime();
        boolean b =  transition.verifyCondition(partialMatchEvents);
//        actualCalcTime += System.nanoTime() - time;
//        numberOfOppositeItems++;
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
        partialMatchEvents.add(event);
        if (isEventCompatibleWithPartialMatch(match, partialMatchEvents, event)) {
//            long time = System.nanoTime();
            sendToNextState(match.createNewPartialMatchWithEvent(event));
//            sendMatchingTime += System.nanoTime() - time;
        }
        partialMatchEvents.remove(partialMatchEvents.size() - 1);
    }

    protected void sendToNextState(Match newPartialMatchWithEvent) {

        BlockingQueue<Match> matchesQueue = dataStorage.getNextStateOutput();
        try {
            matchesQueue.put(newPartialMatchWithEvent);
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
            oppositeWorkers.add(secondaryTask);
            isSecondaryAddToList = true;
        }
    }
}
