package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public abstract class ParallelEvaluationSpecification extends EvaluationSpecification {
	
	// Specification for thread NFA
	public EvaluationSpecification internalSpecification;
	public int num_of_threeads;
	
	public ParallelEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification internalSpecification, int num_of_threads) {
		super(type);
		this.internalSpecification = internalSpecification;
		this.num_of_threeads = num_of_threads;
	}

}
