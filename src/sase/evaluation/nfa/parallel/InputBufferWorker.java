package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.EvaluationOrder;

import java.util.ArrayList;
import java.util.List;
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

//        List<EventType> evalOrderPrecedingStates = new ArrayList<>();
//        for (EventType eventType : evaluationOrder.getFullEvaluationOrder()) { // Get all preceding events in the evalutation order
//            if (eventType == eventState.getEventType()) {
//                break;
//            }
//            evalOrderPrecedingStates.add(eventType);
//        }
//        for (EventType eventType : evalOrderPrecedingStates) { // Check if any preceding event in the evaluation order is actually succeeding in the sequence order
//            if (isFirstTypeSequencedEarlierThanSecondType(eventState.getEventType(), eventType, supportedEventTypes)) {
//                shouldMatchIncomingEvents = false;
//                System.out.println("event State" + eventState + " calculating: " + shouldMatchIncomingEvents);
//                return;
//            }
//        }
            shouldMatchIncomingEvents = true;
            System.out.println("event State" + eventState + " calculating" + shouldMatchIncomingEvents);
        }
    private boolean isFirstTypeSequencedEarlierThanSecondType(EventType firstType, EventType secondType, List<EventType> supportedEventTypes) {
        return supportedEventTypes.indexOf(firstType) < supportedEventTypes.indexOf(secondType);
    }

    @Override
    protected boolean isPreviousStateFinished() {
        return isMainFinished.get();
    }
    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<List<ContainsEvent>> oppositeBufferList) {

        for (List<ContainsEvent> partialMatchesList: oppositeBufferList) {
            long time = System.nanoTime();
            numberOfOppositeItems += partialMatchesList.size();
            List<Match> actualMatches = (List<Match>)(List<?>) partialMatchesList;
            if (actualMatches.isEmpty()) {
                continue;
            }
            List <Event> a = new ArrayList(Event.asList((Event) newElement));
            actualCalcTime = System.nanoTime() -time;
            tryToAddMatchesWithEvents(actualMatches,a);
        }

    }

}
