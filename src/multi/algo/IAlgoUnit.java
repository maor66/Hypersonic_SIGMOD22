package sase.multi.algo;

import sase.evaluation.plan.EvaluationPlan;
import sase.multi.MultiPlan;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;

public interface IAlgoUnit {

	public EvaluationPlan calculateEvaluationPlan(Pattern pattern);
	public EvaluationPlan calculateFullEvaluationPlan(Pattern pattern, EvaluationPlan subPlan, MultiPlan currentMultiPlan);
	public EvaluationPlan getTrivialEvaluationPlan(Pattern pattern);
	
	public Double getPlanCost(Pattern pattern, EvaluationPlan plan);
	public Double getMultiPlanCost(MultiPlan multiPlan);
	
	public MultiPlan instantiateMultiPlan();
	public MultiPlan instantiateMultiPlan(boolean enableSharing);
	public MultiPlan instantiateMultiPlan(MultiPlan source);
	
	//TODO: This is a dirty hack! Think of ways to get rid of it.
	public void swapRandomEventTypes(CompositePattern pattern, MultiPlan multiPlan);

}
