package sase.user.traffic;

import sase.base.EventType;
import sase.input.EventTypesConverter;
import sase.specification.SimulationSpecification;

public class AarhusTrafficEventTypesConverter extends EventTypesConverter {

	public AarhusTrafficEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}
	
	private EventType getEventTypeByNumberOfVehicles(Integer numberOfVehicles) {
		int[] vehicleNumberBounds = AarhusTrafficEventTypesManager.getVehicleNumberBounds();
		for (int i = 0; i < vehicleNumberBounds.length - 1; ++i) {
			if (numberOfVehicles < vehicleNumberBounds[i+1]) {
				return AarhusTrafficEventTypesManager.getEventTypes()[i];
			}
		}
		return AarhusTrafficEventTypesManager.getEventTypes()[vehicleNumberBounds.length-1];
	}
	
	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		Integer numberOfVehicles = 
				Integer.parseInt(rawEvent[AarhusTrafficEventTypesManager.numberOfVehiclesAttributeIndex]);
		return getEventTypeByNumberOfVehicles(numberOfVehicles);
	}

}
