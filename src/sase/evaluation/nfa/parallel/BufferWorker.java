package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.simulator.Environment;
import sase.statistics.Statistics;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferWorker implements Runnable {

    String threadName;
    protected boolean canCreateMatches= true;
    public long actualCalcTime = 0;
    public long windowverifyTime = 0;
    public Thread thread;
    protected final CopyOnWriteArrayList<BufferWorker> finishedWorkers;
    protected AtomicBoolean isMainFinished;
    protected ElementWorker primaryTask;
    protected ElementWorker secondaryTask;
    private BlockingQueue<? extends ContainsEvent> primaryInput;
    private BlockingQueue<? extends ContainsEvent> secondaryInput;
    private List<BufferWorker> workersNeededToFinish;

    public long numberOfHandledItems = 0;
    public long numberOfOppositeItems = 0;
    public  long idleTime = 0;
    public long iteratingBufferTime = 0;
    public long sliceTime = 0;
    public long sliceTimeActual = 0;
    public long sendMatchingTime = 0;
    public long conditionTime = 0;
    public Long innerCondTime = 0L;
    public Long innerWindowTime = 0L;


    public BufferWorker(TypedNFAState eventState,
                        BlockingQueue<? extends ContainsEvent> eventInput,
                        BlockingQueue<? extends ContainsEvent> partialMatchInput,
                        CopyOnWriteArrayList<BufferWorker> finishedWorkers,
                        List<BufferWorker> workersNeededToFinish,
                        BlockingQueue<Match> nextStateOutput,
                        long timeWindow,
                        boolean isInputBufferWorker)
    {
        ElementWorker EventWorker = new EventWorker(eventState, oppositePartialMatchWorkers, nextStateOutput, timeWindow);
        ElementWorker partialMatchWorker = new PartialMatchWorker(eventState, oppositeEventWorkers, nextStateOutput, timeWindow);
        primaryTask = isInputBufferWorker ? EventWorker : partialMatchWorker;
        secondaryTask = isInputBufferWorker ? partialMatchWorker : EventWorker;
        this.finishedWorkers = finishedWorkers;
        this.primaryInput = isInputBufferWorker ? eventInput : partialMatchInput;
        this.secondaryInput = isInputBufferWorker ? partialMatchInput : eventInput;
        this.workersNeededToFinish = workersNeededToFinish;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        thread.setName(threadName + " " + Thread.currentThread().getName());
        while (true) {
            ContainsEvent newElement = null;
            ElementWorker taskUsed = primaryTask;
            long time = System.nanoTime();
            newElement = takePrimaryInput();
            sliceTime += System.nanoTime() - time;
            if (newElement == null) {
                newElement = takeSecondaryInput(); // Check if the secondaryTask queue has an item
                if (newElement == null) {
                    if (isPreviousStateFinished()) {
                        finishRun();
                        return;
                    } else {
                        continue;
                    }
                }
                primaryTask.updateOppositeWorkers(secondaryTask);
                taskUsed = secondaryTask; // The secondaryTask queue had an item so it is the task used for adding, iterating and removing
                sliceTimeActual += System.nanoTime() - time;
            }
            else { //Primary is used
                secondaryTask.updateOppositeWorkers(primaryTask);
            }

//            long time = System.nanoTime();
            taskUsed.handleElement(newElement);
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
        return takeNextInput(secondaryInput, 500);
    }

    private ContainsEvent takePrimaryInput() {
        return takeNextInput(primaryInput, 0);
    }


    protected ContainsEvent takeNextInput(BlockingQueue<ContainsEvent> input, int timeoutInMilis)  {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        try {
            return input.poll(timeoutInMilis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void finishRun() {
        finishedWorkers.add(this);
            System.out.println("Thread " + Thread.currentThread().getName() +" " +Thread.currentThread().getId() + " has finished at "  + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                    " Handled " + numberOfHandledItems + " items and compared to " + numberOfOppositeItems+" opposite items. Idle time "+ idleTime/1000000 + " Condition time " + conditionTime/1000000 +
                    " Iterating buffer time " + iteratingBufferTime/1000000+ " Slice time "+ sliceTime/1000000+ " Actual Slice time "+ sliceTimeActual/1000000+ " Send sync time " + sendMatchingTime/1000000 +
                    " Calculation time "+ actualCalcTime/1000000 + " Window verify time "+ windowverifyTime/1000000 + " Cond 1 " + innerCondTime + " Cond 2 " + innerWindowTime);

    }

    protected boolean isPreviousStateFinished() {
        for (BufferWorker worker: workersNeededToFinish) {
            if (finishedWorkers.indexOf(worker) == -1) {
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


