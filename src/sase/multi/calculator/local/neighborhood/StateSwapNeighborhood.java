package sase.multi.calculator.local.neighborhood;

import java.util.Collection;
import java.util.Random;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class StateSwapNeighborhood extends LocalSearchNeighborhood {

	public StateSwapNeighborhood(IAlgoUnit algoUnit, MultiPlan initialState, Long timeLimit) {
		super(algoUnit, initialState, timeLimit);
	}

	@Override
	protected MultiPlan actuallyGetNextNeighbor() {
		Collection<CompositePattern> patterns = currentState.getPatterns();
		Random random = new Random();
		CompositePattern randomPattern = getObjectAtIndexFromCollection(patterns, random.nextInt(patterns.size()));
		MultiPlan nextNeighbor = algoUnit.instantiateMultiPlan(currentState);
		algoUnit.swapRandomEventTypes(randomPattern, nextNeighbor);
		return nextNeighbor;
	}

}
