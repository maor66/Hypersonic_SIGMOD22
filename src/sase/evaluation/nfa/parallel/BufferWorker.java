package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.config.MainConfig;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;
import sase.simulator.Environment;
import sase.statistics.Statistics;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BufferWorker implements Callable<ThreadContainers.ParallelStatistics> {
    ThreadContainers dataStorage;
    TypedNFAState eventState;
    int finisherInputsToShutdown;
    int numberOfFinisherInputsToSend;
    String threadName;
    String log;
    private int numberOfHandledItems = 0;
    private int numberOfOppositeItems = 0;

    public ThreadContainers getDataStorage() {
        return dataStorage;
    }

    public BufferWorker(TypedNFAState eventState, int finisherInputsToShutdown, int numberOfFinisherInputsToSend)
    {
        this.eventState = eventState;
        this.finisherInputsToShutdown = finisherInputsToShutdown;
        this.numberOfFinisherInputsToSend = numberOfFinisherInputsToSend;
    }

    @Override
    public ThreadContainers.ParallelStatistics call() {
        Thread.currentThread().setName(threadName + " " + Thread.currentThread().getName());
        while (true) {
            ContainsEvent newElement = null;
            try {
                newElement = takeNextInput();
                numberOfHandledItems++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (newElement.isLastInput()) {
//                System.err.println("Finisher input" + this);
                finisherInputsToShutdown--;
                if (finisherInputsToShutdown ==0) {
                    for (int i = 0; i< numberOfFinisherInputsToSend; i++) {
                        sendToNextState(new Match());
                    }
                    if (MainConfig.parallelDebugMode) {
                        System.out.println("Thread " + Thread.currentThread().getName() +" " +Thread.currentThread().getId() + " has finished at "  + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                        " Handled " + numberOfHandledItems + " items and compared to " + numberOfOppositeItems+" opposite items");
                    }
                    return dataStorage.statistics; //TODO: how to end task?
                }
                continue;
            }
            dataStorage.addEventToOwnBuffer(newElement);
            List<List<ContainsEvent>> oppositeBufferList = getOppositeBufferList();
            if (oppositeBufferList.isEmpty()) {
                continue;
            }
            iterateOnOppositeBuffer(newElement, oppositeBufferList);
            List<ContainsEvent> combinedOppositeBuffer = new ArrayList<>();
            oppositeBufferList.forEach(combinedOppositeBuffer::addAll);
            ContainsEvent removingCriteria = getReleventRemovingCriteria(combinedOppositeBuffer);
            if (removingCriteria != null) {
                dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted());
            }
        }
    }

    private ContainsEvent getReleventRemovingCriteria(List<ContainsEvent> oppositeBufferList)
    {
        //TODO: can be optimized because we already go over the MB when looking for matches, so its possible to calculate latest match at that stage
        long latestEarliestTimeStamp = Long.MIN_VALUE;
        ContainsEvent element = null;
        for (ContainsEvent ce : oppositeBufferList) {
            if (ce == null) {
                System.out.println("ce is null");
            }
            if (ce.getEarliestTimestamp() > latestEarliestTimeStamp) {
                element = ce;
                latestEarliestTimeStamp = ce.getEarliestTimestamp();
            }
        }
        return element;
    }

    protected ContainsEvent takeNextInput() throws InterruptedException {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        return dataStorage.getInput().take();
    }


    protected List<List<ContainsEvent>> getOppositeBufferList()
    {
        List<List<ContainsEvent>> oppositeBuffer = new ArrayList<>();
        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
            oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
        }
        numberOfOppositeItems += oppositeBuffer.size();
        return oppositeBuffer;
    }

    protected boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness
    	

        return verifyTimeWindowConstraint(partialMatch, event) && getActualNextTransition(eventState).verifyConditionWithTemporalConditionFirst( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
                Stream.concat(partialMatch.getPrimitiveEvents().stream(),Event.asList(event).stream()).collect(Collectors.toList())); //Combining two lists
    }

    private LazyTransition getActualNextTransition(NFAState state)
    {
        for (Transition transition: state.getOutgoingTransitions()) {
            if (transition.getAction() == Transition.Action.TAKE) {
                return (LazyTransition)transition;
            }
        }
        throw new RuntimeException("No outgoing TAKE transition");
    }

    private boolean verifyTimeWindowConstraint(Match partialMatch, Event event) {
        if (partialMatch == null)
            System.out.println("partial");
        if (event == null)
            System.out.println("event");
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


    protected void tryToAddMatchesWithEvents(List<Match> matches, List<Event> events)
    {
        for (Match partialMatch : matches) {
            for (Event event : events) {
                if (isEventCompatibleWithPartialMatch(eventState, partialMatch, event)) {
                    sendToNextState(partialMatch.createNewPartialMatchWithEvent(event));
                }
            }
        }
    }
    public void initializeDataStorage(ThreadContainers dataStorage) {
        this.dataStorage = dataStorage;
    }

    protected abstract void iterateOnOppositeBuffer(ContainsEvent newElement, List<List<ContainsEvent>> oppositeBufferList);

    protected abstract boolean isBufferSorted();
}
