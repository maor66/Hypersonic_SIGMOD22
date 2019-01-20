package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.ArrayList;
import java.util.List;

public class MatchBufferWorker extends BufferWorker {
    private int cyclicInputQueueIndex = -1;
    @Override
    protected ContainsEvent takeNextInput() throws InterruptedException {
        int numberOfInputQueues = dataStorage.getInputs().size();
        cyclicInputQueueIndex = (cyclicInputQueueIndex + 1) % numberOfInputQueues;
        return takeElementFromQueue(dataStorage.getInputs().get(cyclicInputQueueIndex));
    }

    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement) {
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
