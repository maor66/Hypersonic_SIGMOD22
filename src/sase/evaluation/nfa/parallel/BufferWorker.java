package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;

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
    String log;
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
    public void run() {
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
//                    BufferedWriter writercond = null;
////                    log += System.nanoTime() + ": Finished run";
//                    try {
//                        writercond = new BufferedWriter(new FileWriter("C:\\Users\\Maor\\Documents\\lazyCEPlogs\\log"+Thread.currentThread().getName()+"_"+this.getClass().getName()+eventState+".txt"));
//                        writercond.write(log);
//                        writercond.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    return; //TODO: how to end task?
                }
                continue;
            }
//            if (dataStorage.getEventType() != newEvent.getType()) {
//                throw new RuntimeException("Got wrong event type in Input Buffer");
//            }
//            log += System.nanoTime() + ": Got new element: " + newElement + "\n";
            dataStorage.addEventToOwnBuffer(newElement);
            List<List<ContainsEvent>> oppositeBufferList = getOppositeBufferList();
//            log += System.nanoTime() + ": Iterating on: ";
//            for (ContainsEvent ce : oppositeBufferList) {
//                log += ce + ",";
//            }
//            log += "\n";
            if (oppositeBufferList.isEmpty()) {
                continue;
            }
            iterateOnOppositeBuffer(newElement, oppositeBufferList);
//            log += System.nanoTime() + ": Finished iterating\n";
            List<ContainsEvent> combinedOppositeBuffer = new ArrayList<>();
            oppositeBufferList.forEach(combinedOppositeBuffer::addAll);
            ContainsEvent removingCriteria = getReleventRemovingCriteria(combinedOppositeBuffer);
//            log += System.nanoTime() + ": Removing based on" + removingCriteria + "\n";
            if (removingCriteria != null) {
                String s = dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted());
//                log += s;
//                dataStorage.removeExpiredElements(oppositeBufferList.get(0).getEarliestTimestamp(), isBufferSorted());
            }
//            log += System.nanoTime() + ": Finished removing\n\n";
        }
    }

    private ContainsEvent getReleventRemovingCriteria(List<ContainsEvent> oppositeBufferList)
    {
        //TODO: can be optimized because we already go over the MB when looking for matches, so its possible to calculate latest match at that stage
//        return oppositeBufferList.stream().max(Comparator.comparing(ContainsEvent::getEarliestTimestamp)).orElse(null);
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
        List<List<ContainsEvent>> oppositeBuffer = new ArrayList<>();
        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
            oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
        }
        return oppositeBuffer;
    }

    protected boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        //TODO: doesn't have to verify temporal condition first anymore - check if removing doesn't hurt correctness

        return verifyTimeWindowConstraint(partialMatch, event) && getActualNextTransition(eventState).verifyConditionWithTemporalConditionFirst( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
                Stream.concat(partialMatch.getPrimitiveEvents().stream(),List.of(event).stream()).collect(Collectors.toList())); //Combining two lists
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
                    Match m = partialMatch.createNewPartialMatchWithEvent(event);
                    if (m.getLatestEventTimestamp() > m.getEarliestTimestamp() + dataStorage.getTimeWindow()){
                        System.out.println("time window wrong");
                    }
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
