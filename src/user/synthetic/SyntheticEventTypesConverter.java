package user.synthetic;

import base.EventType;
import input.EventTypesConverter;
import specification.SimulationSpecification;

public class SyntheticEventTypesConverter extends EventTypesConverter {

	public SyntheticEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}

	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		return SyntheticEventTypesManager.getEventTypes()[Integer.parseInt(rawEvent[0])];
	}

}
