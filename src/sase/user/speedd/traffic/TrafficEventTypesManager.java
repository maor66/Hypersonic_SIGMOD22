package sase.user.speedd.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.EventTypesManager;

public class TrafficEventTypesManager extends EventTypesManager {

	public static final String vehicleIDAttributeName = "Vehicle ID";
	public static final String timestampAttributeName = "Timestamp";
	public static final String xCoordinateAttributeName = "X Coordinate";
	public static final String yCoordinateAttributeName = "Y Coordinate";
	public static final String speedAttributeName = "Speed";
	public static final String sectionAttributeName = "Section";
	
	public static final int vehicleIDAttributeIndex = 0;
	public static final int timestampAttributeIndex = 1;
	public static final int xCoordinateAttributeIndex = 2;
	public static final int yCoordinateAttributeIndex = 3;
	public static final int speedAttributeIndex = 4;
	public static final int sectionAttributeIndex = 5;

	private static final int maxAttributeIndex = sectionAttributeIndex;
	
	public static EventType veryLowSpeedEventType;
	public static EventType lowSpeedEventType;
	public static EventType mediumSpeedEventType;
	public static EventType highSpeedEventType;
	public static EventType veryHighSpeedEventType;

	public static final String veryLowSpeedEventTypeName = "VeryLowSpeed";
	public static final String lowSpeedEventTypeName = "LowSpeed";
	public static final String mediumSpeedEventTypeName = "MediumSpeed";
	public static final String highSpeedEventTypeName = "HighSpeed";
	public static final String veryHighSpeedEventTypeName = "VeryHighSpeed";
	
	public TrafficEventTypesManager() {
	}

	@Override
	public String getEventLabel(Event event) {
		return String.format("%d:%f:%f",
							 (Long)event.getAttributeValue(vehicleIDAttributeIndex),
							 (Double)event.getAttributeValue(xCoordinateAttributeIndex),
							 (Double)event.getAttributeValue(yCoordinateAttributeIndex));
	}

	@Override
	public Long getEventTimestamp(Event event) {
		return (Long)event.getAttributeValue(timestampAttributeIndex);
	}

	@Override
	public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
		Object[] newPayload = new Object[payload.length];
		newPayload[vehicleIDAttributeIndex] = 
						Long.parseLong(((String)payload[vehicleIDAttributeIndex]).replaceAll("\"", ""));
		newPayload[timestampAttributeIndex] = 
						Long.parseLong(((String)payload[timestampAttributeIndex]).replaceAll("\"", "")) / 10;
		newPayload[xCoordinateAttributeIndex] = 
						Double.parseDouble(((String)payload[xCoordinateAttributeIndex]).replaceAll("\"", ""));
		newPayload[yCoordinateAttributeIndex] = 
						Double.parseDouble(((String)payload[yCoordinateAttributeIndex]).replaceAll("\"", ""));
		newPayload[speedAttributeIndex] = 
						Double.parseDouble(((String)payload[speedAttributeIndex]).replaceAll("\"", ""));
		newPayload[sectionAttributeIndex] = 
						Integer.parseInt(((String)payload[sectionAttributeIndex]).replaceAll("\"", ""));
		return newPayload;
	}

	@Override
	public long getAverageEventSize() {
		//three integers and three doubles
		return 3*4 + 3*8;
	}

	@Override
	public List<EventType> getKnownEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(veryLowSpeedEventType);
		result.add(lowSpeedEventType);
		result.add(mediumSpeedEventType);
		result.add(highSpeedEventType);
		result.add(veryHighSpeedEventType);
		return result;
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		List<String> result = new ArrayList<String>();
		result.add(veryLowSpeedEventTypeName);
		result.add(lowSpeedEventTypeName);
		result.add(mediumSpeedEventTypeName);
		result.add(highSpeedEventTypeName);
		result.add(veryHighSpeedEventTypeName);
		return result;
	}

	@Override
	protected void actuallyInitializeTypes() {
		Attribute[] attributes = new Attribute[maxAttributeIndex+1];
		attributes[vehicleIDAttributeIndex] = new Attribute(Datatype.LONG, vehicleIDAttributeName);
		attributes[timestampAttributeIndex] = new Attribute(Datatype.LONG, timestampAttributeName);
		attributes[xCoordinateAttributeIndex] = new Attribute(Datatype.DOUBLE, xCoordinateAttributeName);
		attributes[yCoordinateAttributeIndex] = new Attribute(Datatype.DOUBLE, yCoordinateAttributeName);
		attributes[speedAttributeIndex] = new Attribute(Datatype.DOUBLE, speedAttributeName);
		attributes[sectionAttributeIndex] = new Attribute(Datatype.LONG, sectionAttributeName);
		
		veryLowSpeedEventType = new EventType(veryLowSpeedEventTypeName, attributes);
		lowSpeedEventType = new EventType(lowSpeedEventTypeName, attributes);
		mediumSpeedEventType = new EventType(mediumSpeedEventTypeName, attributes);
		highSpeedEventType = new EventType(highSpeedEventTypeName, attributes);
		veryHighSpeedEventType = new EventType(veryHighSpeedEventTypeName, attributes);
	}

	@Override
	protected HashMap<String, EventType> createNameToTypeHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		
		result.put(veryLowSpeedEventTypeName, veryLowSpeedEventType);
		result.put(lowSpeedEventTypeName, lowSpeedEventType);
		result.put(mediumSpeedEventTypeName, mediumSpeedEventType);
		result.put(highSpeedEventTypeName, highSpeedEventType);
		result.put(veryHighSpeedEventTypeName, veryHighSpeedEventType);
			
		return result;
	}

	@Override
	protected HashMap<String, String> createLongNameToShortNameHash() {
		HashMap<String, String> result = new HashMap<String, String>();
		
		result.put(veryLowSpeedEventTypeName, "LOWEST");
		result.put(lowSpeedEventTypeName, "LOW");
		result.put(mediumSpeedEventTypeName, "MEDIUM");
		result.put(highSpeedEventTypeName, "HIGH");
		result.put(veryHighSpeedEventTypeName, "HIGHEST");	
		return result;
	}

	@Override
	public List<String> getAllFusedTypeNames() {
		throw new RuntimeException("Unimplemented");
	}

}
