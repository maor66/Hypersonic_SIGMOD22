package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class SplitDuplicateWorker implements Worker {

    protected ElementWorker primaryTask;
    protected ElementWorker secondaryTask;

    String threadName;

    private List<Worker> workersNeededToFinish;
    protected CopyOnWriteArrayList<Worker> finishedWorkers;
    private CopyOnWriteArrayList<Worker> finishedWithGroup;
    private boolean addedToGroupFinish =  false;

    private ParallelQueue<? extends ContainsEvent> primaryInput;
    private ParallelQueue<? extends ContainsEvent> secondaryInput;
    private ElementWorker taskUsed;

    public long numberOfPrimaryHandledItems = 0;
    public long numberOfSecondaryHandledItems = 0;
    public  long primaryIdleTime = 0;
    public  long secondaryIdleTime = 0;
    private long latestSecondary = 0;
    private long latestPrimary = 0;



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
        taskUsed = secondaryTask;
    }

    @Override
    public void resetGroupFinish() {
        addedToGroupFinish = false;
    }


    @Override
    public long size() {
        return primaryTask.size() + secondaryTask.size();
    }

    @Override
    public void run() {
        thread.setName(threadName + " " + Thread.currentThread().getName());
        while(true) {
            List<ContainsEvent> newBatch = takeInput();
            if (newBatch == null) {
                if (isPreviousStateFinished(finishedWithGroup) && !addedToGroupFinish) {
                    forwardIncompleteBatch();
                    finishedWithGroup.add(this);
                    addedToGroupFinish = true;
                }
                if (isPreviousStateFinished(finishedWorkers)) {
                    forwardIncompleteBatch();
                    printBufferSnapShot(threadName + " " + Thread.currentThread().getName());
                    finishRun();
                    return;
                } else {
                    continue;
                }
            }
            long latestTimestamp = Long.MIN_VALUE;
            for (ContainsEvent newElement : newBatch) {
                if (newElement.getTimestamp() > latestTimestamp) {
                    latestTimestamp = newElement.getTimestamp();
                }
                taskUsed.handleElement(newElement, null, null);
            }

            if (primaryTask == taskUsed) {
                if (latestPrimary < latestTimestamp) {
                    latestPrimary = latestTimestamp;
                }
            }
            else {
                if (latestSecondary < latestTimestamp) {
                    latestSecondary = latestTimestamp;
                }
            }
        }

    }

    private boolean inputPriorityCriteria() {
        return (taskUsed == secondaryTask);
    }

    private List<ContainsEvent> takeInput() {
        List<ContainsEvent> newBatch;
        newBatch = (inputPriorityCriteria()) ? takePrimaryInput() : takeSecondaryInput();
        if (newBatch == null) {
            newBatch =  (inputPriorityCriteria()) ? takeSecondaryInput() : takePrimaryInput();
        }
        return newBatch;
    }

    private List<ContainsEvent> takeSecondaryInput() {
//        long time = System.nanoTime();
        if (secondaryInput.isEmpty()) {
//            secondaryIdleTime += System.nanoTime() - time;
            return null;
        }
        taskUsed = secondaryTask;
        List<ContainsEvent> elementBatch = takeNextInput(secondaryInput, 0);
//        secondaryIdleTime += System.nanoTime() - time;
        if (elementBatch != null) {
            numberOfSecondaryHandledItems++;
        }
        return elementBatch;
    }

    private List<ContainsEvent> takePrimaryInput() {
//        long time = System.nanoTime();
        if (primaryInput.isEmpty()) {
//            primaryIdleTime += System.nanoTime() - time;
            return null;
        }
        taskUsed = primaryTask;
        List<ContainsEvent> elementBatch  = takeNextInput(primaryInput, 0);
//        primaryIdleTime += System.nanoTime() - time;
        if (elementBatch != null) {
            numberOfPrimaryHandledItems++;
        }
        return elementBatch;
    }

    protected List<ContainsEvent> takeNextInput(ParallelQueue<? extends ContainsEvent> input, int timeoutInMilis)  {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        return  (List<ContainsEvent>) input.poll(timeoutInMilis, TimeUnit.MILLISECONDS);
//            return input.take();
    }

    private void forwardIncompleteBatch() {
        primaryTask.forwardIncompleteBatch();
        secondaryTask.forwardIncompleteBatch();
    }

    private void finishRun() {
        finishedWorkers.add(this);
        System.out.println("Buffer Worker - " + Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " Handled " + numberOfPrimaryHandledItems + " primary items " +
                + numberOfSecondaryHandledItems + " Handled secondary items.  Primary idle time " + primaryIdleTime/1000000 + " Secondary Idle time "+ secondaryIdleTime/ 1000000);
        primaryTask.finishRun();
        secondaryTask.finishRun();
    }

    private void printBufferSnapShot(String threadName)
    {
        System.out.println(threadName  + " " +  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                " latest primary timestamp " + latestPrimary +
                " latest secondary timestamp " + latestSecondary +
                " primary buffer size " + primaryTask.currentBufferSize +
                " secondary buffer size " + secondaryTask.currentBufferSize +
                " Handled primary items " + numberOfPrimaryHandledItems + " Secondary Items " + numberOfSecondaryHandledItems);
    }

    protected boolean isPreviousStateFinished(CopyOnWriteArrayList<Worker> finished) {
        for (Worker worker: workersNeededToFinish) {
            if (finished.indexOf(worker) == -1) {
                return false;
            }
        }
        return true;
    }

}
