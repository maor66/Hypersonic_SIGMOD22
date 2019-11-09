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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatchBufferWorker extends BufferWorker {

    private List<BufferWorker> workersNeededToFinish;

    @Override
    protected ContainsEvent iterateOnSubList(ContainsEvent newElement, List<ContainsEvent> bufferSubList) {
        Match match = (Match) newElement;
        List<Event> partialMatchEvents = new ArrayList<>(match.getPrimitiveEvents());
        List<Event> actualEvents = (List<Event>) (List<?>) bufferSubList;
        if (actualEvents.isEmpty()) {
            return null;
        }
        Event latestEventInSubList = actualEvents.get(actualEvents.size() - 1);
        actualEvents = getSlice(actualEvents, (Match) newElement, eventState);
        for (Event event : actualEvents) {
            checkAndSendToNextState(event, partialMatchEvents, match);
        }
        return latestEventInSubList;
    }

    @Override
    protected ContainsEvent iterateOnOppositeBuffer(ContainsEvent newElement, List<List<ContainsEvent>> oppositeBufferList) {
        long latestEarliestTimeStamp = Long.MIN_VALUE;
        Event latestEvent = null;
        Match match = (Match) newElement;
        List<Event> partialMatchEvents = new ArrayList<>(match.getPrimitiveEvents());

        for (List<ContainsEvent> eventsList : oppositeBufferList) {
            List<Event> actualEvents = (List<Event>) (List<?>) eventsList;
            if (actualEvents.isEmpty()) {
               continue;
            }

            Event latestEventInSubList = actualEvents.get(actualEvents.size() - 1);
            if (latestEventInSubList.getTimestamp() > latestEarliestTimeStamp) {
                latestEvent = latestEventInSubList;
                latestEarliestTimeStamp = latestEventInSubList.getTimestamp();
            }

            actualEvents = getSlice(actualEvents, (Match) newElement, eventState);
                for (Event event : actualEvents) {
                checkAndSendToNextState(event, partialMatchEvents, match);

            }
        }
        return latestEvent;
    }




    @Override
    protected boolean isBufferSorted() {
        return false;
    }

    public MatchBufferWorker(TypedNFAState eventState, int finisherInputsToShutdown, int numberOfFinisherInputsToSend, AtomicBoolean isMainFinished, CopyOnWriteArrayList<BufferWorker> finishedWorkers, List<BufferWorker> workersNeededToFinish) {
        super(eventState, finisherInputsToShutdown, numberOfFinisherInputsToSend, isMainFinished, finishedWorkers);
        threadName = "MatchBufferWorker "+ eventState.getName();
        this.workersNeededToFinish = workersNeededToFinish;
    }

    @Override
    protected boolean isPreviousStateFinished() {
        if (workersNeededToFinish == null) //First MBWorker (second state) -> Need to check if main thread finished
            return isMainFinished.get();
        for (BufferWorker worker: workersNeededToFinish) {
            if (finishedWorkers.indexOf(worker) == -1) {
                return false;
            }
        }
        return true;
    }
}
