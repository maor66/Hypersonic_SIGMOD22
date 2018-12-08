package sase.multi.calculator.local.neighborhood;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import sase.base.EventType;
import sase.evaluation.plan.EvaluationPlan;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class PairwiseShareNeighborhood extends LocalSearchNeighborhood {

	public PairwiseShareNeighborhood(IAlgoUnit algoUnit, MultiPlan initialState, Long timeLimit) {
		super(algoUnit, initialState, timeLimit);
	}

	@Override
	protected MultiPlan actuallyGetNextNeighbor() {
		MultiPlan nextNeighbor = null;
		while (nextNeighbor == null) {
			nextNeighbor = attemptToGetNextNeighbor();
		}
		return nextNeighbor;
	}
	
	private boolean isValidPatternSelection(CompositePattern firstPattern, CompositePattern secondPattern) {
		if (firstPattern.getPatternId() == secondPattern.getPatternId()) {
			return false;
		}
		if (!graph.isColoringOn()) {
			return true;
		}
		return (graph.getColoredPatterns().contains(firstPattern) || graph.getColoredPatterns().contains(secondPattern));
	}
	
	private MultiPlan attemptToGetNextNeighbor() {
		CompositePattern firstPattern = graph.getRandomPattern();
		CompositePattern secondPattern = graph.getRandomPattern();
		while (!isValidPatternSelection(firstPattern, secondPattern)) {
			secondPattern = graph.getRandomPattern();
		}
		Set<Set<EventType>> maxSubSets = new HashSet<Set<EventType>>(graph.getPatternPeerMaxSubSets(firstPattern));
		maxSubSets.retainAll(graph.getPatternPeerMaxSubSets(secondPattern));
		if (maxSubSets.isEmpty()) {
			return null;//try one more time
		}
		maxSubSets = filterNonMaxSubsets(maxSubSets);
		Set<EventType> maxSubSet = getRandomElement(maxSubSets);
		Set<EventType> randomSubset = getRandomSubset(maxSubSet);
		EvaluationPlan subPlan = randomSubset.isEmpty() ? null : algoUnit.calculateEvaluationPlan(firstPattern.getSubPattern(randomSubset));
		EvaluationPlan firstNewPlan = algoUnit.calculateFullEvaluationPlan(firstPattern, subPlan, currentState);
		EvaluationPlan secondNewPlan = algoUnit.calculateFullEvaluationPlan(secondPattern, subPlan, currentState);
		Map<CompositePattern, EvaluationPlan> newPlans = new HashMap<CompositePattern, EvaluationPlan>();
		newPlans.put(firstPattern, firstNewPlan);
		newPlans.put(secondPattern, secondNewPlan);
		return createMultiPlanWithNewPlans(newPlans);
	}
	
	private Set<Set<EventType>> filterNonMaxSubsets(Set<Set<EventType>> subsets) {
		if (subsets.size() == 1) {
			return subsets;
		}
		int maxSize = 0;
		for (Set<EventType> set : subsets) {
			if (set.size() > maxSize) {
				maxSize = set.size();
			}
		}
		Set<Set<EventType>> result = new HashSet<Set<EventType>>();
		for (Set<EventType> set : subsets) {
			if (set.size() == maxSize) {
				result.add(set);
			}
		}
		return result;
	}
	
	private Set<EventType> getRandomElement(Set<Set<EventType>> subsets) {
		Iterator<Set<EventType>> iterator = subsets.iterator();
		int randomPatternIndex = new Random().nextInt(subsets.size());
		for (int i = 0; i < randomPatternIndex; ++i) {
			iterator.next();
		}
		return iterator.next();
	}

	protected Set<EventType> getRandomSubset(Set<EventType> maxSubSet) {
		int subsetId = new Random().nextInt((int)Math.pow(2, maxSubSet.size()));
		Set<EventType> result = new HashSet<EventType>();
		for (EventType eventType : maxSubSet) {
			if (subsetId % 2 == 1) {
				result.add(eventType);
			}
			subsetId /= 2;
		}
		return result;
	}
	
	protected MultiPlan createMultiPlanWithNewPlans(Map<CompositePattern, EvaluationPlan> newPlans) {
		MultiPlan newPlan = algoUnit.instantiateMultiPlan(currentState);
		for (Entry<CompositePattern, EvaluationPlan> entry : newPlans.entrySet()) {
			newPlan.replacePatternPlan(entry.getKey(), entry.getValue());
		}
		if (graph.isColoringOn()) {
			newPlan.registerPotentiallyColoredPatterns(newPlans.keySet());
		}
		return newPlan;
	}

}
