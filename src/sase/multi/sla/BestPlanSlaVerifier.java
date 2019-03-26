package sase.multi.sla;

import sase.evaluation.plan.EvaluationPlan;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class BestPlanSlaVerifier implements ISlaVerifier {

	@Override
	public boolean verifySlaRequirements(CompositePattern pattern, MultiPlan globalPlan, IAlgoUnit algoUnit) {
		EvaluationPlan bestPlanForPattern = algoUnit.calculateEvaluationPlan(pattern);
		EvaluationPlan employedPlanForPattern = globalPlan.getPlanForPattern(pattern);
		return algoUnit.getPlanCost(pattern, employedPlanForPattern) <= algoUnit.getPlanCost(pattern, bestPlanForPattern);
	}

}
