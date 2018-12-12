package sase.evaluation.nfa.lazy;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;

import java.util.*;

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
        return findPartialMatchesInCurrentState(eventState, event);
    }

    private List<Match> findPartialMatchesInCurrentState(TypedNFAState eventState, Event event) {
        //TODO: PARALLELIZE: This is the step where a thread receives the event and should do its task (find PMs with comparing to MB)
        return null;
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
