package sase.order.algorithm.adaptive.greedy;

import sase.adaptive.estimation.SlidingWindowEventArrivalRateEstimator;
import sase.adaptive.monitoring.invariant.IInvariantCalculator;
import sase.adaptive.monitoring.invariant.InvariantInput;
import sase.simulator.Environment;

public class GreedyInvariantCalculator implements IInvariantCalculator {

	private double prefixCardinality;
	
	public GreedyInvariantCalculator(double prefixCardinality) {
		this.prefixCardinality = prefixCardinality;
	}
	
	@Override
	public double calculateInvariantValue(InvariantInput input) {
		GreedyInvariantInput greedyInvariantInput = (GreedyInvariantInput)input;
		SlidingWindowEventArrivalRateEstimator eventRateEstimator = Environment.getEnvironment().getEventRateEstimator();
		double eventTypeArrivalRate = eventRateEstimator.getEventRateEstimate(greedyInvariantInput.eventType);
		Double conditionSelectivity = greedyInvariantInput.condition == null ? 
																	1.0 : greedyInvariantInput.condition.getSelectivity();
		return prefixCardinality * eventTypeArrivalRate * conditionSelectivity;
	}

}
