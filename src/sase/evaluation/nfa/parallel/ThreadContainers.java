package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.EventType;
import sase.evaluation.common.Match;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.StampedLock;

public class ThreadContainers {
//    private final BlockingQueue<? extends ContainsEvent> input;
//    private final BlockingQueue<Match> nextStateOutput;
//    private List<ContainsEvent> bufferSubList;
//    private List<? extends BufferWorker> oppositeBufferWorkers;
//    private StampedLock lock;
    private ParallelBuffer ownBuffer;
    private ParallelMatchBuffer nextStateBuffer;
    private ParallelBuffer oppositeBuffer;
    private EventType eventType;
    private long timeWindow;

    public ListIterator<ContainsEvent> getOppositeBuffer() {
        return oppositeBuffer.getBuffer();
    }

    public enum StatisticsType {
        computations ,
        syncActions,
        timestampComparison,
        bufferInsertions,
        bufferRemovals
    };

    public class ParallelStatistics {

        private HashMap<StatisticsType, Integer> values;

        public HashMap<StatisticsType, Integer> getValues() {
            return values;
        }

        public ParallelStatistics()
        {
            values = new HashMap<>();
            for (StatisticsType type :  StatisticsType.values()) {
                values.put(type,0);
            }
        }

        public void incrementStatistic(StatisticsType type) {
            values.put(type, values.get(type) + 1);
        }

    }
    public long getTimeWindow() {
        return timeWindow;
    }

//    public BlockingQueue<Match> getNextStateOutput() {
//        return nextStateOutput;
//    }

    public void sendToNextState (Match m) {
        nextStateBuffer.add(m);
    }

    public ThreadContainers(ParallelBuffer ownBuffer, ParallelBuffer oppositeBuffer, ParallelMatchBuffer nextStateBuffer, EventType state, long timeWindow) {
//        this.input = input;
        this.ownBuffer = ownBuffer;
        this.nextStateBuffer = nextStateBuffer;
        this.oppositeBuffer = oppositeBuffer;
        this.eventType = state;
//        bufferSubList = new ArrayList<>();
//        this.oppositeBufferWorkers = oppositeBufferWorkers;
//        this.nextStateOutput = nextStateOutput;
//        lock = new StampedLock();
        this.timeWindow = timeWindow;
    }

//    public List<? extends BufferWorker> getOppositeBufferWorkers() {
//        return oppositeBufferWorkers;
//    }

//    public List<ContainsEvent> getBufferSubListAfterWorkerFinished() {
//        return bufferSubList;
//    }


//    public List<ContainsEvent> getBufferSubListWithOptimisticLock() {
//    public List<ContainsEvent> getBufferSubListWithOptimisticLock() {
//        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
//        List<ContainsEvent> listView = new ArrayList<>();
//        long stamp = lock.tryOptimisticRead();
//        listView.addAll(bufferSubList); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
//        if (!lock.validate(stamp)) {
//            Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.failedOptimisticReads);
//            stamp = lock.readLock();
//            try {
//                listView = new ArrayList<>();
//                listView.addAll(bufferSubList);
//            } finally {
//                lock.unlockRead(stamp);
//            }
//        }
//        else {
//            Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.successfulOptimisticReads);
//        }
//        return listView;
//    }

    public void addEventToOwnBuffer(ContainsEvent event) {
        ownBuffer.add(event);
    }

//    public void addEventToOwnBuffer(ContainsEvent event) {
//        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
//        long stamp = lock.writeLock();
//        try {
//            bufferSubList.add(event);
//        } finally {
//            lock.unlockWrite(stamp);
//        }
//    }

    public ContainsEvent getInputPersistentlyWithTimer(long milis) {
        return ownBuffer.getInputPersistentlyWithTimer(milis);
    }

    public ContainsEvent getInput() {
        return ownBuffer.getElement();
    }

    public EventType getEventType() {
        return eventType;
    }

    public void removeExpiredElements(long removingCriteriaTimeStamp) {
        ownBuffer.removeElements(removingCriteriaTimeStamp);
    }
//    public void removeExpiredElements(long removingCriteriaTimeStamp, boolean isBufferSorted, ContainsEvent removingCriteria) {
//
//        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
//        int numberOfRemovedElements = 0;
//        long stamp = lock.writeLock();
//        if (bufferSubList.isEmpty()) {
//            lock.unlock(stamp);
//        }
//
//        if (isBufferSorted) { //IB is sorted, while MB isn't
//            ContainsEvent currEvent = bufferSubList.get(0);
//            while (currEvent.getTimestamp() + timeWindow   < removingCriteriaTimeStamp) {
//                bufferSubList.remove(0);
//                numberOfRemovedElements++;
//                if (bufferSubList.isEmpty()) {
//                    break;
//                }
//                currEvent = bufferSubList.get(0);
//            }
//        }
//
//        else { //Since the buffer isn't sorted, the iterating order doesn't matter'
//            int beforeRemovalSize = bufferSubList.size();
//            bufferSubList.removeIf(element -> element.getEarliestTimestamp() + timeWindow   < removingCriteriaTimeStamp);
//            numberOfRemovedElements = beforeRemovalSize - bufferSubList.size();
//        }
//
//        lock.unlock(stamp);
//        if (isBufferSorted) { //This is not a very good design...
//            Environment.getEnvironment().getStatisticsManager().updateParallelStatistic(Statistics.parallelBufferRemovals,
//                    numberOfRemovedElements);
//        }
//        else {
//            Environment.getEnvironment().getStatisticsManager().updateParallelStatistic(Statistics.parallelPartialMatchesDeleltions,
//                    numberOfRemovedElements);
//        }
//    }
}
