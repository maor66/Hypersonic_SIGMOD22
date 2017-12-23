package user.traffic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import base.Attribute;
import base.Datatype;
import base.Event;
import base.EventType;
import pattern.EventTypesManager;

public class AarhusTrafficEventTypesManager extends EventTypesManager {

	public static final String pointIDAttributeName = "Point ID";
	public static final String timestampAttributeName = "Timestamp";
	public static final String averageSpeedAttributeName = "Average Speed";
	public static final String numberOfVehiclesAttributeName = "Number Of Vehicles";
	
	public static final int pointIDAttributeIndex = 0;
	public static final int timestampAttributeIndex = 1;
	public static final int averageSpeedAttributeIndex = 2;
	public static final int numberOfVehiclesAttributeIndex = 3;

	private static final int maxAttributeIndex = numberOfVehiclesAttributeIndex;
	
	private static final int[] vehicleNumberBounds = {1, 10, 15, 20, 25, 30, 35, 45};
	private static EventType[] eventTypes = new EventType[vehicleNumberBounds.length];
	private static String[] eventTypeNames = new String[vehicleNumberBounds.length];
	
	public AarhusTrafficEventTypesManager() {
		for (int i = 0; i < vehicleNumberBounds.length - 1; ++i) {
			eventTypeNames[i] = String.format("%d-%d", vehicleNumberBounds[i], vehicleNumberBounds[i+1] - 1);
		}
		eventTypeNames[vehicleNumberBounds.length-1] = String.format("%d+", vehicleNumberBounds[vehicleNumberBounds.length-1]);
	}

	public static int[] getVehicleNumberBounds() {
		return vehicleNumberBounds;
	}

	public static EventType[] getEventTypes() {
		return eventTypes;
	}

	public static String[] getEventTypeNames() {
		return eventTypeNames;
	}

	@Override
	public String getEventLabel(Event event) {
		return String.format("%d:%d:%d",
							 (Integer)event.getAttributeValue(pointIDAttributeIndex),
							 (Integer)event.getAttributeValue(averageSpeedAttributeIndex),
							 (Integer)event.getAttributeValue(numberOfVehiclesAttributeIndex));
	}

	@Override
	public Long getEventTimestamp(Event event) {
		return (Long)event.getAttributeValue(timestampAttributeIndex);
	}

	@Override
	public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
		Object[] newPayload = new Object[payload.length];
		newPayload[pointIDAttributeIndex] = Integer.parseInt((String)payload[pointIDAttributeIndex]);
		newPayload[timestampAttributeIndex] = Long.parseLong((String)payload[timestampAttributeIndex]);
		newPayload[averageSpeedAttributeIndex] = Integer.parseInt((String)payload[averageSpeedAttributeIndex]);
		newPayload[numberOfVehiclesAttributeIndex] = Integer.parseInt((String)payload[numberOfVehiclesAttributeIndex]);
		return newPayload;
	}

	@Override
	public long getAverageEventSize() {
		//three integers and one long
		return 3*4 + 8;
	}

	@Override
	public List<EventType> getKnownEventTypes() {
		return Arrays.asList(eventTypes);
	}

	@Override
	protected void actuallyInitializeTypes() {
		Attribute[] attributes = new Attribute[maxAttributeIndex+1];
		attributes[pointIDAttributeIndex] = new Attribute(Datatype.LONG, pointIDAttributeName);
		attributes[timestampAttributeIndex] = new Attribute(Datatype.LONG, timestampAttributeName);
		attributes[averageSpeedAttributeIndex] = new Attribute(Datatype.LONG, averageSpeedAttributeName);
		attributes[numberOfVehiclesAttributeIndex] = new Attribute(Datatype.LONG, numberOfVehiclesAttributeName);
		
		for (int i = 0; i < eventTypeNames.length; ++i) {
			eventTypes[i] = new EventType(eventTypeNames[i], attributes);
		}
	}

	@Override
	protected HashMap<String, EventType> createNameToTypeHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		for (int i = 0; i < eventTypeNames.length; ++i) {
			result.put(eventTypeNames[i], eventTypes[i]);
		}			
		return result;
	}

	@Override
	protected HashMap<String, String> createLongNameToShortNameHash() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < eventTypeNames.length; ++i) {
			result.put(eventTypeNames[i], eventTypeNames[i]);
		}			
		return result;
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		return Arrays.asList(getEventTypeNames());
	}

}
