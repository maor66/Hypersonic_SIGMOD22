package sase.user.sensors;

import sase.base.EventType;
import sase.input.EventTypesConverter;
import sase.pattern.EventTypesManager;
import sase.specification.SimulationSpecification;

import java.util.HashMap;
import java.util.List;

public class SensorsByActivityEventTypesConverter extends EventTypesConverter {
    private HashMap<String, EventType> nameToTypeHash;

    public SensorsByActivityEventTypesConverter(SimulationSpecification simulationSpecification) {
        super(simulationSpecification);
        nameToTypeHash = new HashMap<>();
        List<EventType> knownEventTypes = EventTypesManager.getInstance().getKnownEventTypes();
        for (EventType eventType : knownEventTypes) {
            nameToTypeHash.put(eventType.getName(), eventType);
        }
    }

    @Override
    public EventType convertToKnownEventType(String[] rawEvent) {
        String activityLabel = rawEvent[SensorEventTypeManager.labelAttributeIndex];
        return nameToTypeHash.get(activityLabel);
    }
}
