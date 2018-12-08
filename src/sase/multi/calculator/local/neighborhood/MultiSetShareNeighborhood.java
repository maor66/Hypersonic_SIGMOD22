package sase.multi.calculator.local.neighborhood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sase.base.EventType;
import sase.evaluation.plan.EvaluationPlan;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class MultiSetShareNeighborhood extends PairwiseShareNeighborhood {

	private final int maxPatternNum;
	
	public MultiSetShareNeighborhood(IAlgoUnit algoUnit, MultiPlan initialState, Long timeLimit, int maxPatternNum) {
		super(algoUnit, initialState, timeLimit);
		this.maxPatternNum = maxPatternNum;
	}
	
	@Override
	protected MultiPlan actuallyGetNextNeighbor() {
		Set<EventType> maxSubSet;
		Set<CompositePattern> patternsForNewPlans;
		do {
			maxSubSet = graph.getRandomMaxSubSet();
			patternsForNewPlans = selectPatternsForSharing(graph.getPatternsByMaxSubSet(maxSubSet));
		}
		while (!isValidPatternSelection(patternsForNewPlans));
		Set<EventType> randomSubSet = getRandomSubset(maxSubSet);
		CompositePattern subPattern = patternsForNewPlans.iterator().next().getSubPattern(randomSubSet);
		EvaluationPlan subPlan = randomSubSet.isEmpty() ? null : algoUnit.calculateEvaluationPlan(subPattern);
		Map<CompositePattern, EvaluationPlan> newPlans = new HashMap<CompositePattern, EvaluationPlan>();
		for (CompositePattern compositePattern : patternsForNewPlans) {
			newPlans.put(compositePattern, algoUnit.calculateFullEvaluationPlan(compositePattern, subPlan, currentState));
		}
		return createMultiPlanWithNewPlans(newPlans);
	}
	
	private Set<CompositePattern> selectPatternsForSharing(Set<CompositePattern> patterns) {
		if (patterns.size() <= maxPatternNum) {
			return patterns;
		}
		List<CompositePattern> filteredPatterns = new ArrayList<CompositePattern>(patterns);
		Collections.shuffle(filteredPatterns);
		Set<CompositePattern> result = new HashSet<CompositePattern>();
		for (int i = 0; i < maxPatternNum; ++i) {
			result.add(filteredPatterns.get(i));
		}
		return result;
	}
	
	private boolean isValidPatternSelection(Set<CompositePattern> patterns) {
		if (!graph.isColoringOn()) {
			return true;
		}
		Set<CompositePattern> intersection = new HashSet<CompositePattern>(graph.getColoredPatterns());
		intersection.retainAll(patterns);
		return !intersection.isEmpty();
	}

}
