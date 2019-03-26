package sase.multi.calculator.local;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.local.neighborhood.LocalSearchNeighborhood;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;

public class IterativeImprovementMPTCalculator extends LocalSearchMPTCalculator {

	private int maxSteps;
	private int maxNeighbors;
	
	public IterativeImprovementMPTCalculator(NeighborhoodTypes neighborhoodType, 
											 IAlgoUnit algoUnit, Long timeLimit,
											 int multiSetParameter, int maxSteps, int maxNeighbors) {
		super(neighborhoodType, algoUnit, timeLimit, multiSetParameter);
		this.maxSteps = maxSteps;
		this.maxNeighbors = maxNeighbors;
	}

	@Override
	protected MultiPlan executeLocalSearch(MultiPlan initialState, LocalSearchNeighborhood neighborhood) {
		Double currentBestCost = algoUnit.getMultiPlanCost(initialState);
		neighborhood.setCurrentState(initialState);
		long initialTime = System.currentTimeMillis();
		for (int i = 0, j = 0; i < maxSteps && j < maxNeighbors; ++i) {
			MultiPlan currentNeighbor = neighborhood.getNextNeighbor(initialTime);
			if (currentNeighbor == null) {
				//neighborhood exhausted - a local minimum was found
				break;
			}
			Double neighborCost = algoUnit.getMultiPlanCost(currentNeighbor);
			if (neighborCost < currentBestCost) {
				neighborhood.setCurrentState(currentNeighbor);
				currentBestCost = neighborCost;
				j = 0;
			}
			else {
				++j;
			}
			if (System.currentTimeMillis() - initialTime > timeLimit) {
				break;//time is up
			}
		}
		return neighborhood.getCurrentState();
	}

}
