package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public final class RIPEvaluationSpecification extends ParallelEvaluationSpecification {

	public double batchesRatio;
	public long totalNumberOfEvents;
	
	public RIPEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification specification,
			int numOfThreads, double batchesRatio, long totalNumberOfEvents) {
		super(type, specification, numOfThreads);
		this.batchesRatio = batchesRatio;
		this.totalNumberOfEvents = totalNumberOfEvents;
	}
	
	@Override
	public String getShortDescription() {
		return String.format("%s|%f|%d", type, batchesRatio, totalNumberOfEvents);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("ordering algorithm %s, batches ratio %f,total number of Events %d",
				 			 type, batchesRatio, totalNumberOfEvents);
	}
}
