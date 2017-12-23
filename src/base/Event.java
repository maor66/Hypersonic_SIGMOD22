package base;

import pattern.EventTypesManager;

public class Event {
	
	private static final int signatureSize = 2;

	public Event(Event event) {
		this.type = event.type;
		this.systemTimestamp = event.systemTimestamp;
		this.payload = event.payload.clone();
	}

	public static Object[] getEventSignature(Object[] eventPayload) {
		Object[] result = new Object[signatureSize];
		for (int i = 0; i < signatureSize; ++i) {
			result[i] = eventPayload[i];
		}
		return result;
	}

	protected final EventType type;
	protected final long systemTimestamp;
	protected Object[] payload;

	public Event(EventType type, Object[] payload) {
		this.type = type;
		this.systemTimestamp = System.currentTimeMillis();
		this.payload = payload == null ? null : 
										 EventTypesManager.getInstance().convertStringPayloadToObjectPayload(payload);
	}
	
	public EventType getType() {
		return type;
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
	
	@Override
	public String toString() {
		String result = String.format("%s:", type);
		for (int i = 0; i < payload.length; ++i) {
			result += payload[i];
			if (i < payload.length - 1) {
				result += ",";
			}
		}
		return result;
	}

	//TODO: create a clone method

}
