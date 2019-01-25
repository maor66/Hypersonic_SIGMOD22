package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.List;

public class AcceptorBufferWorker extends BufferWorker {
    public AcceptorBufferWorker(TypedNFAState eventState) {
        super(eventState);
    }

    @Override
    protected ContainsEvent takeNextInput() throws InterruptedException {
        return null;
    }

    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<ContainsEvent> oppositeBufferList) {
        //Do nothing
    }

    @Override
    protected boolean isBufferSorted() {
        return false;
    }
}
