package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.evaluation.common.Match;

import java.sql.Time;
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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


    public ParallelQueue (int sizeLimit) {
        likelyBatchSize = ElementWorker.maxBatchSizeRegular;
        if (sizeLimit < likelyBatchSize) {
            throw new RuntimeException("Batch size too large for queue limit");
        }
        this.sizeLimit = sizeLimit / likelyBatchSize;
    }

    public ParallelQueue () {
        this(Integer.MAX_VALUE);
    }


    private int sizeLimit;
    private int likelyBatchSize;
    private AtomicLong currentSize = new AtomicLong(0);
    private AtomicLong maxSize = new AtomicLong(0);
    private ConcurrentLinkedDeque<List<CE>> queue = new ConcurrentLinkedDeque<>();

    public void put(List<CE> ce) {
        put(ce, false);
    }

        public void put(List<CE> ce, boolean isAddFirst) {
        List<CE> copyiedBatch = new ArrayList<>(ce);
        while (currentSize.get() >= sizeLimit) {}
        if (isAddFirst) {
            queue.addFirst(copyiedBatch);
        }
        else {
            queue.add(copyiedBatch);
        }
        long curSize = currentSize.incrementAndGet();
        if (maxSize.get() < curSize) {
            maxSize.set(curSize);
        }
    }

    public List<CE> poll(long time, TimeUnit tu) {
        List<CE> element = queue.poll();
        if (element != null) {
            currentSize.getAndDecrement();
        }
        return element;
    }

    public long getMaxSize() {
        return maxSize.get();
    }

    public void setSizeLimit(int maxValue) {
        sizeLimit = maxValue;
    }
    
    public void putAtHead(List<CE> newPartialMatchWithEvent) {
       put(newPartialMatchWithEvent, true);
    }
}
