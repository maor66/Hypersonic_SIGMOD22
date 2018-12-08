package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.multi.algo.AlgoUnitTypes;
import sase.multi.calculator.MPTCalculatorTypes;
import sase.specification.algo.AlgoUnitSpecification;

public class MultiPlanEvaluationSpecification extends EvaluationSpecification {

	public MPTCalculatorTypes mptCalculatorType;
	public AlgoUnitSpecification algoUnitSpecification;
	
	public MultiPlanEvaluationSpecification(MPTCalculatorTypes mptCalculatorType, AlgoUnitSpecification algoUnitSpecification) {
		super(algoUnitSpecification.type == AlgoUnitTypes.ORDER ? EvaluationMechanismTypes.MULTI_PATTERN_TREE : 
																  EvaluationMechanismTypes.MULTI_PATTERN_MULTI_TREE);
		this.mptCalculatorType = mptCalculatorType;
		this.algoUnitSpecification = algoUnitSpecification;
	}

	@Override
	public String getShortDescription() {
		return String.format("%s|%s|%s", super.getShortDescription(), algoUnitSpecification, mptCalculatorType);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("Multi-pattern evaluation (multi-algorithm %s, single-algorithm %s)", 
							 mptCalculatorType, algoUnitSpecification.type);
	}
}
