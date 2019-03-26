package sase.multi.calculator;

import java.util.Set;

import sase.multi.MultiPatternGraph;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class NoReorderingMPTCalculator implements IMPTCalculator {

	private IAlgoUnit algoUnit;
	
	public NoReorderingMPTCalculator(IAlgoUnit algoUnit) {
		this.algoUnit = algoUnit;
	}

	@Override
	public MultiPlan calculateMultiPlan(MultiPatternGraph graph) {
		Set<CompositePattern> patterns = graph.getAllPatterns();
		MultiPlan result = algoUnit.instantiateMultiPlan(true);
		for (CompositePattern pattern : patterns) {
			result.addPatternPlan(pattern, algoUnit.getTrivialEvaluationPlan(pattern));
		}
		result.setGraph(graph);
		return result;
	}

	@Override
	public IAlgoUnit getAlgoUnit() {
		return algoUnit;
	}

	@Override
	public MultiPlan improveMultiPlan(MultiPlan multiPlan) {
		throw new RuntimeException("Unsupported operation");
	}

}
