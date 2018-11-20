package sase.multi.calculator.local;

import java.util.Random;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.local.neighborhood.LocalSearchNeighborhood;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;

public class SimulatedAnnealingMPTCalculator extends GlobalImprovementBasedLocalSearchMPTCalculator {

	private final double alpha;
	private final int numOfNeighborsForTempInit;
	
	private double currentCost;
	private double currentTemperature;
	private Random random;
	
	public SimulatedAnnealingMPTCalculator(NeighborhoodTypes neighborhoodType, IAlgoUnit algoUnit,
										   Long timeLimitInSeconds, int multiSetParameter, int maxStepsSinceImprovement,
										   double alpha, int numOfNeighborsForTempInit) {
		super(neighborhoodType, algoUnit, timeLimitInSeconds, multiSetParameter, maxStepsSinceImprovement);
		this.alpha = alpha;
		this.numOfNeighborsForTempInit = numOfNeighborsForTempInit;
	}

	@Override
	protected void initializeLocalSearch(MultiPlan initialState, LocalSearchNeighborhood neighborhood) {
		currentCost = algoUnit.getMultiPlanCost(initialState);
		calculateInitialTemperature(neighborhood);
		random = new Random();
	}

	@Override
	protected MultiPlan executeLocalSearchStep(LocalSearchNeighborhood neighborhood, long initialTime) {
		MultiPlan currentNeighbor = neighborhood.getNextNeighbor(initialTime);
		if (currentNeighbor == null) {
			return null;
		}
		Double neighborCost = algoUnit.getMultiPlanCost(currentNeighbor);
		if (neighborCost < currentCost || Math.exp((currentCost - neighborCost) / currentTemperature) > random.nextDouble()) {
			neighborhood.setCurrentState(currentNeighbor);
			currentCost = neighborCost;
		}
		currentTemperature *= alpha;
		return currentNeighbor;
	}
	
	private void calculateInitialTemperature(LocalSearchNeighborhood neighborhood) {
		double initialCost = algoUnit.getMultiPlanCost(neighborhood.getCurrentState());
		double minCost = initialCost;
		double maxCost = initialCost;
		for (int i = 0; i < numOfNeighborsForTempInit; ++i) {
			MultiPlan currentNeighbor = neighborhood.getNextNeighbor();
			if (currentNeighbor == null) {
				break;
			}
			double currentCost = algoUnit.getMultiPlanCost(currentNeighbor);
			if (currentCost < minCost) {
				minCost = currentCost;
			}
			else if (currentCost > maxCost) {
				maxCost = currentCost;
			}
		}
		currentTemperature = maxCost - minCost;
	}

}
