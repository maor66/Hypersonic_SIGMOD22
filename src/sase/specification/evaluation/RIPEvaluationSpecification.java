package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public final class RIPEvaluationSpecification extends ParallelEvaluationSpecification {

	public long eventsPerThread;
	public long windowSize;
	
	public RIPEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification specification,
			int numOfThreads, long eventsPerThread, long windowSize) {
		super(type, specification, numOfThreads);
		this.eventsPerThread = eventsPerThread;
		this.windowSize = windowSize;
	}

}
