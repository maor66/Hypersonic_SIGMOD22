package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public abstract class ParallelEvaluationSpecification extends EvaluationSpecification {
	
	// Specification for thread NFA
	public EvaluationSpecification internalSpecification;
	public int numOfThreads;
	
	public ParallelEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification internalSpecification, int numOfThreads) {
		super(type);
		this.internalSpecification = internalSpecification;
		this.numOfThreads = numOfThreads;
	}

}
