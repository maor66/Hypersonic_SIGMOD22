package user.speedd.traffic;

import base.EventType;
import input.EventTypesConverter;
import specification.SimulationSpecification;

public class TrafficSpeedEventTypesConverter extends EventTypesConverter {

	private static final Double veryHighSpeedThreshold = 100.0;
	private static final Double highSpeedThreshold = 80.0;
	private static final Double mediumSpeedThreshold = 60.0;
	private static final Double lowSpeedThreshold = 40.0;

	public TrafficSpeedEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}
	
	private EventType getEventTypeBySpeed(Double speed) {
		int numberOfEventTypes = patternSpecification.getNumberOfEventTypes();
		if (speed >= veryHighSpeedThreshold) {
			return TrafficEventTypesManager.veryHighSpeedEventType;
		}
		if (speed >= highSpeedThreshold) {
			return numberOfEventTypes < 5 ? (numberOfEventTypes < 4 ? TrafficEventTypesManager.lowSpeedEventType : 
																	  TrafficEventTypesManager.mediumSpeedEventType) : 
											TrafficEventTypesManager.highSpeedEventType;
		}
		if (speed >= mediumSpeedThreshold) {
			return numberOfEventTypes < 4 ? TrafficEventTypesManager.lowSpeedEventType : 
											TrafficEventTypesManager.mediumSpeedEventType;
		}
		if (speed >= lowSpeedThreshold) {
			return TrafficEventTypesManager.lowSpeedEventType;
		}
		return TrafficEventTypesManager.veryLowSpeedEventType;
	}
	
	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		Double speed = Double.parseDouble(rawEvent[TrafficEventTypesManager.speedAttributeIndex].replaceAll("\"", ""));
		return getEventTypeBySpeed(speed);
	}

}
