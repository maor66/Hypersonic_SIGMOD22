package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.simulator.Environment;
import sase.statistics.Statistics;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferWorker implements Runnable {

    String threadName;
    protected boolean canCreateMatches= true;
    public long actualCalcTime = 0;
    public long windowverifyTime = 0;
    public Thread thread;
    protected CopyOnWriteArrayList<BufferWorker> finishedWorkers;
    private CopyOnWriteArrayList<BufferWorker> finishedWithGroup;
    protected AtomicBoolean isMainFinished;
    protected ElementWorker primaryTask;
    protected ElementWorker secondaryTask;
    private ParallelQueue<? extends ContainsEvent> primaryInput;
    private ParallelQueue<? extends ContainsEvent> secondaryInput;
    private List<BufferWorker> workersNeededToFinish;

    public long numberOfPrimaryHandledItems = 0;
    public long numberOfSecondaryHandledItems = 0;
    public long numberOfOppositeItems = 0;
    public  long primaryIdleTime = 0;
    public  long secondaryIdleTime = 0;
    public long iteratingBufferTime = 0;
    public long sliceTime = 0;
    public long sliceTimeActual = 0;
    public long sendMatchingTime = 0;
    public long conditionTime = 0;
    public Long innerCondTime = 0L;
    public Long innerWindowTime = 0L;

private boolean primaryTakenOnce = false;
private boolean addedToGroupFinish =  false;

private int isPrimaryInputTakenLast = 1;

    public BufferWorker(TypedNFAState eventState,
                        ParallelQueue<? extends ContainsEvent> eventInput,
                        ParallelQueue<? extends ContainsEvent> partialMatchInput,
                        ThreadContainers threadContainer,
                        List<ThreadContainers> eventOppositeBuffers,
                        List<ThreadContainers> partialMatchOppositeBuffers,
                        CopyOnWriteArrayList<BufferWorker> finishedWorkers,
                        CopyOnWriteArrayList<BufferWorker> finishedWithGroup,
                        List<BufferWorker> workersNeededToFinish,
                        boolean isInputBufferWorker)
    {
        ElementWorker EventWorker = new EventWorker(eventState, partialMatchOppositeBuffers);
        ElementWorker partialMatchWorker = new PartialMatchWorker(eventState, eventOppositeBuffers);
        primaryTask = isInputBufferWorker ? EventWorker : partialMatchWorker;
        primaryTask.initializeDataStorage(threadContainer);
        secondaryTask = isInputBufferWorker ? partialMatchWorker : EventWorker;
        secondaryTask.initializeDataStorage(threadContainer.createClone());
        this.finishedWorkers = finishedWorkers;
        this.finishedWithGroup = finishedWithGroup;
        this.primaryInput = isInputBufferWorker ? eventInput : partialMatchInput;
        this.secondaryInput = isInputBufferWorker ? partialMatchInput : eventInput;
        this.workersNeededToFinish = workersNeededToFinish;
        threadName = isInputBufferWorker ?  "InputBufferWorker" + eventState.getName() :"MatchBufferWorker "+ eventState.getName();
    }

    public BufferWorker() //Creates dummy BufferWorker
    {}

    @Override
    public void run() {
        thread = Thread.currentThread();
        thread.setName(threadName + " " + Thread.currentThread().getName());
        while (true) {
            ContainsEvent newElement;
            ElementWorker taskUsed = primaryTask;
//            long time = System.nanoTime();
            newElement = takePrimaryInput();
//            sliceTime += System.nanoTime() - time;
            if (newElement == null) {
                newElement = takeSecondaryInput(); // Check if the secondaryTask queue has an item
                if (newElement == null) {
                    if (isPreviousStateFinished(finishedWithGroup) && !addedToGroupFinish) {
                        finishedWithGroup.add(this);
                        addedToGroupFinish = true;
                    }
                    if (isPreviousStateFinished(finishedWorkers)) {
                        finishRun();
                        return;
                    } else {
                        continue;
                    }
                }
                numberOfSecondaryHandledItems++;
                isPrimaryInputTakenLast = 0;
                primaryTask.updateOppositeWorkers(secondaryTask);
                taskUsed = secondaryTask; // The secondaryTask queue had an item so it is the task used for adding, iterating and removing
            }
            else {
                isPrimaryInputTakenLast = 1;
                numberOfPrimaryHandledItems++;
            }
//            else { //Primary is used
//                secondaryTask.updateOppositeWorkers(primaryTask);
//            }

//            long time = System.nanoTime();
//            try {
//                if ((numberOfPrimaryHandledItems + numberOfSecondaryHandledItems) % 500== 0) {
//                    Thread.sleep(20);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            taskUsed.handleElement(newElement, finishedWorkers, (isPrimaryInputTakenLast==1) ? secondaryInput : primaryInput );
//            ContainsEvent removingCriteria = null;
//            long latestTimeStamp = Long.MIN_VALUE;
//            for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
//               ContainsEvent ce = iterateOnSubList(newElement, worker.getDataStorage().getBufferSubListWithReadLock());
//               worker.getDataStorage().releaseReadLock();
//               if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
//                   latestTimeStamp = ce.getEarliestTimestamp();
//                   removingCriteria = ce;
//               }
//            }
//
//            List<List<ContainsEvent>> oppositeBufferList = getOppositeBufferList();
//            idleTime += System.nanoTime() - time;
//            if (oppositeBufferList.isEmpty()) {
//                continue;
//            }
//            numberOfHandledItems++;
//            time = System.nanoTime();
//            ContainsEvent removingCriteria = iterateOnOppositeBuffer(newElement, oppositeBufferList);
//            iteratingBufferTime += System.nanoTime() - time;
//            if (removingCriteria != null) {
//                time = System.nanoTime();
//                dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted(), removingCriteria);
//                windowverifyTime += System.nanoTime() -time;
//            }
        }
    }

    private ContainsEvent takeSecondaryInput() {
//        long time = System.nanoTime();
        if (!primaryTakenOnce || secondaryInput.isEmpty()) {
//            secondaryIdleTime += System.nanoTime() - time;
            return null;
        }
        ContainsEvent ce = takeNextInput(secondaryInput, 50);
//        secondaryIdleTime += System.nanoTime() - time;
        return ce;
    }

    private ContainsEvent takePrimaryInput() {
//        long time = System.nanoTime();
        if (primaryInput.isEmpty()) {
//            primaryIdleTime += System.nanoTime() - time;
            return null;
        }
        primaryTakenOnce = true;

        ContainsEvent ce  = takeNextInput(primaryInput, 0);
//        primaryIdleTime += System.nanoTime() - time;
        return ce;
    }


    protected ContainsEvent takeNextInput(ParallelQueue<? extends ContainsEvent> input, int timeoutInMilis)  {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        return input.poll(timeoutInMilis, TimeUnit.MILLISECONDS);
//            return input.take();
    }
    private void finishRun() {
        finishedWorkers.add(this);
        System.out.println("Buffer Worker - " + Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " Handled " + numberOfPrimaryHandledItems + " primary items " +
                + numberOfSecondaryHandledItems + " Handled secondary items.  Primary idle time " + primaryIdleTime/1000000 + " Secondary Idle time "+ secondaryIdleTime/ 1000000);
        primaryTask.finishRun();
        secondaryTask.finishRun();
    }

    protected boolean isPreviousStateFinished(CopyOnWriteArrayList<BufferWorker> finished) {
        for (BufferWorker worker: workersNeededToFinish) {
            if (finished.indexOf(worker) == -1) {
                return false;
            }
        }
        return true;
    }

    private ContainsEvent getReleventRemovingCriteria(List<ContainsEvent> oppositeBufferList)
    {
        //TODO: can be optimized because we already go over the MB when looking for matches, so its possible to calculate latest match at that stage
        long latestEarliestTimeStamp = Long.MIN_VALUE;
        ContainsEvent element = null;
        for (ContainsEvent ce : oppositeBufferList) {
            if (ce.getEarliestTimestamp() > latestEarliestTimeStamp) {
                element = ce;
                latestEarliestTimeStamp = ce.getEarliestTimestamp();
            }
        }
        return element;
    }

    public void resetGroupFinish() {
        addedToGroupFinish = false;
    }

//    public void initializeOppositeWorkers(List<BufferWorker> primaryOppositeWorkers, List<BufferWorker> secondaryOppositeWorkers) {
//        List<ElementWorker> actualPrimaryOppositeWorkers = new ArrayList<>();
//        insertElementWorkers(primaryTask, primaryOppositeWorkers, actualPrimaryOppositeWorkers);
//        List<ElementWorker> actualSecondaryOppositeWorkers = new ArrayList<>();
//        insertElementWorkers(secondaryTask, secondaryOppositeWorkers, actualSecondaryOppositeWorkers);
//    }
//
//    private void insertElementWorkers(ElementWorker task, List<BufferWorker> oppositeWorkers, List<ElementWorker> actualOppositeWorkers) {
//        for (BufferWorker worker : oppositeWorkers) {
//            actualOppositeWorkers.add(worker.primaryTask);
//        }
//        task.initializeOppositeWorkers(actualOppositeWorkers);
//    }
}
//
//    protected List<List<ContainsEvent>> getOppositeBufferList()
//    {
//        List<List<ContainsEvent>> oppositeBuffer = new ArrayList<>();
//        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
//            if (finishedWorkers.indexOf(worker)==-1) {
////                oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
//                oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
//                }
//            else { //If the worker has finished its run, the sub buffer won't change so we don't have to copy it
//                oppositeBuffer.add(worker.getDataStorage().getBufferSubListAfterWorkerFinished());
//            }
//        }
//        return oppositeBuffer;
//    }


