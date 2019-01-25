package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.ArrayList;
import java.util.List;

public class MatchBufferWorker extends BufferWorker {
    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<ContainsEvent> oppositeBufferList) {
        List<ContainsEvent> events = getOppositeBufferList();
        List<Event> actualEvents = (List<Event>)(List<?>) events;
        tryToAddMatchesWithEvents(new ArrayList<>(List.of((Match)newElement)), actualEvents);
    }

    @Override
    protected boolean isBufferSorted() {
        return false;
    }
    public MatchBufferWorker(TypedNFAState eventState) {
        super(eventState);
    }}
