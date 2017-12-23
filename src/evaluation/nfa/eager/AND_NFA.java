package evaluation.nfa.eager;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import evaluation.nfa.NFA;
import evaluation.nfa.eager.elements.NFAState;
import evaluation.nfa.eager.elements.Transition;
import pattern.Pattern;
import pattern.condition.base.CNFCondition;

public class AND_NFA extends NFA {

	public AND_NFA(Pattern pattern) {
		super(pattern);
	}

	private static List<List<Integer>> getAllPermutations(List<Integer> numbers) {
		List<List<Integer>> permutations = new ArrayList<List<Integer>>();
		if (numbers.size() == 1) {
			List<Integer> result = new ArrayList<Integer>();
			result.add(numbers.get(0));
			permutations.add(result);
			return permutations;
		}
		Integer firstNumber = numbers.get(0);
		List<Integer> innerNumbersList = new ArrayList<Integer>(numbers);
		innerNumbersList.remove(firstNumber);
		List<List<Integer>> prevPermutations = getAllPermutations(innerNumbersList);
		for (List<Integer> prevPermutation : prevPermutations) {
			for (int i = 0; i <= prevPermutation.size(); ++i) {
				List<Integer> newPermutation = new ArrayList<Integer>(prevPermutation);
				newPermutation.add(i, firstNumber);
				permutations.add(newPermutation);
			}
		}
		return permutations;
	}
	
	//will probably need this some time
	@SuppressWarnings("unused")
	private List<List<EventType>> getAllEventsOrders(List<EventType> eventTypes) {
		List<List<EventType>> orders = new ArrayList<List<EventType>>();
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < eventTypes.size(); ++i) {
			indices.add(i);
		}
		List<List<Integer>> permutations = getAllPermutations(indices);
		for (List<Integer> permutation : permutations) {
			List<EventType> currOrder = new ArrayList<EventType>();
			for (Integer index : permutation) {
				currOrder.add(eventTypes.get(index.intValue()));
			}
			orders.add(currOrder);
		}
		return orders;
	}
	
	protected void buildStatesSubTree(NFAState rootState, List<EventType> types, CNFCondition condition) {
		recursiveBuildStatesSubTree(rootState, types, condition);
	}
	
	protected void recursiveBuildStatesSubTree(NFAState rootState, List<EventType> types, CNFCondition condition) {
		if (types.isEmpty())
			return;
		if (types.size() == 1) {
			addTransition(rootState, finalState, Transition.Action.TAKE, types.get(0), condition);
			return;
		}
		for (EventType eventType : types) {
			NFAState stateForCurrentType = new NFAState(String.format("State for %s", eventType.getName()));
			states.add(stateForCurrentType);
			List<EventType> typesForSubTree = new ArrayList<EventType>(types);
			typesForSubTree.remove(eventType);
			CNFCondition conditionForCurrentType = condition.getConditionExcludingTypes(typesForSubTree);
			CNFCondition nextCondition = condition.getConditionForTypes(typesForSubTree, false);
			addTransition(rootState, stateForCurrentType, Transition.Action.TAKE, eventType, conditionForCurrentType);
			recursiveBuildStatesSubTree(stateForCurrentType, typesForSubTree, nextCondition);
		}
	}
	
	@Override
	protected void initNFAStructure(Pattern pattern) {
		initialState = new NFAState("Initial State", true, false, false);
		states.add(initialState);
		finalState = new NFAState("Final State", false, true, true);
		states.add(finalState);
		buildStatesSubTree(initialState, pattern.getEventTypes(), (CNFCondition)pattern.getCondition());
	}

	@Override
	public String getStructureSummary() {
		return "Eager";
	}
}
