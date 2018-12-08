package sase.evaluation.nfa.lazy.order.algorithm.adaptive.greedy;

import sase.adaptive.monitoring.invariant.IInvariantCalculator;
import sase.adaptive.monitoring.invariant.InvariantInput;

public class GreedyInvariantCalculator implements IInvariantCalculator {

	private double prefixCardinality;
	
	public GreedyInvariantCalculator(double prefixCardinality) {
		this.prefixCardinality = prefixCardinality;
	}
	
	@Override
	public double calculateInvariantValue(InvariantInput input) {
		GreedyInvariantInput greedyInvariantInput = (GreedyInvariantInput)input;
		double eventTypeArrivalRate = greedyInvariantInput.eventType.getRate();
		Double conditionSelectivity = greedyInvariantInput.condition == null ? 
																	1.0 : greedyInvariantInput.condition.getSelectivity();
		return prefixCardinality * eventTypeArrivalRate * conditionSelectivity;
	}

}
