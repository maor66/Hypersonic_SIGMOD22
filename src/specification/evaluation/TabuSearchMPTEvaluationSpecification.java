package sase.specification.evaluation;

import sase.multi.calculator.MPTCalculatorTypes;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;
import sase.specification.algo.AlgoUnitSpecification;

public class TabuSearchMPTEvaluationSpecification extends LocalSearchMPTEvaluationSpecification {

	public int maxStepsSinceImprovement;
	public int neighborsPerStep;
	public int memorySize;
	
	public TabuSearchMPTEvaluationSpecification(AlgoUnitSpecification algoUnitSpecification,
												NeighborhoodTypes neighborhoodType, Long timeLimitInSeconds, 
												int multiSetParameter, int maxStepsSinceImprovement, 
												int neighborsPerStep, int memorySize) {
		super(MPTCalculatorTypes.TABU_SEARCH, algoUnitSpecification, neighborhoodType, timeLimitInSeconds, multiSetParameter);
		this.maxStepsSinceImprovement = maxStepsSinceImprovement;
		this.neighborsPerStep = neighborsPerStep;
		this.memorySize = memorySize;
	}

	@Override
	public String getShortDescription() {
		return String.format("%s{%d,%d,%d}", 
							 super.getShortDescription(), maxStepsSinceImprovement, neighborsPerStep, memorySize);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (max steps since improvement: %d, neighbors per step: %d, memory size: %d)",
				 super.getLongDescription(), maxStepsSinceImprovement, neighborsPerStep, memorySize);
	}

}
