package sase.user.synthetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.EventTypesManager;

public class SyntheticEventTypesManager extends EventTypesManager {

	private final int numberOfEventTypes;
	private static EventType[] eventTypes;
	
	public SyntheticEventTypesManager(int numberOfEventTypes) {
		this.numberOfEventTypes = numberOfEventTypes;
		eventTypes = new EventType[numberOfEventTypes];
	}

	@Override
	public String getEventLabel(Event event) {
		return (String)event.getAttributeValue(0);
	}

	@Override
	public Long getEventTimestamp(Event event) {
		return (Long)event.getAttributeValue(1);
	}

	@Override
	public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
		Object[] newPayload = new Object[payload.length];
		newPayload[0] = payload[0];
		newPayload[1] = Long.valueOf((String)payload[1]);
		return newPayload;
	}

	@Override
	public long getAverageEventSize() {
		return 43;//just a random number
	}

	@Override
	public List<EventType> getKnownEventTypes() {
		return Arrays.asList(getEventTypes());
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		List<String> result = new ArrayList<String>();
		for (EventType eventType : eventTypes) {
			result.add(eventType.getName());
		}
		return result;
	}

	@Override
	protected void actuallyInitializeTypes() {
		Attribute[] attributes = new Attribute[2];
		attributes[0] = new Attribute(Datatype.TEXT, "Type");
		attributes[1] = new Attribute(Datatype.LONG, "Timestamp");
		for (int i = 0; i < numberOfEventTypes; ++i) {
			eventTypes[i] = new EventType(new Integer(i).toString(), attributes);
		}
	}

	@Override
	protected HashMap<String, EventType> createNameToTypeHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		for (int i = 0; i < numberOfEventTypes; ++i) {
			result.put(new Integer(i).toString(), eventTypes[i]);
		}
		return result;
	}

	@Override
	protected HashMap<String, String> createLongNameToShortNameHash() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < numberOfEventTypes; ++i) {
			result.put(new Integer(i).toString(), new Integer(i).toString());
		}	
		return result;
	}

	public static EventType[] getEventTypes() {
		return eventTypes;
	}

}
