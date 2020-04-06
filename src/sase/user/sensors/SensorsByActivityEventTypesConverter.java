package sase.user.sensors;

import sase.base.EventType;
import sase.input.EventTypesConverter;
import sase.specification.SimulationSpecification;

public class SensorsByActivityEventTypesConverter extends EventTypesConverter {
    public SensorsByActivityEventTypesConverter(SimulationSpecification simulationSpecification) {
        super(simulationSpecification);
    }

    @Override
    public EventType convertToKnownEventType(String[] rawEvent) {
        return null;
    }
}
