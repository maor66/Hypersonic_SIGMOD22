package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;

import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ParallelBuffer {

    protected CopyOnWriteArrayList<ContainsEvent> buffer = new CopyOnWriteArrayList<>();
    protected ReentrantLock lock = new ReentrantLock();
    protected int nextElementToRead = 0;
    protected long timeWindow;

    ParallelBuffer(long timeWindow) {this.timeWindow = timeWindow;}

    public void add(ContainsEvent ce) {
        lock.lock();
        buffer.add(ce);
        lock.unlock();
    }

    public ListIterator<ContainsEvent> getBuffer(){
        return buffer.listIterator();
    }

    public ContainsEvent getElement() {
        lock.lock();
        if (nextElementToRead > buffer.size() - 1 ) {
            lock.unlock();
            return null;
        }
        ContainsEvent element = buffer.get(nextElementToRead++);
        lock.unlock();
        return element;
    }
    public void removeElements(long removingCriteriaTimeStamp)
    {
        lock.lock();
        if (buffer.isEmpty()) {
            lock.unlock();
            return;
        }
        nextElementToRead -= actuallyRemove(removingCriteriaTimeStamp);
        lock.unlock();
    }

    protected abstract int actuallyRemove(long removingCriteriaTimeStamp);

    public ContainsEvent getInputPersistentlyWithTimer(long milis) {
        final int TIMES_TO_TRY = 10;
        for (int i = 0; i < TIMES_TO_TRY; i++) {
            ContainsEvent element = getElement();
            if (element != null) {
                return element;
            }
            try {
                Thread.sleep(milis / TIMES_TO_TRY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
