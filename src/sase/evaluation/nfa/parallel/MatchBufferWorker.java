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

public class MatchBufferWorker extends BufferWorker {
    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<ContainsEvent> oppositeBufferList) {
        List<ContainsEvent> events = getOppositeBufferList();
        if (events.isEmpty()) {
            return;
        }
        List<Event> actualEvents = (List<Event>)(List<?>) events;
        tryToAddMatchesWithEvents(new ArrayList<>(List.of((Match)newElement)), getSlice(actualEvents, (Match) newElement, eventState));
//        tryToAddMatchesWithEvents(new ArrayList<>(List.of((Match)newElement)), actualEvents);
    }

    private List<Event> getSlice(List<Event> events, Match partialMatch, TypedNFAState eventState) {
        if (events.isEmpty()) {
            return events;
        }
        //TODO: getting too much results, probably not calculating correctly
        Event lowerBoundEvent = getActualNextTransition(eventState).getActualPrecedingEvent(partialMatch.getPrimitiveEvents());
        Event upperBoundEvent = getActualNextTransition(eventState).getActualSucceedingEvent(partialMatch.getPrimitiveEvents());
        long lowerBoundSequenceNumber = (lowerBoundEvent != null) ? lowerBoundEvent.getSequenceNumber() : 0;
        long upperBoundSequenceNumber = (upperBoundEvent != null) ? upperBoundEvent.getSequenceNumber() : Long.MAX_VALUE;
//        System.out.println("lower "+ lowerBoundEvent);
//        System.out.println("upper "+ upperBoundEvent);
        events.removeIf(event -> event.getSequenceNumber() < lowerBoundSequenceNumber || event.getSequenceNumber() > upperBoundSequenceNumber);
        return events;
        //Cannot use getSlice since IB is not sorted, must go over all events one-by-one
        //TODO: is it possible to use getSlice for performance? maybe sorting while inserting?

//        EfficientInputBuffer EIB = new EfficientInputBuffer(new ArrayList<>(List.of((eventState.getEventType()))), getTimeWindow());
//        EIB.storeAll(events);
//        return EIB.getSlice(eventState.getEventType(), lowerBoundEvent, upperBoundEvent);
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
    public MatchBufferWorker(TypedNFAState eventState) {
        super(eventState);
    }}
