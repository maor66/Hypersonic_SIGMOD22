package sase.evaluation.plan;

public abstract class EvaluationPlan {

	protected Double cost;
	
	public Double getCost() {
		return cost;
	}
	
	public void setCost(Double cost) {
		this.cost = cost;
	}

	public abstract Object getRepresentation();
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EvaluationPlan)) {
			return false;
		}
		EvaluationPlan otherEvaluationPlan = (EvaluationPlan)other;
		return getRepresentation().equals(otherEvaluationPlan.getRepresentation());
	}
	
	@Override
	public int hashCode() {
		return getRepresentation().hashCode();
	}
}
