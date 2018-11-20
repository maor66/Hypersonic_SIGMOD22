package sase.multi.calculator.local.neighborhood;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;

public class NeighborhoodFactory {

	public static LocalSearchNeighborhood createNeighborhood(NeighborhoodTypes type, 
													  		 IAlgoUnit algoUnit,
													  		 MultiPlan initialState,
													  		 int multiSetParameter, 
													  		 Long timeLimit) {
		switch (type) {
			case MULTI_SET:
				return new MultiSetShareNeighborhood(algoUnit, initialState, timeLimit, multiSetParameter);
			case PAIRWISE:
				return new PairwiseShareNeighborhood(algoUnit, initialState, timeLimit);
			case STATE_SWAP:
				return new StateSwapNeighborhood(algoUnit, initialState, timeLimit);
			default:
				throw new RuntimeException("Unexpected neighborhood type");
		}
	}
}
