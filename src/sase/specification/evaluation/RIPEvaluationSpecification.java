package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public final class RIPEvaluationSpecification extends ParallelEvaluationSpecification {

	private int events_per_thread;
	private int window_size;
	
	public RIPEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification specification,
			int num_of_threads, int events_per_thread, int window_size) {
		super(type, specification, num_of_threads);
		this.events_per_thread = events_per_thread;
		this.window_size = window_size;
	}

}
