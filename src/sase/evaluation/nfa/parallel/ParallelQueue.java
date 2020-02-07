package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;

import java.sql.Time;
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelQueue<CE> {
//    private LinkedList<CE> queue = new LinkedList<>();
////    ReentrantReadWriteLock putLock = new ReentrantReadWriteLock();
////    ReadWriteLock takeLock = new ReentrantReadWriteLock();
//    private final Object putLock = new Object();
//    private final Object takeLock = new Object();
//
//    public boolean isEmpty()
//    {
//        return queue.isEmpty();
//    }
//
//    public void put(CE ce) {
//        synchronized (putLock) {
//            queue.addLast(ce);
//        }
//    }
//
//    public CE poll(long time, TimeUnit tu) {
//        synchronized (takeLock) {
//                return queue.poll();
//        }
//    }
//
//    public CE take() {
//        return  poll(0, TimeUnit.SECONDS);
//    }
        public boolean isEmpty() {
            return queue.isEmpty();
        }

    public void put(CE ce) {
            queue.add(ce);
        }
    private AbstractQueue<CE> queue = new ConcurrentLinkedQueue<CE>();
        public CE poll(long time, TimeUnit tu) {
            return queue.poll();
        }

    public CE take() {
                        return  poll(0, TimeUnit.SECONDS);

            }

}
