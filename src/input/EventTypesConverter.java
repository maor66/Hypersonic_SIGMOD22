package sase.input;

import sase.base.EventType;
import sase.specification.PatternSpecification;
import sase.specification.SimulationSpecification;

public abstract class EventTypesConverter {
	
	protected final PatternSpecification patternSpecification;
	
	public EventTypesConverter(SimulationSpecification simulationSpecification) {
		patternSpecification = simulationSpecification.getPatternSpecification();
	}

	public abstract EventType convertToKnownEventType(String[] rawEvent);
}
