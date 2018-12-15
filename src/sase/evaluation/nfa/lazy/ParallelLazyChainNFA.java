package sase.evaluation.nfa.lazy;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelLazyChainNFA extends LazyChainNFA {

    private Map<TypedNFAState, List<Event>> parallelInputBuffer;
    private Map<TypedNFAState, List<Match>> partialMatchBuffer;

    public ParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
        super(pattern, evaluationPlan, negationType);
        createStateMappings();
    }

    @Override
    public List<Match> processNewEvent(Event event, boolean canStartInstance) {
        //return super.processNewEvent(event, canStartInstance);
        TypedNFAState eventState = getStateByEventType(parallelInputBuffer.keySet(), event);
        if (null == eventState) {
            // The event has irrelevent type (not in the query)
            return null;
        }
        parallelInputBuffer.get(eventState).add(event); //TODO: PARALLELIZE : Add to the state's input buffer
        return findPartialMatchesOnNewEvent(eventState, event);
//        return findPartialMatchesInCurrentState(eventState, event);
    }

    private List<Match> findPartialMatchesOnNewEvent(TypedNFAState eventState, Event event) {
        return findPartialMatchesInCurrentState(eventState, new ArrayList<>(List.of(event)), partialMatchBuffer.get(eventState));
    }
    private List<Match> findPartialMatchesOnNewPartialMatch(TypedNFAState eventState, Match partialMatch) {
        return findPartialMatchesInCurrentState(eventState, parallelInputBuffer.get(eventState), new ArrayList<>(List.of(partialMatch)));
    }


    private List<Match> findPartialMatchesInCurrentState(TypedNFAState eventState, List<Event> eventList, List<Match> partialMatchList) {
        //TODO: PARALLELIZE: This is the step where a thread receives the event and should do its task (find PMs with comparing to MB)
        List <Match> extraEventPartialMatches = new ArrayList<>();
        for (Match partialMatch : partialMatchList) {
            for (Event event : eventList) { //One of the list is of size 1, so it is actually comparing one object with every other on the list
                if (isEventCompatibleWithPartialMatch(eventState, partialMatch, event)) {
                    extraEventPartialMatches.add(partialMatch.createNewPartialMatchWithEvent(evaluationOrder.getFullEvaluationOrder(), event));
                }
            }
        }
        if (eventState.isAccepting()) { // Last state so can return the matches
            // TODO: PARALLELIZE: instead of returning matches, should forward to an output thread
            return extraEventPartialMatches;
        }
        TypedNFAState nextState = (TypedNFAState) eventState.getIncomingTransitions().get(0).getDestination();//should be only one transition
        return sendPartialMatchToNextState(nextState, extraEventPartialMatches); // Sending in "batch" all new partial matches (they more "complete" as the new event is added) to the next state
    }

    private boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)
        List<Transition> transitions = eventState.getOutgoingTransitions();
        if (transitions.size() != 1) {
            throw new RuntimeException("States should have only one outgoing transition");
        }
        return transitions.get(0).verifyCondition( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
                Stream.concat(partialMatch.getPrimitiveEvents().stream(),List.of(event).stream()).collect(Collectors.toList())); //Combining two lists
    }

    private List<Match> sendPartialMatchToNextState(TypedNFAState nextState, List<Match> extraEventPartialMatches) {
        partialMatchBuffer.get(nextState).addAll(extraEventPartialMatches); //TODO: PARALLELIZE: should be done in a seperate thread (or threads)
        List <Match> completeMatches = new ArrayList<>();
        for (Match partialMatch : extraEventPartialMatches) {
            completeMatches.addAll(findPartialMatchesOnNewPartialMatch(nextState, partialMatch);
        }
        return completeMatches;
    }


    private void createStateMappings()
    {
        parallelInputBuffer = new HashMap<>();
        partialMatchBuffer = new HashMap<>();
        //TODO : should create states as TypedNFAStates
        for (TypedNFAState s : states) {
            parallelInputBuffer.put(s, new ArrayList<>());
            partialMatchBuffer.put(s, new ArrayList<>());
        }
    }

    private TypedNFAState getStateByEventType(Set<TypedNFAState> states, Event event)
    {
        for (TypedNFAState s : states){
            if (s.getEventType().getName().equals(event.getType().getName())) {
                return s;
            }
        }
        return null;
    }
}
