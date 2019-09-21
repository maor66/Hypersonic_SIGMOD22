package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.EfficientInputBuffer;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;

public class MatchBufferWorker extends BufferWorker {
    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<List<ContainsEvent>> oppositeBufferList) {
        for (List<ContainsEvent> eventsList : oppositeBufferList) {
            List<Event> actualEvents = (List<Event>) (List<?>) eventsList;
            if (actualEvents.isEmpty()) {
                return;
            }
            long time = System.nanoTime();
            actualEvents = getSlice(actualEvents, (Match) newElement, eventState);
            sliceTime += System.nanoTime() - time;
            List<Match> m = new ArrayList<>(Match.asList((Match)newElement));
            actualCalcTime += System.nanoTime() - time;
            tryToAddMatchesWithEvents(m, actualEvents);
        }
    }

    public int getIndexWithClosestValue(List<Event> events, long desiredValue, boolean getLower, boolean comapreBySequence) {
        int minIndex = 0, maxIndex = events.size() - 1;
        int midIndex;
        long currValue;
        while (maxIndex >= minIndex)
        {
            midIndex = (maxIndex + minIndex) / 2;
            Event currEvent = events.get(midIndex);
            currValue = (comapreBySequence) ? currEvent.getSequenceNumber() : currEvent.getTimestamp();
            if (currValue == desiredValue)
                return (comapreBySequence) ? midIndex : findOutermostIndexWithSameKey(events, desiredValue, getLower, midIndex);
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

    private List<Event> getSlice(List<Event> events, Match partialMatch, TypedNFAState eventState) {
        if (events.isEmpty()) {
            return events;
        }

        //TODO: getting too much results, probably not calculating correctly
        Event lowerBoundEvent = getActualNextTransition(eventState).getActualPrecedingEvent(partialMatch.getPrimitiveEvents());
        Event upperBoundEvent = getActualNextTransition(eventState).getActualSucceedingEvent(partialMatch.getPrimitiveEvents());
        int lowerIndex, upperIndex;

        //Can use the actual getSlice as now the list is sorted (getting it from each IB separately)
//        EfficientInputBuffer EIB = new EfficientInputBuffer(eventState.getEventType(), events.size());
//        EIB.storeAllWithoutCopy(events);
        if (lowerBoundEvent == null) {
//            lowerBoundEvent = getBoundByWindow(events, partialMatch, true);
//            lowerBoundEvent = events.get(Collections.binarySearch(events, new Event(partialMatch.getLatestEvent().getTimestamp() - dataStorage.getTimeWindow()),c));
//            lowerBoundEvent = events.get(EIB.getIndexWithClosestTimestampWindow(eventState.getEventType(),partialMatch.getLatestEventTimestamp() - dataStorage.getTimeWindow(), false));
            lowerIndex = getIndexWithClosestValue(events,partialMatch.getLatestEventTimestamp() - dataStorage.getTimeWindow(), true, false);
        }
        else {
            lowerIndex = getIndexWithClosestValue(events, lowerBoundEvent.getSequenceNumber(), true, true);
        }

        if (upperBoundEvent == null) {
//            upperBoundEvent = getBoundByWindow(events, partialMatch, false);
//            upperBoundEvent = events.get(EIB.getIndexWithClosestTimestampWindow(eventState.getEventType(),partialMatch.getEarliestTimestamp()+ dataStorage.getTimeWindow(), false));
            upperIndex = getIndexWithClosestValue(events, partialMatch.getEarliestTimestamp()+ dataStorage.getTimeWindow(), false, false);
        }
        else {
            upperIndex = getIndexWithClosestValue(events, upperBoundEvent.getSequenceNumber(), false, true);
        }
        return events.subList(lowerIndex, upperIndex);
//        return EIB.getSlice(eventState.getEventType(), lowerBoundEvent, upperBoundEvent);
    }

    private Event getBoundByWindow(List<Event> events, Match partialMatch, boolean isLowerBound) {
        long boundTimestamp = (isLowerBound) ? partialMatch.getLatestEventTimestamp() : partialMatch.getEarliestTimestamp();
        int direction = (isLowerBound) ? 1 : -1;
        boundTimestamp += dataStorage.getTimeWindow() * direction * -1;
        int startpos = (isLowerBound) ? 0 : events.size()-1;
        while (direction * boundTimestamp > direction * events.get(startpos).getTimestamp()) {
            startpos += direction;
        }
        return (startpos !=0 && startpos != events.size()-1) ? events.get(startpos+ direction*-1) : null;

    }

    private LazyTransition getActualNextTransition(NFAState state) {
        for (Transition transition : state.getOutgoingTransitions()) {
            if (transition.getAction() == Transition.Action.TAKE) {
                return (LazyTransition) transition;
            }
        }
        throw new RuntimeException("No outgoing TAKE transition");
    }

    @Override
    protected boolean isBufferSorted() {
        return false;
    }
    public MatchBufferWorker(TypedNFAState eventState, int finisherInputsToShutdown, int numberOfFinisherInputsToSend) {
        super(eventState, finisherInputsToShutdown, numberOfFinisherInputsToSend);
        threadName = "MatchBufferWorker "+ eventState.getName();
    }}
