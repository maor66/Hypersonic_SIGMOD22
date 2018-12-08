package sase.user.trams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.EventTypesManager;

public class TramEventTypesManager extends EventTypesManager {

	public static final String timestampAttributeName = "Timestamp";
	public static final String lineNumbersAttributeName = "LineNumbers";
	public static final int timestampAttributeIndex = 0;
	public static final int lineNumbersAttributeIndex = 1;
	public static final String lineNumbersSeparator = ":";

	//tram congestion pattern type names
	public static final String normalTrafficEventTypeName = "NormalTraffic";
	public static final String lightCongestionEventTypeName = "LightCongestion";
	public static final String mediumCongestionEventTypeName = "MediumCongestion";
	public static final String severeCongestionEventTypeName = "SevereCongestion";
	public static final String heavyCongestionEventTypeName = "HeavyCongestion";
	
	//tram congestion pattern types
	public static EventType normalTrafficEventType;
	public static EventType lightCongestionEventType;
	public static EventType mediumCongestionEventType;
	public static EventType severeCongestionEventType;
	public static EventType heavyCongestionEventType;
	
	public TramEventTypesManager() {
	}
	
	@Override
	protected HashMap<String, EventType> createNameToTypeHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		
		result.put(normalTrafficEventTypeName, normalTrafficEventType);
		result.put(lightCongestionEventTypeName, lightCongestionEventType);
		result.put(mediumCongestionEventTypeName, mediumCongestionEventType);
		result.put(severeCongestionEventTypeName, severeCongestionEventType);
		result.put(heavyCongestionEventTypeName, heavyCongestionEventType);
			
		return result;
	}
	
	@Override
	protected HashMap<String, String> createLongNameToShortNameHash() {
		HashMap<String, String> result = new HashMap<String, String>();
		
		result.put(normalTrafficEventTypeName, "NORM");
		result.put(lightCongestionEventTypeName, "LIGHT");
		result.put(mediumCongestionEventTypeName, "MEDIUM");
		result.put(severeCongestionEventTypeName, "SEVERE");
		result.put(heavyCongestionEventTypeName, "HEAVY");	
		return result;
	}
	
	@Override
	protected void actuallyInitializeTypes() {
		Attribute[] attributes = new Attribute[2];
		attributes[timestampAttributeIndex] = new Attribute(Datatype.LONG, timestampAttributeName);
		attributes[lineNumbersAttributeIndex] = new Attribute(Datatype.TEXT, lineNumbersAttributeName);
		
		normalTrafficEventType = new EventType(normalTrafficEventTypeName, attributes);
		lightCongestionEventType = new EventType(lightCongestionEventTypeName, attributes);
		mediumCongestionEventType = new EventType(mediumCongestionEventTypeName, attributes);
		severeCongestionEventType = new EventType(severeCongestionEventTypeName, attributes);
		heavyCongestionEventType = new EventType(heavyCongestionEventTypeName, attributes);
	}

	@Override
	public String getEventLabel(Event event) {
		return (String)event.getAttributeValue(lineNumbersAttributeName);
	}
	
	@Override
	public Long getEventTimestamp(Event event) {
		return (Long)event.getAttributeValue(timestampAttributeIndex);
	}
	
	@Override
	public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
		Object[] newPayload = new Object[payload.length];
		try {
			newPayload[timestampAttributeIndex] = 
				new SimpleDateFormat("yyyyMMddHHmmss").parse((String)payload[timestampAttributeIndex]).getTime() / 1000;
		} catch (ParseException e) {
			throw new RuntimeException("Illegal date format");
		}
		newPayload[lineNumbersAttributeIndex] = payload[lineNumbersAttributeIndex];
		return newPayload;
	}
	
	@Override
	public long getAverageEventSize() {
		//timestamp is 8 bytes (long) and line numbers string is of average length of 10 characters
		return 8 + 2*10;
	}
	
	@Override
	public List<EventType> getKnownEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(normalTrafficEventType);
		result.add(lightCongestionEventType);
		result.add(mediumCongestionEventType);
		result.add(severeCongestionEventType);
		result.add(heavyCongestionEventType);
		return result;
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		List<String> result = new ArrayList<String>();
		result.add(normalTrafficEventTypeName);
		result.add(lightCongestionEventTypeName);
		result.add(mediumCongestionEventTypeName);
		result.add(severeCongestionEventTypeName);
		result.add(heavyCongestionEventTypeName);
		return result;
	}
}
