package evaluation.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.AggregatedEvent;
import base.Event;
import base.EventType;
import pattern.EventTypesManager;

public class EventBuffer {

	private final List<Event> events;
	private final HashMap<EventType, AggregatedEvent> typeToAggregatedEvent;
	private Long earliestTimestamp = null;
	private Long latestTimestamp = null;
	
	public EventBuffer(List<EventType> iterativeEventTypes) {
		this(iterativeEventTypes, null);
	}
	
	public EventBuffer(List<EventType> iterativeEventTypes, List<Event> events) {
		this.events = (events == null) ? new ArrayList<Event>() : events;
		if (events != null) {
			for (Event event : events) {
				updateSystemTimestamps(event);
			}
		}
		typeToAggregatedEvent = new HashMap<EventType, AggregatedEvent>();
		if (iterativeEventTypes == null) {
			return;
		}
		for (EventType eventType : iterativeEventTypes) {
			typeToAggregatedEvent.put(eventType, null);
		}
	}
	
	private void addPrimitiveEvent(Event event) {
		EventType eventType = event.getType();
		if (typeToAggregatedEvent.containsKey(eventType)) {
			//this event is to be aggregated
			if (typeToAggregatedEvent.get(eventType) == null) {
				AggregatedEvent aggregatedEvent = new AggregatedEvent(eventType);
				events.add(aggregatedEvent);
				typeToAggregatedEvent.put(eventType, aggregatedEvent);
			}
			typeToAggregatedEvent.get(eventType).addPrimitiveEvent(event);
		}
		else {
			//ordinary event
			events.add(event);
		}
	}
	
	private void addAggregatedEvent(AggregatedEvent event) {
		EventType eventType = event.getType();
		if (!typeToAggregatedEvent.containsKey(eventType)) {
			throw new RuntimeException(String.format("Unknown iterative type %s", eventType));
		}
		if (typeToAggregatedEvent.get(eventType) == null) {
			typeToAggregatedEvent.put(eventType, event);
			events.add(event);
		}
		else {
			typeToAggregatedEvent.get(eventType).addAggregatedEvent(event);
		}
	}
	
	public void addEvent(Event event) {
		if (event instanceof AggregatedEvent) {
			addAggregatedEvent((AggregatedEvent)event);
		}
		else {
			addPrimitiveEvent(event);
		}
		updateSystemTimestamps(event);
	}
	
	private void updateSystemTimestamps(Event event) {
		Long timestamp = event.getSystemTimestamp();
		if (earliestTimestamp == null || timestamp < earliestTimestamp) {
			earliestTimestamp = timestamp;
		}
		if (latestTimestamp == null || timestamp > latestTimestamp) {
			latestTimestamp = timestamp;
		}
	}
	
	public void addEvents(List<Event> events) {
		if (events == null) {
			return;
		}
		for (Event event : events) {
			addEvent(event);
		}
	}

	public List<Event> getEvents() {
		return events;
	}
	
	public Event getEventByType(EventType type) {
		for (Event event : events) {
			if (event.getType() == type) {
				return event;
			}
		}
		return null;
	}
	
	@Override
	public EventBuffer clone() {
		EventBuffer clonedEventBuffer = new EventBuffer(null);
		for (Event event : events) {
			if (event instanceof AggregatedEvent) {
				AggregatedEvent clonedEvent = ((AggregatedEvent)event).clone();
				clonedEventBuffer.events.add(clonedEvent);
				clonedEventBuffer.typeToAggregatedEvent.put(event.getType(), clonedEvent);
			}
			else {
				clonedEventBuffer.events.add(event);
			}
		}
		for (EventType aggregatedEventType : typeToAggregatedEvent.keySet()) {
			if (!clonedEventBuffer.typeToAggregatedEvent.containsKey(aggregatedEventType)) {
				clonedEventBuffer.typeToAggregatedEvent.put(aggregatedEventType, null);
			}
		}
		clonedEventBuffer.setEarliestTimestamp(earliestTimestamp);
		clonedEventBuffer.setLatestTimestamp(latestTimestamp);
		return clonedEventBuffer;
	}
	
	public long size() {
		return events.size() * EventTypesManager.getInstance().getAverageEventSize();
	}

	public Long getEarliestTimestamp() {
		return earliestTimestamp;
	}

	public Long getLatestTimestamp() {
		return latestTimestamp;
	}

	public void setEarliestTimestamp(Long earliestTimestamp) {
		this.earliestTimestamp = earliestTimestamp;
	}

	public void setLatestTimestamp(Long latestTimestamp) {
		this.latestTimestamp = latestTimestamp;
	}
	
	
}
