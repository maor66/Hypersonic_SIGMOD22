package sase.evaluation.nfa.parallel;

import sase.base.Event;
import sase.base.EventType;

import java.util.List;

public class FinishBarrier {

    public FinishBarrier(List<EventType> evaluationOrder) {

    }

    public void addBarrierLevel(EventType eventType, int workerCount) {

    }

    public void notifyWorkerFinished(EventType eventType) {

    }

    public boolean hasFinishedWithAllPreviousStates(EventType eventType) {

    }

    public List<EventType> getAllFinishedState() {
        throw new RuntimeException("Not implemented yet");
    }

    public void resetCounts() {

    }
}
