package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;

public abstract class EvaluationSpecification {

	public EvaluationMechanismTypes type;
	
	public EvaluationSpecification(EvaluationMechanismTypes type) {
		this.type = type;
	}
	
	public String getShortDescription() {
		return type.toString();
	}
	
	public String getLongDescription() {
		return type.toString();
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}
}
