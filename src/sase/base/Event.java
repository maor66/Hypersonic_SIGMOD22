package sase.base;

import sase.pattern.EventTypesManager;

public class Event implements Comparable<Event> {
	
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

	protected final long sequenceNumber;
	protected EventType type;
	protected final long systemTimestamp;
	protected Object[] payload;

	public Event(EventType type, Object[] payload) {
		this.sequenceNumber = eventCounter++;
		this.type = type;
		this.systemTimestamp = System.currentTimeMillis();
		this.payload = payload == null ? null : 
										 EventTypesManager.getInstance().convertStringPayloadToObjectPayload(payload);
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
	
	public long getTimestamp() {
		return EventTypesManager.getInstance().getEventTimestamp(this);
	}
	
	public Object[] getSignature() {
		return getEventSignature(payload);
	}

	public long getSystemTimestamp() {
		return systemTimestamp;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}

	@Override
	public String toString() {
		return String.format("%s%d:%d", type.getName(), sequenceNumber, getTimestamp());
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
