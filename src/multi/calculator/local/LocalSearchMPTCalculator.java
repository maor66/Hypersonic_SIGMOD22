package sase.multi.calculator.local;

import java.util.Set;

import sase.multi.MultiPatternGraph;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.IMPTCalculator;
import sase.multi.calculator.local.neighborhood.LocalSearchNeighborhood;
import sase.multi.calculator.local.neighborhood.NeighborhoodFactory;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;
import sase.pattern.CompositePattern;

public abstract class LocalSearchMPTCalculator implements IMPTCalculator {

	public static MultiPlan createLocallyOptimalState(MultiPatternGraph graph, IAlgoUnit algoUnit) {
		Set<CompositePattern> patterns = graph.getAllPatterns();
		MultiPlan result = algoUnit.instantiateMultiPlan();
		for (CompositePattern pattern : patterns) {
			result.addPatternPlan(pattern, algoUnit.calculateEvaluationPlan(pattern));
		}
		result.setGraph(graph);
		return result;
	}
	
	protected final IAlgoUnit algoUnit;
	protected final NeighborhoodTypes neighborhoodType;
	protected final Long timeLimit;
	private final int multiSetParameter;
	
	public LocalSearchMPTCalculator(NeighborhoodTypes neighborhoodType, IAlgoUnit algoUnit,
									Long timeLimitInSeconds, int multiSetParameter) {
		this.neighborhoodType = neighborhoodType;
		this.algoUnit = algoUnit;
		this.timeLimit = timeLimitInSeconds * 1000;
		this.multiSetParameter = multiSetParameter;
	}
	
	@Override
	public MultiPlan calculateMultiPlan(MultiPatternGraph graph) {
		return improveMultiPlan(createLocallyOptimalState(graph, algoUnit));
		
	}
	
	@Override
	public MultiPlan improveMultiPlan(MultiPlan multiPlan) {
		LocalSearchNeighborhood neighborhood = NeighborhoodFactory.createNeighborhood(neighborhoodType, algoUnit,
					  																  multiPlan, multiSetParameter, timeLimit);
		return executeLocalSearch(multiPlan, neighborhood);
	}

	@Override
	public IAlgoUnit getAlgoUnit() {
		return algoUnit;
	}
	
	protected abstract MultiPlan executeLocalSearch(MultiPlan initialState, LocalSearchNeighborhood neighborhood);

}
