package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.EfficientInputBuffer;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchBufferWorker extends BufferWorker {
    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<List<ContainsEvent>> oppositeBufferList) {
        for (List<ContainsEvent> eventsList : oppositeBufferList) {
            List<Event> actualEvents = (List<Event>) (List<?>) eventsList;
            if (actualEvents.isEmpty()) {
                return;
            }
            actualEvents = getSlice(actualEvents, (Match) newElement, eventState);
            tryToAddMatchesWithEvents(new ArrayList<>(List.of((Match) newElement)), actualEvents);
        }
    }

    private List<Event> getSlice(List<Event> events, Match partialMatch, TypedNFAState eventState) {
        if (events.isEmpty()) {
            return events;
        }
        //TODO: getting too much results, probably not calculating correctly
        Event lowerBoundEvent = getActualNextTransition(eventState).getActualPrecedingEvent(partialMatch.getPrimitiveEvents());
        Event upperBoundEvent = getActualNextTransition(eventState).getActualSucceedingEvent(partialMatch.getPrimitiveEvents());

        //Can use the actual getSlice as now the list is sorted (getting it from each IB separately)
        EfficientInputBuffer EIB = new EfficientInputBuffer(new ArrayList<>(List.of((eventState.getEventType()))), 0); //TODO: Time window doesn't matter for this specific use but should pay attention to this
        EIB.storeAllWithoutCopy(events);
        return EIB.getSlice(eventState.getEventType(), lowerBoundEvent, upperBoundEvent);
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
