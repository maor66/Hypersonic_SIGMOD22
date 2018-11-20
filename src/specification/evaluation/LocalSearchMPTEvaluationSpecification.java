package sase.specification.evaluation;

import sase.multi.calculator.MPTCalculatorTypes;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;
import sase.specification.algo.AlgoUnitSpecification;

public class LocalSearchMPTEvaluationSpecification extends MultiPlanEvaluationSpecification {

	public NeighborhoodTypes neighborhoodType;
	public Long timeLimitInSeconds;
	public int multiSetParameter;
	
	public LocalSearchMPTEvaluationSpecification(MPTCalculatorTypes mptCalculatorType,
												 AlgoUnitSpecification algoUnitSpecification,
												 NeighborhoodTypes neighborhoodType,
												 Long timeLimitInSeconds,
												 int multiSetParameter) {
		super(mptCalculatorType, algoUnitSpecification);
		this.neighborhoodType = neighborhoodType;
		this.timeLimitInSeconds = timeLimitInSeconds;
		this.multiSetParameter = multiSetParameter;
	}

	@Override
	public String getShortDescription() {
		return String.format("%s{%s(%s)|%ssec}",
							 super.getShortDescription(), neighborhoodType, multiSetParameter, timeLimitInSeconds);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s, neighborhood %s(%s), time limit %s seconds", 
							 super.getLongDescription(), neighborhoodType, multiSetParameter, timeLimitInSeconds);
	}

}
