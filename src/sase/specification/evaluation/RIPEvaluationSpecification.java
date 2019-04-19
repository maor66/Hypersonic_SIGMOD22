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
	
	@Override
	public String getShortDescription() {
		return String.format("%s|%d|%d", type, eventsPerThread, windowSize);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (ordering algorithm %s, events per thread %d, window size %d)",
				 			 type, eventsPerThread, windowSize);
	}
}
