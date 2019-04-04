package sase.base;

import sase.pattern.EventTypesManager;

import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Comparable<Event>, ContainsEvent {
	
	private static final int signatureSize = 2;
	private static long eventCounter = 0;
	
	public static void resetCounter() {
		eventCounter = 0;
	}
	
	public static Object[] getEventSignature(Object[] eventPayload) {
		Object[] result = new Object[signatureSize];
		for (int i = 0; i < signatureSize; ++i) {
			result[i] = eventPayload[i];
		}
		return result;
	}
	
	public static List<Event> asList(Event event) {
		List result = new ArrayList<Event>();
		result.add(event);
		return result;
	}

	protected final long sequenceNumber;
	protected EventType type;
	protected final long systemTimestamp;
	protected Object[] payload;
	private long timestamp = 0;
	private boolean isLastInput = false;

	public Event(EventType type, Object[] payload) {
		this.sequenceNumber = eventCounter++;
		this.type = type;
		this.systemTimestamp = System.currentTimeMillis();
		this.payload = payload == null ? null : 
										 EventTypesManager.getInstance().convertStringPayloadToObjectPayload(payload);
	}

	public Event()
	{
		this(null, null);
		isLastInput = true;
	}
	
	private Event(Event event) {
		this.sequenceNumber = event.sequenceNumber;
		this.type = event.type;
		this.systemTimestamp = event.systemTimestamp;
		this.payload = event.payload.clone();
		this.timestamp = event.timestamp;
		this.isLastInput = event.isLastInput;
	}
	
	public Event clone() {
		return new Event(this);
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Object[] getAttributes() {
		return payload;
	}
	
	private int findAttributeByName(String attributeName) {
		for (int i = 0; i < type.getAttributes().size(); ++i) {
			if (type.getAttributes().get(i).getName().equals(attributeName)) {
				return i;
			}
		}
		return -1;
	}
	
	public Object getAttributeValue(String attributeName) {
		int attributeIndex = findAttributeByName(attributeName);
		return attributeIndex == -1 ? null : getAttributeValue(attributeIndex);
	}
	
	public Object getAttributeValue(int attributeIndex) {
		return payload[attributeIndex];
	}
	
	public String getLabel() {
		return EventTypesManager.getInstance().getEventLabel(this);
	}

	@Override
	public long getTimestamp() {

		if (timestamp!=0) {
			return timestamp;
		}
		Long l = EventTypesManager.getInstance().getEventTimestamp(this);
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddkkmm");
		try {
			Date d = f.parse(l.toString());
			timestamp = d.getTime()/1000/60;
			return timestamp;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}



	public Object[] getSignature() {
		return getEventSignature(payload);
	}

	public long getSystemTimestamp() {
		return systemTimestamp;
	}

	@Override
	public long getSequenceNumber() {
		return sequenceNumber;
	}

	@Override
	public long getEarliestTimestamp() { //The earliest timestamp is the same as the event's timestamp
		return getTimestamp();
	}

    @Override
    public boolean isLastInput() {
        return isLastInput;
    }


	@Override
	public String toString() {
		return String.format("%s%d:%d:%d", type.getName(), sequenceNumber, getTimestamp(),EventTypesManager.getInstance().getEventTimestamp(this));
		/*String result = String.format("%s:", type);
		for (int i = 0; i < payload.length; ++i) {
			result += payload[i];
			if (i < payload.length - 1) {
				result += ",";
			}
		}
		return result;*/
	}

	@Override
	public int compareTo(Event e) {
		return new Long(sequenceNumber - e.sequenceNumber).intValue();
	}

}
