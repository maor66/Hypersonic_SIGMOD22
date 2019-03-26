package sase.adaptive.monitoring;

import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.simulator.Environment;

public class PlanCreatorBasedAdaptationNecessityDetector implements IAdaptationNecessityDetector {

	private Pattern pattern;
	private EvaluationPlan evaluationPlan = null;
	
	public PlanCreatorBasedAdaptationNecessityDetector(Pattern pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public boolean shouldAdapt() {
		if (evaluationPlan == null) {
			evaluationPlan = Environment.getEnvironment().getEvaluationPlanCreator().createEvaluationPlan(pattern);
			return false;
		}
		EvaluationPlan newEvaluationPlan = 
				Environment.getEnvironment().getEvaluationPlanCreator().createEvaluationPlan(pattern);
		if (!evaluationPlan.toString().equals(newEvaluationPlan.toString())) {
			evaluationPlan = newEvaluationPlan;
			return true;
		}
		return false;
	}

}
