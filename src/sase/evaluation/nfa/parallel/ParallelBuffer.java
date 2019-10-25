package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

public abstract class ParallelBuffer {

    protected List<ContainsEvent> buffer = new ArrayList<>();
    protected BlockingQueue<ContainsEvent> input = new LinkedBlockingQueue<>();
    protected StampedLock lock = new StampedLock();
    protected int totalNumberOfWorkers;
    private long stamp;
    protected long timeWindow;

    public ParallelBuffer(long timeWindow, int totalNumberOfWorkers) {this.timeWindow = timeWindow;
    this.totalNumberOfWorkers = totalNumberOfWorkers;}

    public void add(ContainsEvent ce) {
        input.add(ce);
    }

    public List<ContainsEvent> getBuffer(){

        return getBufferSubListWithOptimisticLock();
    }

    public List<ContainsEvent> lockAndAcquireBuffer() {
        stamp = lock.readLock();
        return buffer;
    }

    public void releaseBuffer() {
        lock.unlockRead(stamp);
    }

    public List<ContainsEvent> getBufferSubListWithOptimisticLock() {
//        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        List<ContainsEvent> listView = new ArrayList<>();
        long stamp = lock.tryOptimisticRead();
        listView.addAll(buffer); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
        if (!lock.validate(stamp)) {
//            Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.failedOptimisticReads);
            stamp = lock.readLock();
            try {
                listView = new ArrayList<>();
                listView.addAll(buffer);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        else {
//            Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.successfulOptimisticReads);
        }
        return listView;
    }


    public ContainsEvent getElement() {
        ContainsEvent ce = null;
        try {
            ce = input.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ce == null) {
            return null;
        }
        long stamp = lock.writeLock();
        try {
            buffer.add(ce);
        } finally {
            lock.unlockWrite(stamp);
        }
        return ce;
    }

    public void removeElements(long removingCriteriaTimeStamp, int workerIndex)
    {
        long stamp = lock.writeLock();
        if (buffer.isEmpty()) {
            lock.unlock(stamp);
            return;
        }
        actuallyRemove(removingCriteriaTimeStamp, workerIndex);
        lock.unlock(stamp);
    }

    protected int getStartingIteratorIndex(int workerIndex) {
        int n = buffer.size() * workerIndex;
        return (int) Math.ceil((double) n / totalNumberOfWorkers);
    }


    protected abstract void actuallyRemove(long removingCriteriaTimeStamp, int workerIndex);

//    public ContainsEvent getInputPersistentlyWithTimer(long milis) {
//        final int TIMES_TO_TRY = 10;
//        for (int i = 0; i < TIMES_TO_TRY; i++) {
//            ContainsEvent element = getElement();
//            if (element != null) {
//                return element;
//            }
//            try {
//                Thread.sleep(milis / TIMES_TO_TRY);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
}
