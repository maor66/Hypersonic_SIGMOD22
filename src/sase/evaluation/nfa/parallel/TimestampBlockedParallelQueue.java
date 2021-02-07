package sase.evaluation.nfa.parallel;

import sase.evaluation.common.Match;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class TimestampBlockedParallelQueue extends ParallelQueue<Match> {


    public class WindowToPartialMatches
    {
        private Map<Long, List<Match>> windowBlocks = new ConcurrentHashMap<>();
        private AtomicBoolean isEarliestBlockCounted = new AtomicBoolean(false);
        private long currentBlockSerial = -1;
        private final long timeWindow;

        private ReadWriteLock rwl = new ReentrantReadWriteLock();
        private Lock rLock = rwl.readLock();
        private Lock wLock = rwl.writeLock();
        public WindowToPartialMatches(long timeWindow) { this.timeWindow = timeWindow;}

public Map<Long, Long>  addedBlocks = new ConcurrentHashMap<>();
public Map<Long, Long>  removedBlocks = new ConcurrentHashMap<>();

        public Long insertNewPartialMatch(Match match)
        {
            long block = match.getEarliestTimestamp() / timeWindow;
            rLock.lock();
            if (block < currentBlockSerial) {
//                System.out.println("Got an early match " + match + " Current early block is " + currentBlockSerial);
                block = currentBlockSerial;
            }
            if (currentBlockSerial == -1) {
                currentBlockSerial = block;
            }
            rLock.unlock();
            List<Match> matches = windowBlocks.computeIfAbsent(block, aLong -> new ArrayList<>());
            matches.add(match);
            addedBlocks.putIfAbsent(block, 0L);
            return block;
        }

        public List<Match> popEarliestBlock(long earliestBlock)
        {
//            isEarliestBlockCounted.set(false);
//            System.out.println("Current block is " + currentBlockSerial + " Sent block " + earliestBlock);
            if (earliestBlock != currentBlockSerial) {
                return new ArrayList<>();
            }
            removedBlocks.putIfAbsent(currentBlockSerial, 0L);
            wLock.lock();
            List<Match> earliestBlockMatches = windowBlocks.remove(earliestBlock);
            if (earliestBlockMatches == null) {
                wLock.unlock();
                return new ArrayList<>();
            }
            try {
                currentBlockSerial = Collections.min(windowBlocks.keySet());
            }
            catch (Exception e)
            {
                System.out.println("Exception. Current is " + currentBlockSerial + " Sent block " + earliestBlock);
                throw e;
            }
            wLock.unlock();
            if (currentBlockSerial < earliestBlock) {
//                System.out.println("Removed new " + earliestBlock +" before old " + currentBlockSerial);
            }
            return earliestBlockMatches;

        }

        public boolean isFilledBesidesEarliest()
        {
//            if (isEarliestBlockCounted.get()) {
//                return false;
//            }
            if (windowBlocks.keySet().size() > 1) {
//                isEarliestBlockCounted.set(true);
                return true;
            }
            return false;
        }

        public long getCurrentBlockSerial() {
            return currentBlockSerial;
        }

        public List<Match> popAllBlocks() {
            List<Match> matches = new ArrayList<>();
            List<Long> blocks = new ArrayList<>(windowBlocks.keySet());
            Collections.sort(blocks);
            blocks.remove(Long.MAX_VALUE / timeWindow);
            for (long block : blocks) {
                matches.addAll(windowBlocks.get(block));
            }
            windowBlocks.clear();
            return matches;
        }
    }


    private final int expectedThreads;
    private AtomicInteger currentThreadsThatFinishedWithBlock = new AtomicInteger(0);
    private Map<Long, WindowToPartialMatches> threadStorageMapping = new ConcurrentHashMap<>();
    private Map<Long, Map<Long, Integer>> finishedThreadsByBlock = new ConcurrentHashMap<>();
    List<Integer> finishedThreads = new ArrayList<>();
    private final long timeWindow;

    public TimestampBlockedParallelQueue(int expectedThreads, long timeWindow)
    {
        super();
        this.expectedThreads = expectedThreads * 2; // Two workers per thread
        this.timeWindow = timeWindow;
    }

//    private long getThreadIdentifier()
//    {
//        return Thread.currentThread().getId();
//    }

    private int received = 0;
    @Override
    public void put(List<Match> oneMatch, long id) {
//                l.lock();

        received++;
        if (oneMatch.size() > 1)
        {
            throw new RuntimeException();
        }
        Match match = oneMatch.get(0);

        WindowToPartialMatches mapping = threadStorageMapping.computeIfAbsent(id,integer ->  new WindowToPartialMatches(timeWindow));
        long block = mapping.insertNewPartialMatch(match);
        Map<Long, Integer> finishedThreads = finishedThreadsByBlock.computeIfAbsent(block, aLong -> new ConcurrentHashMap<>());
        if (match.getEarliestTimestamp() == Long.MAX_VALUE) {
            currentThreadsThatFinishedWithBlock.incrementAndGet();
        }

        if (mapping.isFilledBesidesEarliest()) {
            finishedThreads.putIfAbsent(id, 0);
            if (finishedThreads.size() == expectedThreads || currentThreadsThatFinishedWithBlock.get() == expectedThreads) {
                if (match.getEarliestTimestamp() == Long.MAX_VALUE) { //Pop all
                    sendAllBlocks();
//                    l.unlock();
                }
                else {
//                    l.lock();
                    long earliestBlock = Collections.min(finishedThreadsByBlock.keySet());
                    sendBlockToQueue(earliestBlock);
//                    l.unlock();
                    finishedThreadsByBlock.remove(earliestBlock);
                }
            }
        }
//        l.unlock();


//        if (mapping.isFilledBesidesEarliest()) {
//            int finishedThreads = currentThreadsThatFinishedWithBlock.incrementAndGet();
//            if (finishedThreads == expectedThreads) {
//                sendBlockToQueue();
//                currentThreadsThatFinishedWithBlock.set(0);
//            }
//        }
    }

    private void sendAllBlocks()
    {
        for (WindowToPartialMatches map : threadStorageMapping.values())
        {
            sendToActualQueue(map.popAllBlocks());
        }
        System.out.println("Queue received " + received + " sent " + sent);
    }

    Lock l = new ReentrantLock();

    private void sendBlockToQueue(long block)
    {
//        long earliestBlock = Collections.min(threadStorageMapping.values().stream().map(WindowToPartialMatches::getCurrentBlockSerial).collect(Collectors.toList()));
        for (WindowToPartialMatches map : threadStorageMapping.values())
        {
//            System.out.println("Sending block " + block + " At " + System.identityHashCode(this));
//            l.lock();
            List<Match > matches = map.popEarliestBlock(block);
//            l.unlock();
            sendToActualQueue(matches);
        }
    }

    private int sent = 0;
    private void sendToActualQueue(List<Match> matches) {
        for (Match earlyMatch : matches) {
            sent++;
            super.put(Collections.singletonList(earlyMatch), 0);
        }
    }
}