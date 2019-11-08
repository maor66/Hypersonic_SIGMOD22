package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;
import sase.simulator.Environment;
import sase.statistics.Statistics;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BufferWorker implements Runnable {
    ThreadContainers dataStorage;
    TypedNFAState eventState;
    public int finisherInputsToShutdown;
    int numberOfFinisherInputsToSend;
    String threadName;
    protected boolean canCreateMatches= true;
    public long actualCalcTime = 0;
    private LazyTransition transition;
    public long windowverifyTime = 0;
    public Thread thread;
    protected final CopyOnWriteArrayList<BufferWorker> finishedWorkers;
    protected AtomicBoolean isMainFinished;

    public long numberOfHandledItems = 0;
    public long numberOfOppositeItems = 0;
    public  long idleTime = 0;
    public long iteratingBufferTime = 0;
    public long sliceTime = 0;
    public long sliceTimeActual = 0;
    public long sendMatchingTime = 0;
    public long conditionTime = 0;
    public Long innerCondTime = 0L;
    public Long innerWindowTime = 0L;


    public ThreadContainers getDataStorage() {
        return dataStorage;
    }

    public BufferWorker(TypedNFAState eventState, int finisherInputsToShutdown, int numberOfFinisherInputsToSend, AtomicBoolean isMainFinished, CopyOnWriteArrayList<BufferWorker> finishedWorkers)
    {
        this.eventState = eventState;
        this.finisherInputsToShutdown = finisherInputsToShutdown;
        this.numberOfFinisherInputsToSend = numberOfFinisherInputsToSend;
        transition = (LazyTransition) eventState.getActualNextTransition();
        this.isMainFinished = isMainFinished;
        this.finishedWorkers = finishedWorkers;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        thread.setName(threadName + " " + Thread.currentThread().getName());
        while (true) {
            ContainsEvent newElement = null;

            try {
                long time = System.nanoTime();
                newElement = takeNextInput();
                sliceTime += System.nanoTime() - time;
                if (newElement == null) {
                    sliceTimeActual += System.nanoTime() - time;
                    if (isPreviousStateFinished()) {
                        finishRun();
                        return;
                    }
                    else {
                        continue;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long time = System.nanoTime();
            dataStorage.addEventToOwnBuffer(newElement);
            ContainsEvent removingCriteria = null;
            long latestTimeStamp = Long.MIN_VALUE;
            for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
               ContainsEvent ce = iterateOnSubList(newElement, worker.getDataStorage().getBufferSubListWithReadLock());
               worker.getDataStorage().releaseReadLock();
               if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
                   latestTimeStamp = ce.getEarliestTimestamp();
                   removingCriteria = ce;
               }
            }

//            List<List<ContainsEvent>> oppositeBufferList = getOppositeBufferList();
//            idleTime += System.nanoTime() - time;
//            if (oppositeBufferList.isEmpty()) {
//                continue;
//            }
//            numberOfHandledItems++;
//            time = System.nanoTime();
//            ContainsEvent removingCriteria = iterateOnOppositeBuffer(newElement, oppositeBufferList);
//            iteratingBufferTime += System.nanoTime() - time;
            if (removingCriteria != null) {
                time = System.nanoTime();
                dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted(), removingCriteria);
                windowverifyTime += System.nanoTime() -time;
            }
        }
    }

    protected abstract ContainsEvent iterateOnSubList(ContainsEvent newElement, List<ContainsEvent> bufferSubList);

    private void finishRun() {
        finishedWorkers.add(this);
            System.out.println("Thread " + Thread.currentThread().getName() +" " +Thread.currentThread().getId() + " has finished at "  + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                    " Handled " + numberOfHandledItems + " items and compared to " + numberOfOppositeItems+" opposite items. Idle time "+ idleTime/1000000 + " Condition time " + conditionTime/1000000 +
                    " Iterating buffer time " + iteratingBufferTime/1000000+ " Slice time "+ sliceTime/1000000+ " Actual Slice time "+ sliceTimeActual/1000000+ " Send sync time " + sendMatchingTime/1000000 +
                    " Calculation time "+ actualCalcTime/1000000 + " Window verify time "+ windowverifyTime/1000000 + " Cond 1 " + innerCondTime + " Cond 2 " + innerWindowTime);

    }
    protected abstract boolean isPreviousStateFinished();

    private ContainsEvent getReleventRemovingCriteria(List<ContainsEvent> oppositeBufferList)
    {
        //TODO: can be optimized because we already go over the MB when looking for matches, so its possible to calculate latest match at that stage
        long latestEarliestTimeStamp = Long.MIN_VALUE;
        ContainsEvent element = null;
        for (ContainsEvent ce : oppositeBufferList) {
            if (ce.getEarliestTimestamp() > latestEarliestTimeStamp) {
                element = ce;
                latestEarliestTimeStamp = ce.getEarliestTimestamp();
            }
        }
        return element;
    }

    protected ContainsEvent takeNextInput() throws InterruptedException {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        return dataStorage.getInput().poll(1, TimeUnit.SECONDS);
    }

    protected List<List<ContainsEvent>> getOppositeBufferList()
    {
        List<List<ContainsEvent>> oppositeBuffer = new ArrayList<>();
        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
            if (finishedWorkers.indexOf(worker)==-1) {
//                oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
                oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
                }
            else { //If the worker has finished its run, the sub buffer won't change so we don't have to copy it
                oppositeBuffer.add(worker.getDataStorage().getBufferSubListAfterWorkerFinished());
            }
        }
        return oppositeBuffer;
    }

    protected boolean isEventCompatibleWithPartialMatch(Match partialMatch, List<Event> partialMatchEvents, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness
        long time = System.nanoTime();
        boolean b =  transition.verifyCondition(partialMatchEvents);
        actualCalcTime += System.nanoTime() - time;
        numberOfOppositeItems++;
        if (b) {
            time = System.nanoTime();
            boolean w = verifyTimeWindowConstraint(partialMatch, event);
            conditionTime += System.nanoTime() - time;
            return  w;
        }
        return  false;
    }


    private boolean verifyTimeWindowConstraint(Match partialMatch, Event event) {

        return (partialMatch.getLatestEventTimestamp() <= event.getTimestamp() + dataStorage.getTimeWindow()) &&
                (partialMatch.getEarliestEvent() + dataStorage.getTimeWindow() >= event.getTimestamp());
    }

    protected void sendToNextState(Match newPartialMatchWithEvent) {

        BlockingQueue<Match> matchesQueue = dataStorage.getNextStateOutput();
        try {
            matchesQueue.put(newPartialMatchWithEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void checkAndSendToNextState(Event event, List<Event> partialMatchEvents, Match match) {
        partialMatchEvents.add(event);
        if (isEventCompatibleWithPartialMatch(match, partialMatchEvents, event)) {
            long time = System.nanoTime();
            sendToNextState(match.createNewPartialMatchWithEvent(event));
            sendMatchingTime += System.nanoTime() - time;
        }
        partialMatchEvents.remove(partialMatchEvents.size() - 1);
    }

    public void initializeDataStorage(ThreadContainers dataStorage) {
        this.dataStorage = dataStorage;
    }

    protected abstract ContainsEvent iterateOnOppositeBuffer(ContainsEvent newElement, List<List<ContainsEvent>> oppositeBufferList);

    protected abstract boolean isBufferSorted();
}
