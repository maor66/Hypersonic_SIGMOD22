package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BufferWorker implements Runnable {
    ThreadContainers dataStorage;
    TypedNFAState eventState;
    public ThreadContainers getDataStorage() {
        return dataStorage;
    }

    public BufferWorker(TypedNFAState eventState)
    {
        this.eventState = eventState;
    }

    @Override
    public void run() {
        while (true) {
            ContainsEvent newElement = null;
            try {
                newElement = takeNextInput();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ContainsEvent removingCriteria = dataStorage.getRemovingData().poll();
            if (removingCriteria != null) {
                dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted());
            }

            if (newElement.isLastInput()) {
                System.err.println("Finisher input");
                return; //TODO: how to end task?
            }
//            if (dataStorage.getEventType() != newEvent.getType()) {
//                throw new RuntimeException("Got wrong event type in Input Buffer");
//            }
            dataStorage.addEventToOwnBuffer(newElement);
            iterateOnOppositeBuffer(newElement);
        }
    }

    protected abstract ContainsEvent takeNextInput() throws InterruptedException;
    protected ContainsEvent takeElementFromQueue(BlockingQueue<ContainsEvent> queue) throws InterruptedException {
        return queue.take();
    }

    protected List<ContainsEvent> getOppositeBufferList()
    {
        List<ContainsEvent> oppositeBuffer = new ArrayList<>();
        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
            oppositeBuffer.addAll(worker.getDataStorage().getBufferSubListWithOptimisticLock());
        }
        return oppositeBuffer;
    }

    protected boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness

        return verifyTimeWindowConstraint(partialMatch, event) && getActualNextTransition(eventState).verifyConditionWithTemporalConditionFirst( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
                Stream.concat(partialMatch.getPrimitiveEvents().stream(),List.of(event).stream()).collect(Collectors.toList())); //Combining two lists
    }

    private LazyTransition getActualNextTransition(NFAState state)
    {
        for (Transition transition: state.getOutgoingTransitions()) {
            if (transition.getAction() == Transition.Action.TAKE) {
                return (LazyTransition)transition;
            }
        }
        throw new RuntimeException("No outgoing TAKE transition");
    }

    private boolean verifyTimeWindowConstraint(Match partialMatch, Event event) {
        return (partialMatch.getLatestEventTimestamp() <= event.getTimestamp() + dataStorage.getTimeWindow()) &&
                (partialMatch.getEarliestEvent() + dataStorage.getTimeWindow() >= event.getTimestamp());

    }

    protected void sendToNextState(Match newPartialMatchWithEvent) {
        BlockingQueue<Match> matchesQueue = dataStorage.getNextStateWorkers().get((dataStorage.getNextStateWorkers().keySet().toArray()[dataStorage.getNextWorkerToSendTo()]));
        dataStorage.updateNextWorkerToSendTo();
        try {
            matchesQueue.put(newPartialMatchWithEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void tryToAddMatchesWithEvents(List<Match> matches, List<Event> events)
    {
        for (Match partialMatch : matches) {
            for (Event event : events) {
                if (isEventCompatibleWithPartialMatch(eventState, partialMatch, event)) {
                    sendToNextState(partialMatch.createNewPartialMatchWithEvent(event));
                }
            }
        }
    }
    public void initializeDataStorage(ThreadContainers dataStorage) {
        this.dataStorage = dataStorage;
    }

    protected abstract void iterateOnOppositeBuffer(ContainsEvent newElement);

    protected abstract boolean isBufferSorted();
}
