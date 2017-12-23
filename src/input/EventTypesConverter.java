package input;

import base.EventType;
import specification.PatternSpecification;
import specification.SimulationSpecification;

public abstract class EventTypesConverter {
	
	protected final PatternSpecification patternSpecification;
	
	public EventTypesConverter(SimulationSpecification simulationSpecification) {
		patternSpecification = simulationSpecification.getPatternSpecification();
	}

	public abstract EventType convertToKnownEventType(String[] rawEvent);
}
