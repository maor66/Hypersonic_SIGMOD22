package sase.evaluation.data_parallel;

import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.specification.evaluation.ParallelEvaluationSpecification;

public final class RIPEvaluationMechanism extends DataParallelEvaluationMechanism {

	public RIPEvaluationMechanism(Pattern pattern, ParallelEvaluationSpecification specification,
			EvaluationPlan evaluationPlan) {
		super(pattern, specification, evaluationPlan);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void scheduleEvent(EvaluationInput evaluationInput) {
		// TODO Auto-generated method stub
		
	}
	
	
}
