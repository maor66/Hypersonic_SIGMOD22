package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.StampedLock;

public class ThreadContainers {
    private final ParallelQueue<Match> nextStateOutput;
    private List<ContainsEvent> bufferSubList;
    private StampedLock lock;
    private EventType eventType;
    private long timeWindow;
    private long stamp;
    private AtomicBoolean isContainerActive;


    public List<ContainsEvent> getBufferSubListWithReadLock() {
        stamp = lock.readLock();
        return bufferSubList;
    }
    public void releaseReadLock() {
        lock.unlockRead(stamp);
    }

    public ThreadContainers createClone() {
        return new ThreadContainers(nextStateOutput, eventType, timeWindow);
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

    public ParallelQueue<Match> getNextStateOutput() {
        return nextStateOutput;
    }

    public ThreadContainers(ParallelQueue<Match> nextStateOutput, EventType state, long timeWindow) {
        this.eventType = state;
        bufferSubList = new ArrayList<>();
//        this.oppositeBufferWorkers = oppositeBufferWorkers;
        this.nextStateOutput = nextStateOutput;
        lock = new StampedLock();
        this.timeWindow = timeWindow;
    }


    public List<ContainsEvent> getBufferSubListAfterWorkerFinished() {
        return bufferSubList;
    }


    public List<ContainsEvent> getBufferSubListWithOptimisticLock() {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        List<ContainsEvent> listView = new ArrayList<>();
        long stamp = lock.tryOptimisticRead();
        listView.addAll(bufferSubList); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
        if (!lock.validate(stamp)) {
            Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.failedOptimisticReads);
            stamp = lock.readLock();
            try {
                listView = new ArrayList<>();
                listView.addAll(bufferSubList);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        else {
            Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.successfulOptimisticReads);
        }
        return listView;
    }

    public void addEventToOwnBuffer(ContainsEvent event) {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        long stamp = lock.writeLock();
        try {
            if (!bufferSubList.isEmpty()&& event instanceof Event && bufferSubList.get(bufferSubList.size() - 1).getTimestamp() > event.getTimestamp()) {
                System.out.println("Event out of order " + event + "last in bufer" + bufferSubList.get(bufferSubList.size()-1));
            }
            bufferSubList.add(event);
        } finally {
            lock.unlockWrite(stamp);
        }
    }


    public EventType getEventType() {
        return eventType;
    }

    public int removeExpiredElements(long removingCriteriaTimeStamp, boolean isBufferSorted, ContainsEvent removingCriteria) {


        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        int numberOfRemovedElements = 0;
        long stamp = lock.writeLock();
        if (bufferSubList.isEmpty()) {
            lock.unlock(stamp);
        }

        if (isBufferSorted) { //IB is sorted, while MB isn't
            ContainsEvent currEvent = bufferSubList.get(0);
            while (currEvent.getTimestamp() + timeWindow * 2   < removingCriteriaTimeStamp) {
                bufferSubList.remove(0);
                numberOfRemovedElements++;
                if (bufferSubList.isEmpty()) {
                    break;
                }
                currEvent = bufferSubList.get(0);
            }
        }

        else { //Since the buffer isn't sorted, the iterating order doesn't matter'
            int beforeRemovalSize = bufferSubList.size();
            bufferSubList.removeIf(element -> element.getEarliestTimestamp() + timeWindow   < removingCriteriaTimeStamp);
            numberOfRemovedElements = beforeRemovalSize - bufferSubList.size();
        }

        lock.unlock(stamp);
        if (isBufferSorted) { //This is not a very good design...
            Environment.getEnvironment().getStatisticsManager().updateParallelStatistic(Statistics.parallelBufferRemovals,
                    numberOfRemovedElements);
        }
        else {
            Environment.getEnvironment().getStatisticsManager().updateParallelStatistic(Statistics.parallelPartialMatchesDeleltions,
                    numberOfRemovedElements);
        }
        return numberOfRemovedElements;
    }

    public boolean isContainerActive() {

    }

    public void setContainerActive() {

    }
}
