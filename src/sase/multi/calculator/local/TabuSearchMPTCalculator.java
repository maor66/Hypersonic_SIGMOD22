package sase.multi.calculator.local;

import java.util.ArrayList;
import java.util.List;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.local.neighborhood.LocalSearchNeighborhood;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;

public class TabuSearchMPTCalculator extends GlobalImprovementBasedLocalSearchMPTCalculator {

	private final int neighborsPerStep;
	private final int memorySize;
	
	private List<MultiPlan> memory;
	
	public TabuSearchMPTCalculator(NeighborhoodTypes neighborhoodType, IAlgoUnit algoUnit,
								   Long timeLimitInSeconds, int multiSetParameter, 
								   int maxStepsSinceImprovement, int neighborsPerStep, int memorySize) {
		super(neighborhoodType, algoUnit, timeLimitInSeconds, multiSetParameter, maxStepsSinceImprovement);
		this.neighborsPerStep = neighborsPerStep;
		this.memorySize = memorySize;
	}

	@Override
	protected void initializeLocalSearch(MultiPlan initialState, LocalSearchNeighborhood neighborhood) {
		this.memory = new ArrayList<MultiPlan>();
	}

	@Override
	protected MultiPlan executeLocalSearchStep(LocalSearchNeighborhood neighborhood, long initialTime) {
		Double locallyBestCost = null;
		MultiPlan locallyBestNeighbor = null;;
		for (int j = 0; j < neighborsPerStep; ++j) {
			MultiPlan currentNeighbor = neighborhood.getNextNeighbor(initialTime);
			if (currentNeighbor == null) {
				return null;
			}
			if (memory.contains(currentNeighbor)) {
				continue;
			}
			Double neighborCost = algoUnit.getMultiPlanCost(currentNeighbor);
			if (locallyBestCost == null || neighborCost < locallyBestCost) {
				locallyBestCost = neighborCost;
				locallyBestNeighbor = currentNeighbor;
			}
		}
		if (locallyBestCost == null) {
			return null;
		}
		neighborhood.setCurrentState(locallyBestNeighbor);
		memory.add(locallyBestNeighbor);
		if (memory.size() > memorySize) {
			memory.remove(0);
		}
		return locallyBestNeighbor;
	}
}
