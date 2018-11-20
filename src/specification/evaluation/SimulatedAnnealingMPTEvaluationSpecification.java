package sase.specification.evaluation;

import sase.multi.calculator.MPTCalculatorTypes;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;
import sase.specification.algo.AlgoUnitSpecification;

public class SimulatedAnnealingMPTEvaluationSpecification extends LocalSearchMPTEvaluationSpecification {

	public int maxStepsSinceImprovement;
	public double alpha;
	public int numOfNeighborsForTempInit;
	
	public SimulatedAnnealingMPTEvaluationSpecification(AlgoUnitSpecification algoUnitSpecification, 
														NeighborhoodTypes neighborhoodType,
														Long timeLimitInSeconds, int multiSetParameter,
														int maxStepsSinceImprovement, double alpha, 
														int numOfNeighborsForTempInit) {
		super(MPTCalculatorTypes.SIMULATED_ANNEALING, algoUnitSpecification, 
			  neighborhoodType, timeLimitInSeconds, multiSetParameter);
		this.maxStepsSinceImprovement = maxStepsSinceImprovement;
		this.alpha = alpha;
		this.numOfNeighborsForTempInit = numOfNeighborsForTempInit;
	}

	@Override
	public String getShortDescription() {
		return String.format("%s{%d,%f,%d}", 
							 super.getShortDescription(), maxStepsSinceImprovement, alpha, numOfNeighborsForTempInit);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (max steps since improvement: %d, alpha: %f, neighbors for init: %d)", 
				 super.getLongDescription(), maxStepsSinceImprovement, alpha, numOfNeighborsForTempInit);
	}
}
