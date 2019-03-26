package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public final class HirzelEvaluationSpecification extends ParallelEvaluationSpecification {

	public String attribute;
	
	public HirzelEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification specification,
			int num_of_threads, String attribute) {
		super(type, specification, num_of_threads);
		this.attribute = attribute;
	}
}
