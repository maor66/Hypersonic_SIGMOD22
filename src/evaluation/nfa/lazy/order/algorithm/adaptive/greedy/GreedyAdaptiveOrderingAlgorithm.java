package sase.evaluation.nfa.lazy.order.algorithm.adaptive.greedy;

import java.util.ArrayList;
import java.util.List;

import sase.adaptive.monitoring.invariant.Invariant;
import sase.adaptive.monitoring.invariant.InvariantAdaptationNecessityDetector;
import sase.adaptive.monitoring.invariant.compare.InvariantComparer.ComparisonType;
import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.algorithm.GreedyCostModelOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.evaluation.nfa.lazy.order.cost.ThroughputCostModel;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.base.CNFCondition;
import sase.simulator.Environment;

public class GreedyAdaptiveOrderingAlgorithm implements IOrderingAlgorithm {

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		if (!isInvariantBasedAdaptationEnabled()) {
			return new GreedyCostModelOrderingAlgorithm().calculateEvaluationOrder(pattern, new ThroughputCostModel());
		}
		if (pattern.getType() != PatternOperatorTypes.SEQ && pattern.getType() != PatternOperatorTypes.AND_SEQ) {
			throw new RuntimeException("Only sequence/conjunction patterns order calculation is currently supported.");
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		List<EventType> availableTypes = new ArrayList<EventType>(pattern.getEventTypes());
		List<EventType> result = new ArrayList<EventType>();
		List<Double> prefixCardinalities = new ArrayList<Double>();
		List<Invariant> invariants = new ArrayList<Invariant>();
		while (!availableTypes.isEmpty()) {
			EventType nextType = selectNextEventType(compositePattern, costModel, result, availableTypes,
													 prefixCardinalities, invariants);
			availableTypes.remove(nextType);
			result.add(nextType);
		}
		InvariantAdaptationNecessityDetector detector = 
				(InvariantAdaptationNecessityDetector)Environment.getEnvironment().getAdaptationNecessityDetector();
		detector.setInvariants(invariants);
		return result;
	}
	
	private boolean isInvariantBasedAdaptationEnabled() {
		return (Environment.getEnvironment().getAdaptationNecessityDetector() instanceof InvariantAdaptationNecessityDetector);
	}

	protected EventType selectNextEventType(CompositePattern compositePattern, ICostModel costModel,
										  List<EventType> prefix, List<EventType> remainingTypes,
										  List<Double> prefixCardinalities, List<Invariant> invariants) {
		if (prefix.isEmpty()) {
			return selectFirstEventType(remainingTypes, prefixCardinalities, invariants);
		}
		EventType currentBestEventType = null;
		Double currentLowestCardinality = null;
		CNFCondition currentBestCondition = null;
		List<Invariant> currentStageInvariants = new ArrayList<Invariant>();
		List<EventType> allEventTypes = compositePattern.getEventTypes();
		List<EventType> typesToExclude = new ArrayList<EventType>(allEventTypes);
		for (EventType eventType : prefix) {
			typesToExclude.remove(eventType);
		}
		CNFCondition mainCondition = (CNFCondition)compositePattern.getCondition();
		for (EventType candidateEventType : remainingTypes) {
			List<EventType> currTypesToExclude = new ArrayList<EventType>(typesToExclude);
			currTypesToExclude.remove(candidateEventType);
			CNFCondition conditionForCandidateType = 
					mainCondition.getConditionExcludingTypes(currTypesToExclude).getConditionForType(candidateEventType, false);
			GreedyInvariantCalculator calculator = 
					new GreedyInvariantCalculator(prefixCardinalities.get(prefixCardinalities.size() - 1));
			Double currentCardinality =
					calculator.calculateInvariantValue(new GreedyInvariantInput(candidateEventType,
																				conditionForCandidateType));
			if (testAndCreateEventTypeInvariant(candidateEventType, currentBestEventType, 
												conditionForCandidateType, currentBestCondition,
												currentCardinality, currentLowestCardinality,
												prefixCardinalities, currentStageInvariants)) {
				currentBestEventType = candidateEventType;
				currentLowestCardinality = currentCardinality;
				currentBestCondition = conditionForCandidateType;
			}
		}
		prefixCardinalities.add(currentLowestCardinality.doubleValue());
		if (!currentStageInvariants.isEmpty()) {
			invariants.add(getTightestInvariant(currentStageInvariants, currentBestEventType));
		}
		return currentBestEventType;
	}

	private EventType selectFirstEventType(List<EventType> remainingTypes,
										   List<Double> prefixCardinalities,
										   List<Invariant> invariants) {
		EventType currentBestEventType = null;
		Double currentLowestRate = null;
		List<Invariant> currentStageInvariants = new ArrayList<Invariant>();
		for (EventType eventType : remainingTypes) {
			Double currentRate = eventType.getRate();
			if (testAndCreateEventTypeInvariant(eventType, currentBestEventType, null, null,
												currentRate.doubleValue(), currentLowestRate.doubleValue(),
												prefixCardinalities, currentStageInvariants)) {
				currentBestEventType = eventType;
				currentLowestRate = currentRate;
			}
		}
		prefixCardinalities.add(currentLowestRate.doubleValue());
		if (!currentStageInvariants.isEmpty()) {
			invariants.add(getTightestInvariant(currentStageInvariants, currentBestEventType));
		}
		return currentBestEventType;
	}
	
	private Invariant getTightestInvariant(List<Invariant> invariants, EventType eventType) {
		Double currentDistance = null;
		Invariant currentInvariant = null;
		for (Invariant invariant : invariants) {
			GreedyInvariantInput leftInput = (GreedyInvariantInput) invariant.getLeftInput();
			if (leftInput.eventType != eventType) {
				GreedyInvariantInput rightInput = (GreedyInvariantInput) invariant.getRightInput();
				if (rightInput.eventType != eventType) {
					continue;
				}
			}
			if (currentInvariant == null || invariant.getValueDistance() < currentDistance) {
				currentInvariant = invariant;
				currentDistance = invariant.getValueDistance();
			}
		}
		return currentInvariant;
	}
	
	private boolean testAndCreateEventTypeInvariant(EventType leftType, EventType rightType,
													CNFCondition leftCondition, CNFCondition rightCondition,
													Double leftCardinality, Double rightCardinality,
													List<Double> prefixCardinalities,
													List<Invariant> invariants) {
		if (leftType == null || rightType == null) {
			return true;
		}
		GreedyInvariantInput leftInput = new GreedyInvariantInput(leftType, leftCondition);
		GreedyInvariantInput rightInput = new GreedyInvariantInput(rightType, rightCondition);
		GreedyInvariantCalculator calculator = 
							new GreedyInvariantCalculator(prefixCardinalities.isEmpty() ? 1.0 : 
														  prefixCardinalities.get(prefixCardinalities.size() - 1));
		ComparisonType comparisonType;
		boolean result = false;
		if (leftCardinality < rightCardinality) {
			result = true;
			comparisonType = ComparisonType.LESS;
		}
		else if (leftCardinality > rightCardinality) {
			comparisonType = ComparisonType.MORE;
		}
		else {
			comparisonType = ComparisonType.EQUALS;
		}
		InvariantAdaptationNecessityDetector detector = 
				(InvariantAdaptationNecessityDetector)Environment.getEnvironment().getAdaptationNecessityDetector();
		Invariant invariant = new Invariant(leftInput, rightInput, calculator,
											detector.getComparerFactory().createInvariantComparer(comparisonType));
		invariants.add(invariant);
		return result;
	}

}
