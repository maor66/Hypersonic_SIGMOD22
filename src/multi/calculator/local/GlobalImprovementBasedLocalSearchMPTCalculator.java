package sase.multi.calculator.local;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.local.neighborhood.LocalSearchNeighborhood;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;

public abstract class GlobalImprovementBasedLocalSearchMPTCalculator extends LocalSearchMPTCalculator {

	protected final int maxStepsSinceImprovement;
	
	public GlobalImprovementBasedLocalSearchMPTCalculator(NeighborhoodTypes neighborhoodType,
			IAlgoUnit algoUnit, Long timeLimitInSeconds,
			int multiSetParameter, int maxStepsSinceImprovement) {
		super(neighborhoodType, algoUnit, timeLimitInSeconds, multiSetParameter);
		this.maxStepsSinceImprovement = maxStepsSinceImprovement;
	}

	@Override
	protected MultiPlan executeLocalSearch(MultiPlan initialState, LocalSearchNeighborhood neighborhood) {
		neighborhood.setCurrentState(initialState);
		MultiPlan bestState = initialState;
		Double currentBestCost = algoUnit.getMultiPlanCost(initialState);
		long initialTime = System.currentTimeMillis();
		initializeLocalSearch(initialState, neighborhood);
		for (int i = 0; i < maxStepsSinceImprovement; ) {
			MultiPlan currentlySelectedNeighbor = executeLocalSearchStep(neighborhood, initialTime);
			if (currentlySelectedNeighbor == null) {
				//neighborhood exhausted or no state could be selected - start over
				neighborhood.setCurrentState(initialState);
				continue;
			}
			Double neighborCost = algoUnit.getMultiPlanCost(currentlySelectedNeighbor);
			if (neighborCost < currentBestCost) {
				currentBestCost = neighborCost;
				bestState = currentlySelectedNeighbor;
				i = 0;
			}
			else {
				++i;
			}
			if (System.currentTimeMillis() - initialTime > timeLimit) {
				break;//time is up
			}
		}
		return bestState;
	}

	protected abstract void initializeLocalSearch(MultiPlan initialState, LocalSearchNeighborhood neighborhood);
	protected abstract MultiPlan executeLocalSearchStep(LocalSearchNeighborhood neighborhood, long initialTime);

}
