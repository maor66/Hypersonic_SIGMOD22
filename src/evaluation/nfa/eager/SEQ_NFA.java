package evaluation.nfa.eager;

import java.util.List;

import base.EventType;
import evaluation.common.State;
import evaluation.nfa.NFA;
import evaluation.nfa.eager.elements.NFAState;
import evaluation.nfa.eager.elements.Transition;
import pattern.Pattern;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;
import pattern.condition.time.PairTemporalOrderCondition;

public class SEQ_NFA extends NFA {

	public SEQ_NFA(Pattern pattern) {
		super(pattern);
	}

	public static void addTimingConstraints(List<EventType> events, CNFCondition condition) {
		for (int i = 0; i < events.size() - 1; ++i) {
			for (int j = i + 1; j < events.size(); ++j) {
				condition.addAtomicCondition(new PairTemporalOrderCondition(events.get(i), 
																		events.get(j)));
			}
		}
	}
	
	@Override
	protected void initNFAStructure(Pattern pattern) {
		CNFCondition condition = (CNFCondition)pattern.getCondition();
		List<EventType> currentSequenceOrder = pattern.getEventTypes();
		addTimingConstraints(currentSequenceOrder, condition);
		buildStatesChain(currentSequenceOrder, condition);
	}
	
	private void buildStatesChain(List<EventType> order, CNFCondition fullCondition) {
		buildStatesChain(order, fullCondition, null);
	}
	
	private void buildStatesChain(List<EventType> order, CNFCondition fullCondition,
									NFAState startingState) {
		buildStatesChain(order, fullCondition, startingState, null);
	}
	
	private void buildStatesChain(List<EventType> order,
									CNFCondition fullCondition,
									NFAState startingState,
									NFAState finishingState) {
		if (startingState == null) {
			startingState = new NFAState("Initial State", true, false, false);
			initialState = startingState;
			states.add(startingState);
		}
		NFAState currentState = startingState;
		List<CNFCondition> transitionConditions = fullCondition.getSubConditionsByOrder(order, false);
		for (int i = 0; i < order.size(); ++i) {
			EventType currentType = order.get(i);
			CNFCondition currentCondition = transitionConditions.get(i);
			NFAState destState = (i == order.size() - 1 && finishingState != null) ? 
														finishingState :
														new NFAState("Finishing State", false,  true, true);
			addTransition(currentState, destState, Transition.Action.TAKE, currentType, currentCondition);
			currentState = destState;
			if (currentState != finishingState)
				states.add(currentState);
		}
		if (finishingState == null) {
			finalState = currentState;
		}
	}

	@Override
	public String getStructureSummary() {
		return "Eager";
	}

	@Override
	public State getStateByAtomicCondition(AtomicCondition condition) {
		return null;
	}

	@Override
	public double getStateReachProbability(State state) {
		return 0;
	}
}
