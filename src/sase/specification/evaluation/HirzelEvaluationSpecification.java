package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public final class HirzelEvaluationSpecification extends ParallelEvaluationSpecification {

	public String attribute;
	
	public HirzelEvaluationSpecification(EvaluationMechanismTypes type, EvaluationSpecification specification,
			int num_of_threads, String attribute) {
		super(type, specification, num_of_threads);
		this.attribute = attribute;
	}
	
	@Override
	public String getShortDescription() {
		return String.format("%s|%s", type, attribute);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (attribute %s)", type, attribute);
	}
}
