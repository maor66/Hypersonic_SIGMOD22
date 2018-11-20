package sase.multi.calculator;

import sase.multi.MultiPatternGraph;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;

public interface IMPTCalculator {

	public MultiPlan calculateMultiPlan(MultiPatternGraph graph);
	public MultiPlan improveMultiPlan(MultiPlan multiPlan);
	public IAlgoUnit getAlgoUnit();
}
