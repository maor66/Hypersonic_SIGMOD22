package sase.evaluation.plan;

import java.util.HashMap;

import sase.pattern.Pattern;

public class DisjunctionEvaluationPlan extends EvaluationPlan {

	private final HashMap<Pattern, EvaluationPlan> nestedPlans;
	
	public DisjunctionEvaluationPlan(HashMap<Pattern, EvaluationPlan> nestedPlans) {
		this.nestedPlans = nestedPlans;
		Double planCost = 0.0;
		for (EvaluationPlan nestedPlan : this.nestedPlans.values()) {
			planCost += nestedPlan.getCost();
		}
		this.cost = planCost;
	}

	@Override
	public HashMap<Pattern, EvaluationPlan> getRepresentation() {
		return nestedPlans;
	}
	
	@Override
	public String toString() {
		String result = "List of nested plans:\n";
		for (Pattern pattern : nestedPlans.keySet()) {
			result += String.format("Pattern: %s\nPlan: %s\n\n", pattern, nestedPlans.get(pattern));
		}
		return result;
	}

}
