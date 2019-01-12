package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.simulator.Environment;
import sase.statistics.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.locks.StampedLock;

public class ThreadContainers {
    private List<ContainsEvent> inputBufferSubList;
    private BlockingQueue<ContainsEvent> eventsFromMain;
    private BlockingQueue<ContainsEvent> removingData;
    private StampedLock lock;
    private EventType eventType;
    private int sequentialNumber;
    private long timeWindow;

    public ThreadContainers(BlockingQueue<ContainsEvent> eventsFromMain, BlockingQueue<ContainsEvent> removingData, EventType state, long timeWindow) {
        this.eventsFromMain = eventsFromMain;
        this.removingData = removingData;
        this.eventType = state;
        inputBufferSubList = new ArrayList<>();
        lock = new StampedLock();
        this.timeWindow = timeWindow;
    }

    public List<ContainsEvent>  getInputBufferSubListWithOptimisticLock() {
        List<ContainsEvent> listView = new ArrayList<>();
        long stamp = lock.tryOptimisticRead();
        listView.addAll(inputBufferSubList); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                listView.addAll(inputBufferSubList);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return listView;
    }

    public void addEventToOwnInputBuffer(ContainsEvent event) {
        long stamp = lock.writeLock();
        try {
            inputBufferSubList.add(event);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public LinkedTransferQueue<ContainsEvent> getEventsFromMain() {
        return (LinkedTransferQueue<ContainsEvent>) eventsFromMain;
    }

    public BlockingQueue<ContainsEvent> getRemovingData() {
        return removingData;
    }

    public int getSequentialNumber() {
        return sequentialNumber;
    }

    public StampedLock getLock() {
        return lock;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void removeExpiredEvents(ContainsEvent removingCriteria) {
        long latest_timestamp = removingCriteria.getTimestamp();
        long stamp = lock.writeLock();
        int numberOfRemovedEvents = 0;
        if (inputBufferSubList.isEmpty()) {
            lock.unlock(stamp);
            return;
        }
        ContainsEvent currEvent = inputBufferSubList.get(0);
//        while (currEvent.getTimestamp() + timeWindow < latest_timestamp) {
        //TODO: This doesn't remove as much events as ilya's algorithm. Using the commented out line improves it but should check if its enough
        // The problem is that I should remove based on the rPM as they indicate what "time" it is for the partial matches, which means that older rPMs won't arrive,
        // (if assuming that OoO can't happen) If I remove based on coming events, I could have a delayed rPM that should have been compared to an already deleted event
        while (currEvent.getTimestamp() + timeWindow < inputBufferSubList.get(inputBufferSubList.size()-1).getTimestamp()) {
            inputBufferSubList.remove(0);
            numberOfRemovedEvents++;
            if (inputBufferSubList.isEmpty()) {
                break;
            }
            currEvent = inputBufferSubList.get(0);
        }
        lock.unlock(stamp);
//        System.out.println("PAR deleted: " + numberOfRemovedEvents); //TODO: delete
        Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferRemovals,
                numberOfRemovedEvents);
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
//    public abstract void addEventToOwnInputBuffer(Event newEvent);
//}
