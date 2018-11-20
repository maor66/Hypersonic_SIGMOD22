package sase.specification.evaluation;

import sase.multi.calculator.MPTCalculatorTypes;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;
import sase.specification.algo.AlgoUnitSpecification;

public class IterativeImprovementMPTEvaluationSpecification extends LocalSearchMPTEvaluationSpecification {

	public int maxSteps;
	public int maxNeighbors;
	
	public IterativeImprovementMPTEvaluationSpecification(AlgoUnitSpecification algoUnitSpecification, Long timeLimitInSeconds,
														  int multiSetParameter, NeighborhoodTypes neighborhoodType, 
														  int maxSteps, int maxNeighbors) {
		super(MPTCalculatorTypes.ITERATIVE_IMPROVEMENT, algoUnitSpecification, 
			  neighborhoodType, timeLimitInSeconds, multiSetParameter);
		this.maxSteps = maxSteps;
		this.maxNeighbors = maxNeighbors;
	}

	@Override
	public String getShortDescription() {
		return String.format("%s{%d,%d}", super.getShortDescription(), maxSteps, maxNeighbors);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (max steps: %d, max neighbors: %d}", super.getLongDescription(), maxSteps, maxNeighbors);
	}

}
