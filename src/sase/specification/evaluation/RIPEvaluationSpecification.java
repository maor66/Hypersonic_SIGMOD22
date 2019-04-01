package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public final class RIPEvaluationSpecification extends ParallelEvaluationSpecification {

	private long events_per_thread;
	private long window_size;
	
	public RIPEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification specification,
			int num_of_threads, long events_per_thread, long window_size) {
		super(type, specification, num_of_threads);
		this.events_per_thread = events_per_thread;
		this.window_size = window_size;
	}

}
