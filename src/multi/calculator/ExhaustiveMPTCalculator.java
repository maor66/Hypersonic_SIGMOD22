package sase.multi.calculator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sase.base.EventType;
import sase.evaluation.plan.EvaluationPlan;
import sase.multi.MultiPatternGraph;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.local.LocalSearchMPTCalculator;
import sase.pattern.CompositePattern;

public class ExhaustiveMPTCalculator implements IMPTCalculator {

	private class SubSetIterator {
		
		private final List<EventType> eventList;
		private int counter = 0;
		
		public SubSetIterator(Set<EventType> set) {
			this.eventList = new ArrayList<EventType>(set);
		}
		
		public Set<EventType> getNextSubSet() {
			Set<EventType> result = new HashSet<EventType>();
			int currValue = counter++;
			if (currValue >= Math.pow(2, eventList.size())) {
				return null;
			}
			for (EventType eventType : eventList) {
				if (currValue % 2 == 1) {
					result.add(eventType);
				}
				currValue /= 2;
			}
			return result;
		}
	}
	
	private IAlgoUnit algoUnit;
	
	public ExhaustiveMPTCalculator(IAlgoUnit algoUnit) {
		this.algoUnit = algoUnit;
	}

	@Override
	public MultiPlan calculateMultiPlan(MultiPatternGraph graph) {
		MultiPlan initialPlan = LocalSearchMPTCalculator.createLocallyOptimalState(graph, algoUnit);
		Set<CompositePattern> patterns = graph.getAllPatterns();
		return recursiveCalculateBestTree(graph, initialPlan, patterns);
	}
	
	private MultiPlan recursiveCalculateBestTree(MultiPatternGraph graph, MultiPlan initialPlan, 
												 Set<CompositePattern> patterns) {
		if (patterns.isEmpty()) {
			return initialPlan;
		}
		Double bestCost = algoUnit.getMultiPlanCost(initialPlan);
		MultiPlan bestPlan = algoUnit.instantiateMultiPlan(initialPlan);
		CompositePattern currentPattern = patterns.iterator().next();
		Set<Set<EventType>> maxSubSets = graph.getPatternPeerMaxSubSets(currentPattern);
		Set<CompositePattern> restOfThePatterns = new HashSet<CompositePattern>(patterns);
		restOfThePatterns.remove(currentPattern);
		Set<EvaluationPlan> attemptedPlans = new HashSet<EvaluationPlan>();
		for (Set<EventType> maxSubSet : maxSubSets) {
			SubSetIterator subSetIterator = new SubSetIterator(maxSubSet);
			Set<EventType> currentSubSet = subSetIterator.getNextSubSet();
			while (currentSubSet != null) {
				EvaluationPlan fullPlan;
				if (currentSubSet.isEmpty()) {
					fullPlan = algoUnit.calculateEvaluationPlan(currentPattern);
				}
				else {
					CompositePattern subPattern = currentPattern.getSubPattern(currentSubSet);
					EvaluationPlan subPlan = algoUnit.calculateEvaluationPlan(subPattern);
					fullPlan = algoUnit.calculateFullEvaluationPlan(currentPattern, subPlan, null);
				}
				if (!attemptedPlans.contains(fullPlan)) {
					attemptedPlans.add(fullPlan);
					MultiPlan candidatePlan = algoUnit.instantiateMultiPlan(initialPlan);
					candidatePlan.replacePatternPlan(currentPattern, fullPlan);
					candidatePlan = recursiveCalculateBestTree(graph, initialPlan, restOfThePatterns);
					Double candidatePlanCost = algoUnit.getMultiPlanCost(candidatePlan);
					if (candidatePlanCost < bestCost) {
						bestCost = candidatePlanCost;
						bestPlan = candidatePlan;
					}
				}
				currentSubSet = subSetIterator.getNextSubSet();
			}
		}
		return bestPlan;
	}

	@Override
	public IAlgoUnit getAlgoUnit() {
		return algoUnit;
	}

	@Override
	public MultiPlan improveMultiPlan(MultiPlan multiPlan) {
		throw new RuntimeException("Unsupported operation");
	}

}
