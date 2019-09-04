package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BufferWorker implements Runnable {
    ThreadContainers dataStorage;
    TypedNFAState eventState;
    int finisherInputsToShutdown;
    int numberOfFinisherInputsToSend;
    String threadName;
    String log = "";
    long ID;
    int loopCounter;
    public ThreadContainers getDataStorage() {
        return dataStorage;
    }

    public BufferWorker(TypedNFAState eventState, int finisherInputsToShutdown, int numberOfFinisherInputsToSend)
    {
        this.eventState = eventState;
        this.finisherInputsToShutdown = finisherInputsToShutdown;
        this.numberOfFinisherInputsToSend = numberOfFinisherInputsToSend;
    }

    private void writeLog()
    {
        try {
            BufferedWriter logWriter = new BufferedWriter(new FileWriter("C:\\Users\\Maor\\Documents\\compHyb" + ID + ".txt"));
            logWriter.write(log);
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName + Thread.currentThread().getName());
        ID =  Thread.currentThread().getId();
        loopCounter = 0;
        while (true) {
            ContainsEvent newElement = null;
            try {
                newElement = takeNextInput();
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
                    writeLog();
                    return; //TODO: how to end task?
                }
                continue;
            }
//            log += "Adding to my buffer" +  newElement + "\n";
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
        return dataStorage.getInput().take();
    }


    protected List<List<ContainsEvent>> getOppositeBufferList()
    {
//        log+= "Getting buffers. loop " + loopCounter + "\n";
        List<List<ContainsEvent>> oppositeBuffer = new ArrayList<>();
        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
//            log += "Getting from worker " + worker.ID + "\n";
            var a = worker.getDataStorage().getBufferSubListWithOptimisticLock();
            for (var b : a) {
//                log+= "Got element " + b + "\n";
            }
            oppositeBuffer.add(a);
//            oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
        }
        return oppositeBuffer;
    }

    protected boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness

        //TODO: complicated condition instead of return for debugging reasons
        log+= loopCounter+ ": " + partialMatch +", " + event + "\n";
        if (verifyTimeWindowConstraint(partialMatch, event)) {
            return getActualNextTransition(eventState).verifyConditionWithTemporalConditionFirst(
                    Stream.concat(partialMatch.getPrimitiveEvents().stream(),Event.asList(event).stream()).collect(Collectors.toList()));
        }
        return false;
//        return verifyTimeWindowConstraint(partialMatch, event) && getActualNextTransition(eventState).verifyConditionWithTemporalConditionFirst( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
//                Stream.concat(partialMatch.getPrimitiveEvents().stream(),Event.asList(event).stream()).collect(Collectors.toList())); //Combining two lists
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
        loopCounter++;
//        log += "Starting loop " + loopCounter + "has " + events.size() + " events and " + matches.size() + " matches\n";
        for (Match partialMatch : matches) {
            for (Event event : events) {
//                log+= "In loop " + loopCounter + "Event" + event + " partial match " + partialMatch;
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
