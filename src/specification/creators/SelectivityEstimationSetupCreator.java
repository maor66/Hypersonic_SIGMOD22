package sase.specification.creators;

import java.util.ArrayList;
import java.util.List;

import sase.specification.SimulationSpecification;

public abstract class SelectivityEstimationSetupCreator implements ISimulationSpecificationCreator {
	
	@Override
	public SimulationSpecification[] createSpecifications() {
		String[] eventTypeNames = getEventTypeNames();
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		for (int i = 0; i < eventTypeNames.length; ++i) {
			for (int j = i + 1; j < eventTypeNames.length; ++j) {
				result.addAll(createSpecificationsForEventTypePair(eventTypeNames[i], eventTypeNames[j]));
				result.addAll(createSpecificationsForEventTypePair(eventTypeNames[j], eventTypeNames[i]));
			}
		}
		return result.toArray(new SimulationSpecification[0]);
	}

	protected abstract String[] getEventTypeNames();
	protected abstract List<SimulationSpecification> createSpecificationsForEventTypePair(String firstEventName, 
																						  String secondEventName);

}
