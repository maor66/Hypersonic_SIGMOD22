package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.config.MainConfig;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;
import sase.pattern.EventTypesManager;

import java.util.ArrayList;
import java.util.List;

public class PartialMatchWorker extends ElementWorker {
    private int eventsInMatch = 0;

    public PartialMatchWorker(TypedNFAState eventState, List<ThreadContainers> eventOppositeBuffers) {
        super(eventState, eventOppositeBuffers);
    }

    @Override
    protected boolean oppositeTaskNotFinished(List<BufferWorker> workersNeededToFinish) {
        return workersNeededToFinish.isEmpty();
//        return true;
    }

    @Override
    protected boolean isBufferSorted() {
        return false;
    }

    @Override
    protected ContainsEvent iterateOnSubList(ContainsEvent newElement, List<ContainsEvent> bufferSubList) {
        Match match = (Match) newElement;
        recordMatchSize(match);
        List<Event> partialMatchEvents = new ArrayList<>(match.getPrimitiveEvents());
        List<Event> actualEvents = (List<Event>) (List<?>) bufferSubList;
        if (actualEvents.isEmpty()) {
//            sliceTime += System.nanoTime() - time;
            return null;
        }
        Event latestEventInSubList = actualEvents.get(actualEvents.size() - 1);

        long time = System.nanoTime();
        if (MainConfig.isLazyEvaluation) { // Have to use the actual getSlice
            actualEvents = getSlice(actualEvents, (Match) newElement, eventState);
            sliceTimeActual += System.nanoTime() - time;

            for (Event event : actualEvents) {
                checkAndSendToNextState(event, partialMatchEvents, match);
            }
        }
        else { //Eager ordering so can use just the upper limit and break when events in the IB are old enough
            int upperIndex = getIndexWithClosestValue(actualEvents, match.getEarliestTimestamp() + dataStorage.getTimeWindow(), false, false);
            sliceTimeActual += System.nanoTime() - time;

            for (int i = upperIndex; i-- > 0 ;) {
                Event event = actualEvents.get(i);
                if (event.getSequenceNumber() < match.getLatestEvent().getSequenceNumber()) {
                    return latestEventInSubList;
                }
                checkAndSendToNextState(event, partialMatchEvents, match);
            }
        }
        return latestEventInSubList;
    }

    private void recordMatchSize(Match match) {
        if (eventsInMatch != 0) {
            return;
        }
        eventsInMatch = match.getEventsInMatch();
    }

    @Override
    protected long sizeOfElement() {
        return eventsInMatch * EventTypesManager.getInstance().getAverageEventSize();
    }

    public int getIndexWithClosestValue(List<Event> events, long desiredValue, boolean getLower, boolean compareBySequence) {
        int minIndex = 0, maxIndex = events.size() - 1;
        int midIndex;
        long currValue;
        while (maxIndex >= minIndex)
        {
            midIndex = (maxIndex + minIndex) / 2;
            Event currEvent = events.get(midIndex);
            currValue = (compareBySequence) ? currEvent.getSequenceNumber() : currEvent.getTimestamp();
            if (currValue == desiredValue)
                return (compareBySequence) ? midIndex : findOutermostIndexWithSameKey(events, desiredValue, getLower, midIndex);
            if (currValue < desiredValue)
                minIndex = midIndex + 1;
            else
                maxIndex = midIndex - 1;
        }
        return minIndex;
    }

    private int findOutermostIndexWithSameKey(List<Event> events, long desiredTimestamp, boolean getLower, int midIndex) {
        if (midIndex == 0 || midIndex == events.size() - 1) {
            return midIndex + ((getLower) ? 0 : 1);
        }
        while (desiredTimestamp == events.get(midIndex).getTimestamp()) {
            if (getLower) {
                midIndex--;
            }
            else {
                midIndex++;
            }
        }
        return midIndex +  ((getLower) ? 1 : 0);
    }

    private LazyTransition getActualNextTransition(NFAState state) {
        for (Transition transition : state.getOutgoingTransitions()) {
            if (transition.getAction() == Transition.Action.TAKE) {
                return (LazyTransition) transition;
            }
        }
        throw new RuntimeException("No outgoing TAKE transition");
    }

    private List<Event> getSlice(List<Event> events, Match partialMatch, TypedNFAState eventState) {
        if (events.isEmpty()) {
            return events;
        }

        Event lowerBoundEvent = getActualNextTransition(eventState).getActualPrecedingEvent(partialMatch.getPrimitiveEvents());
        Event upperBoundEvent = getActualNextTransition(eventState).getActualSucceedingEvent(partialMatch.getPrimitiveEvents());
        int lowerIndex, upperIndex;

        if (lowerBoundEvent == null) {
            lowerIndex = getIndexWithClosestValue(events,partialMatch.getLatestEventTimestamp() - dataStorage.getTimeWindow(), true, false);
        }
        else {
            lowerIndex = getIndexWithClosestValue(events, lowerBoundEvent.getSequenceNumber(), true, true);
        }

        if (upperBoundEvent == null) {
            upperIndex = getIndexWithClosestValue(events, partialMatch.getEarliestTimestamp()+ dataStorage.getTimeWindow(), false, false);
        }
        else {
            upperIndex = getIndexWithClosestValue(events, upperBoundEvent.getSequenceNumber(), false, true);
        }
        return events.subList(lowerIndex, upperIndex);
    }
}
