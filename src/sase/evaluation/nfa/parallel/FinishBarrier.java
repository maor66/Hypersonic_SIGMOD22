package sase.evaluation.nfa.parallel;

import sase.base.Event;
import sase.base.EventType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.StampedLock;

public class FinishBarrier {
    private final List<EventType> evaluationOrder;
    private final HashMap<EventType, Integer> initialLevels = new HashMap<>();
    private Map<EventType, Integer> currentLevels = new HashMap<>();

    private StampedLock lock = new StampedLock();
    private CopyOnWriteArrayList<EventType> finishedStates = new CopyOnWriteArrayList<>();

    public FinishBarrier(List<EventType> evaluationOrder) {
        this.evaluationOrder = evaluationOrder;
    }

    public int numberOfFinishedStates()
    {
        return finishedStates.size();
    }

    public void addBarrierLevel(EventType eventType, int workerCount) {
        initialLevels.put(eventType, workerCount);
        currentLevels.put(eventType, workerCount);
    }

    public void notifyWorkerFinished(EventType eventType) {
        long stamp = lock.writeLock();
        int workersLeftInLevel = currentLevels.get(eventType) - 1;
        if (workersLeftInLevel < 0) {
            throw new RuntimeException("Decreased number of worker too many times at type " + eventType);
        }
        if (workersLeftInLevel == 0) {
            finishedStates.add(eventType);
        }
        currentLevels.put(eventType, workersLeftInLevel);
        lock.unlockWrite(stamp);
    }

    public boolean hasFinishedWithAllPreviousStates(EventType eventType) {
        int indexInOrder = evaluationOrder.indexOf(eventType);
        return finishedStates.size() >= indexInOrder;
    }

    public Iterator<EventType> getAllFinishedStates() {
        return finishedStates.iterator();
    }

    public void resetCounts() {
        currentLevels = new HashMap<>(initialLevels);
        finishedStates.clear();
    }
}
