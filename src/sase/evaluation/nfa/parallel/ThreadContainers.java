package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.simulator.Environment;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;
import sase.statistics.Statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.StampedLock;

public class ThreadContainers {
    private final BlockingQueue<? extends ContainsEvent> input;
    private final BlockingQueue<Match> nextStateOutput;
    private List<ContainsEvent> bufferSubList;
    private List<? extends BufferWorker> oppositeBufferWorkers;
    private StampedLock lock;
    private EventType eventType;
    private long timeWindow;


    public long getTimeWindow() {
        return timeWindow;
    }

    public BlockingQueue<Match> getNextStateOutput() {
        return nextStateOutput;
    }

    public ThreadContainers(BlockingQueue<? extends ContainsEvent> input, List<? extends BufferWorker> oppositeBufferWorkers, BlockingQueue<Match> nextStateOutput, EventType state, long timeWindow) {
        this.input = input;
        this.eventType = state;
        bufferSubList = new ArrayList<>();
        this.oppositeBufferWorkers = oppositeBufferWorkers;
        this.nextStateOutput = nextStateOutput;
        lock = new StampedLock();
        this.timeWindow = timeWindow;
    }

    public List<? extends BufferWorker> getOppositeBufferWorkers() {
        return oppositeBufferWorkers;
    }

    public List<ContainsEvent> getBufferSubListWithOptimisticLock() {
        List<ContainsEvent> listView = new ArrayList<>();
        long stamp = lock.tryOptimisticRead();
        listView.addAll(bufferSubList); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                listView = new ArrayList<>();
                listView.addAll(bufferSubList);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return listView;
    }

    public void addEventToOwnBuffer(ContainsEvent event) {
        long stamp = lock.writeLock();
        try {
            bufferSubList.add(event);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public BlockingQueue<? extends ContainsEvent> getInput() {
        return input;
    }

    public StampedLock getLock() {
        return lock;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void removeExpiredElements(long removingCriteriaTimeStamp, boolean isBufferSorted) {
        int numberOfRemovedElements = 0;
        List<ContainsEvent> removedEvents = new ArrayList<>();
        long stamp = lock.writeLock();
        if (bufferSubList.isEmpty()) {
            lock.unlock(stamp);
            return;
        }
        if (isBufferSorted) { //IB is sorted, while MB isn't
            ContainsEvent currEvent = bufferSubList.get(0);
            while (currEvent.getTimestamp() + timeWindow < removingCriteriaTimeStamp) {
                removedEvents.add(bufferSubList.remove(0));
                numberOfRemovedElements++;
                if (bufferSubList.isEmpty()) {
                    break;
                }
                currEvent = bufferSubList.get(0);
            }
        }
        else { //Since the buffer isn't sorted, the iterating order doesn't matter'
            int beforeRemovalSize = bufferSubList.size();
            bufferSubList.removeIf(element -> element.getEarliestTimestamp() + timeWindow < removingCriteriaTimeStamp);
            numberOfRemovedElements = beforeRemovalSize - bufferSubList.size();
        }
        lock.unlock(stamp);
        if (isBufferSorted) { //This is not a very good design...
            Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferRemovals,
                    numberOfRemovedElements);
//            try {
//                BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Maor\\Documents\\removelogs\\" + System.nanoTime()));
//                for (ContainsEvent ce : removedEvents) {
//                    bw.write("Removed event " + ce + " based on rPM: " + removingCriteriaTimeStamp);
//                }
//                bw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }
        else {
            Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.instanceDeletions,
                    numberOfRemovedElements);
        }
    }
}




















//package sase.evaluation.nfa.parallel;
//
//import sase.base.Event;
//import sase.base.EventType;
//import sase.evaluation.common.Match;
//import sase.simulator.Environment;
//import sase.statistics.Statistics;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedTransferQueue;
//import java.util.concurrent.locks.StampedLock;
//
//public class ThreadContainers {
//
//    protected StampedLock lock;
//    protected EventType eventType;
//    protected long timeWindow;
//
//    public ThreadContainers(EventType state, long timeWindow) {
//        this.eventType = state;
//        lock = new StampedLock();
//        this.timeWindow = timeWindow;
//    }
//
//    protected <T> List<T> getBufferSubListWithOptimisticLock(List<T> buffer)
//    {
//        List<T> listView = new ArrayList<>();
//        long stamp = lock.tryOptimisticRead();
//        listView.addAll(buffer); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
//        if (!lock.validate(stamp)) {
//            stamp = lock.readLock();
//            try {
//                listView.addAll(buffer);
//            } finally {
//                lock.unlockRead(stamp);
//            }
//        }
//        return listView;
//    }
//
//    protected  <T> void addToOwnBuffer(T element, List<T> buffer)
//    {
//        long stamp = lock.writeLock();
//        try {
//            buffer.add(element);
//        }
//        finally {
//            lock.unlockWrite(stamp);
//        }
//    }
//
//    public StampedLock getLock() {
//        return lock;
//    }
//
//    public EventType getEventType() {
//        return eventType;
//    }
//
//
//    public abstract Event takeFromInputQueue();
//
//    public abstract Match pollRemovingCriteria();
//
//    public abstract <T> void removeExpiredElements(T removingCriteria);
//
//    public abstract void addEventToOwnBuffer(Event newEvent);
//}
