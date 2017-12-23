package simulator;

import java.util.HashMap;

import statistics.StatisticsManager;

public class SimulationHistory {

	private HashMap<String, StatisticsManager> simulations;
	
	public SimulationHistory() {
		simulations = new HashMap<String, StatisticsManager>();
	}
	
	public void registerSimulation(String simulationId, StatisticsManager simulationData) {
		if (simulations.containsKey(simulationId)) {
			return;
		}
		simulations.put(simulationId, simulationData);
	}
	
	public StatisticsManager getStatisticsManagerForSimulation(String simulationId) {
		return simulations.get(simulationId);
	}

}
