package sase.multi.calculator;

import java.util.Set;

import sase.multi.MultiPatternGraph;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class NoSharingMPTCalculator implements IMPTCalculator {

	private IAlgoUnit algoUnit;
	
	public NoSharingMPTCalculator(IAlgoUnit algoUnit) {
		this.algoUnit = algoUnit;
	}

	@Override
	public MultiPlan calculateMultiPlan(MultiPatternGraph graph) {
		Set<CompositePattern> patterns = graph.getAllPatterns();
		MultiPlan result = algoUnit.instantiateMultiPlan(false);
		for (CompositePattern pattern : patterns) {
			result.addPatternPlan(pattern, algoUnit.calculateEvaluationPlan(pattern));
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
