package sase.user.synthetic;

import sase.base.EventType;
import sase.input.EventTypesConverter;
import sase.specification.SimulationSpecification;

public class SyntheticEventTypesConverter extends EventTypesConverter {

	public SyntheticEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}

	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		return SyntheticEventTypesManager.getEventTypes()[Integer.parseInt(rawEvent[0])];
	}

}
