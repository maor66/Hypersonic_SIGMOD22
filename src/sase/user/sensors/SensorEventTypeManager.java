package sase.user.sensors;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.EventTypesManager;

import java.util.HashMap;
import java.util.List;

public class SensorEventTypeManager extends EventTypesManager {
    public static final int labelAttributeIndex = 34;
    public static final int timestampAttributeIndex = 0;
    public static final int firstSensorIDdataIndex = 1;
    public static final int complexityDataIndex = 7;
    public static final int activityChangeIndex = 8;
    public static final int areaTransitionsIndex = 9;
    public static final int numSensorsIndex = 10;
    public static final int weightedSensorCountFirstIndex = 10;

    @Override
    public String getEventLabel(Event event) {
        return (String)event.getAttributeValue(labelAttributeIndex);
    }

    @Override
    public Long getEventTimestamp(Event event) {
        return (Long)event.getAttributeValue(timestampAttributeIndex);
    }

    @Override
    public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
        Object[] newPayload = new Object[payload.length];
        newPayload[timestampAttributeIndex] = Long.valueOf((String)payload[timestampAttributeIndex]);
        for (int i = firstSensorIDdataIndex; i < complexityDataIndex; ++i) { //Integers
                newPayload[i] = Integer.valueOf((String)payload[i]);
        }
        newPayload[complexityDataIndex] = Double.valueOf((String)payload[complexityDataIndex]);
        newPayload[activityChangeIndex] = Double.valueOf((String)payload[activityChangeIndex]);
        newPayload[areaTransitionsIndex] = Integer.valueOf((String)payload[areaTransitionsIndex]);
        newPayload[numSensorsIndex] = Integer.valueOf((String)payload[numSensorsIndex]);
        for (int i = weightedSensorCountFirstIndex; i < labelAttributeIndex; i++) {
            newPayload[i] = Double.valueOf((String) payload[i]);
        }
        newPayload[labelAttributeIndex] = payload[labelAttributeIndex];
        return newPayload;
    }
    

    @Override
    public long getAverageEventSize() {
        return 0;
    }

    @Override
    public List<EventType> getKnownEventTypes() {
        return null;
    }

    @Override
    public List<String> getKnownEventTypeNames() {
        return null;
    }

    @Override
    protected void actuallyInitializeTypes() {

    }

    @Override
    protected HashMap<String, EventType> createNameToTypeHash() {
        return null;
    }

    @Override
    protected HashMap<String, String> createLongNameToShortNameHash() {
        return null;
    }
}
