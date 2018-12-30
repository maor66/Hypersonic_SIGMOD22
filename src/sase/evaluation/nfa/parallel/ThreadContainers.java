package sase.evaluation.nfa.parallel;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.StampedLock;

public class ThreadContainers
{
    private List<Event> inputBufferSubList;
    private BlockingQueue<Event> eventsFromMain;
    private StampedLock lock;
    private EventType eventType;
    private int sequentialNumber;
    private long timeWindow;

    public ThreadContainers(BlockingQueue eventsFromMain, EventType state, long timeWindow)
    {
        this.eventsFromMain = eventsFromMain;
        this.eventType = state;
        inputBufferSubList = new ArrayList<>();
        lock = new StampedLock();
        this.timeWindow = timeWindow;
    }

    public List<Event> getInputBufferSubListWithOptimisticLock()
    {
        long stamp = lock.tryOptimisticRead();
        List<Event> listView = new ArrayList<>(inputBufferSubList); //TODO: copies the list, which can hit performance. A different solution could be to read from the start of the list, this can be done without locking (up to a point)
        if (!lock.validate(stamp))
        {
            stamp = lock.readLock();
            try {
                listView = new ArrayList<>(inputBufferSubList);
            }
            finally {
                lock.unlockRead(stamp);
            }
        }
        return listView;

    }
    public void addEventToOwnInputBuffer(Event event) {
        long stamp = lock.writeLock();
        try {
            inputBufferSubList.add(event);
        }
        finally {
           lock.unlockWrite(stamp);
        }
    }

    public BlockingQueue<Event> getEventsFromMain() {
        return eventsFromMain;
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

    public void removeExpiredEvents(Event newEvent) {
        inputBufferSubList.removeIf(event -> event.getTimestamp() + timeWindow < newEvent.getTimestamp()); //TODO: no locking!!!!
        //TODO: removing can be done from the start of the list and stop since the list is sorted by timestamp
    }
}
