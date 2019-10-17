package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.EvaluationOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputBufferWorker extends BufferWorker {

private boolean shouldMatchIncomingEvents;



    @Override
    protected boolean isBufferSorted() {
        return true;
    }
        public InputBufferWorker(TypedNFAState eventState, EvaluationOrder evaluationOrder, List<EventType> supportedEventTypes, int finisherInputsToShutdown,
                                 int numberOfFinisherInputsToSend, AtomicBoolean isMainFinished, CopyOnWriteArrayList<BufferWorker> finishedWorkers) {
            super(eventState, finisherInputsToShutdown, numberOfFinisherInputsToSend, isMainFinished, finishedWorkers);
            canCreateMatches = true;
            threadName = "InputBufferWorker "+ eventState.getName();
            EventType prevEventType = evaluationOrder.getFullEvaluationOrder().get(evaluationOrder.getFullEvaluationOrder().indexOf(eventState.getEventType())-1);
            if (supportedEventTypes.indexOf(prevEventType) > supportedEventTypes.indexOf(eventState.getEventType())) {
                System.out.println(threadName + " Cannot create matches");
                canCreateMatches = false;
            }
            shouldMatchIncomingEvents = true;
            System.out.println("event State" + eventState + " calculating" + shouldMatchIncomingEvents);
        }


    @Override
    protected boolean isPreviousStateFinished() {
        return isMainFinished.get();
    }
    @Override
    protected ContainsEvent iterateOnOppositeBuffer(ContainsEvent newElement, ListIterator<ContainsEvent> oppositeBufferList) {

        long latestEarliestTimeStamp = Long.MIN_VALUE;
        Match latest = null;

        while (oppositeBufferList.hasNext()) {
            Match match = (Match) oppositeBufferList.next();

                if (match.getEarliestTimestamp() > latestEarliestTimeStamp) {
                    latest = match;
                    latestEarliestTimeStamp = match.getEarliestTimestamp();
                }
                List<Event> partialMatchEvents = new ArrayList<>(match.getPrimitiveEvents());

                checkAndSendToNextState((Event) newElement, partialMatchEvents, match);
            }

        return latest;
    }


}
