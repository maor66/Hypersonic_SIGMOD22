package sase.evaluation.nfa.parallel;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SplitDuplicateWorker implements Worker {

    protected ElementWorker primaryTask;
    protected ElementWorker secondaryTask;

    public SplitDuplicateWorker(TypedNFAState eventState,
                                ParallelQueue<Event> eventInput,
                                ParallelQueue<Match> partialMatchInput,
                                ThreadContainers threadContainers,
                                CopyOnWriteArrayList<Worker> finishedWorkers,
                                CopyOnWriteArrayList<Worker> finishedWithGroup,
                                List<Worker> workersNeededToFinish) {
        ThreadContainers containerClone = threadContainers.createClone();
        primaryTask = new EventWorker(eventState, Collections.singletonList(containerClone));
        primaryTask.initializeDataStorage(threadContainers);
        secondaryTask = new PartialMatchWorker(eventState, Collections.singletonList(threadContainers));
        secondaryTask.initializeDataStorage(containerClone);
        this.finishedWorkers = finishedWorkers;
        this.finishedWithGroup = finishedWithGroup;
        this.primaryInput =  eventInput;
        this.secondaryInput = partialMatchInput;
        this.workersNeededToFinish = workersNeededToFinish;
        threadName = "SplitDuplicateWorker" + eventState.getName();
    }

    @Override
    public void resetGroupFinish() {

    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void run() {

    }
}
