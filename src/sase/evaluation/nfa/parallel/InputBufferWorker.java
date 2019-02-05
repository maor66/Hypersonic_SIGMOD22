package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.EvaluationOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InputBufferWorker extends BufferWorker {

private boolean shouldMatchIncomingEvents;
    @Override
    protected void iterateOnOppositeBuffer(ContainsEvent newElement, List<ContainsEvent> oppositeBufferList) {
        if (eventState.isInitial()) { // Send automatically to next state
            //TODO: should optimize and send to MB worker in the second state directly from the main thread
            sendToNextState(new Match(new ArrayList<>(((List<Event>)(List<?>) List.of(newElement))), System.currentTimeMillis()));
        }
        List<ContainsEvent> partialMatches = oppositeBufferList;
        List<Match> actualMatches = (List<Match>)(List<?>) partialMatches;
//        for (Match m: actualMatches) {
//            if (m.getLatestEvent().getSequenceNumber() > newElement.getSequenceNumber()) {
//                System.out.println("rPm - " + m + " Event - " + newElement);
//            }
//        }
//        if (!shouldMatchIncomingEvents) {
//            return;
//        }
        if (actualMatches.isEmpty()) {
            return;
        }
//        actualMatches.removeIf(match -> match.getLatestEvent().getSequenceNumber() > newElement.getSequenceNumber());
        tryToAddMatchesWithEvents(actualMatches, new ArrayList<>(List.of((Event)newElement)));
    }


    @Override
    protected boolean isBufferSorted() {
        return true;
    }
        public InputBufferWorker(TypedNFAState eventState, EvaluationOrder evaluationOrder, List<EventType> supportedEventTypes, int finisherInputsToShutdown, int numberOfFinisherInputsToSend) {
        super(eventState, finisherInputsToShutdown, numberOfFinisherInputsToSend);
        List<EventType> evalOrderPrecedingStates = new ArrayList<>();
        for (EventType eventType : evaluationOrder.getFullEvaluationOrder()) { // Get all preceding events in the evalutation order
            if (eventType == eventState.getEventType()) {
                break;
            }
            evalOrderPrecedingStates.add(eventType);
        }
        for (EventType eventType : evalOrderPrecedingStates) { // Check if any preceding event in the evaluation order is actually succeeding in the sequence order
            if (isFirstTypeSequencedEarlierThanSecondType(eventState.getEventType(), eventType, supportedEventTypes)) {
                shouldMatchIncomingEvents = false;
                System.out.println("event State" + eventState + " calculating: " + shouldMatchIncomingEvents);
                return;
            }
        }
        shouldMatchIncomingEvents = true;
        System.out.println("event State" + eventState + " calculating" + shouldMatchIncomingEvents);
    }

    private boolean isFirstTypeSequencedEarlierThanSecondType(EventType firstType, EventType secondType, List<EventType> supportedEventTypes) {
        return supportedEventTypes.indexOf(firstType) < supportedEventTypes.indexOf(secondType);
    }

//
//    ThreadContainers dataStorage;
//

//    @Override
//    public void run() {
//        while (true) {
//            ContainsEvent newEvent;
//            ContainsEvent removingCriteria;
//            try {
//                newEvent = dataStorage.getInputQueue().take(); //TODO: better to take batches instead of one event
//                removingCriteria = dataStorage.getRemovingData().poll();
//                if (removingCriteria != null) {
//                    dataStorage.(removingCriteria);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException("Exception while trying to get event from BlockingQueue");
//            }
//            if (newEvent.isLastInput()) {
//                System.err.println("Finisher evemt");
//                return; //TODO: how to end task?
//            }
////            if (dataStorage.getEventType() != newEvent.getType()) {
////                throw new RuntimeException("Got wrong event type in Input Buffer");
////            }
//            dataStorage.addEventToOwnBuffer(newEvent);
//            removeExpiredEvents(newEvent);
//        }
////        iterateOnMatchBuffer();
//    }
//
//    private void removeExpiredEvents(ContainsEvent newEvent) {
////        dataStorage.removeExpiredEvents(newEvent);
//        //TODO: wrong removing!!! Can only remove after taken for comparison against partial match. Should remove when a partial match is received
//        //TODO: When a rPM arrives, must check based on all partial match TS. example: got B13_D17. In IB we have C6_C11
//        //TODO: It is possible to remove C6 only since it is not possible to get D16 or smaller so B must be B7 or above.
//        //TODO: Should check if it is indeed possible for rPM to be delayed (multiple threads so...) such that BX_D15 is possible
//    }
//
//    public ThreadContainers getDataStorage() {
//        return dataStorage;
//    }
}
