package sase.evaluation.nfa.lazy.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.CompositePattern;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class EfficientInputBuffer {

	private class TypeBuffer {
		
		private static final int initialSize = 10000;
		
		private EventType type;
		private List<Event> events;
		
		public TypeBuffer(EventType type, int size) {
			this.type = type;			
			events = new ArrayList<Event>(size);
		}
		
		public void store(Event event) {
			events.add(event);
		}
		
		public void storeAll(List<Event> eventsToStore) {
			events.addAll(eventsToStore);
		}

		private int getIndexWithClosestTimestamp(Long timestamp, boolean fromAbove) {
			int minIndex = 0, maxIndex = events.size() - 1;
			int midIndex = (maxIndex + minIndex) / 2;
			Long currTimestamp = null;
			while (maxIndex >= minIndex)
			{
				midIndex = (maxIndex + minIndex) / 2;
				Event currEvent = events.get(midIndex); 
				currTimestamp = currEvent.getSequenceNumber(); 
				if (currTimestamp == timestamp)
					return midIndex;
				if (currTimestamp < timestamp)
					minIndex = midIndex + 1;
				else
					maxIndex = midIndex - 1;
			}
			if (currTimestamp < timestamp) {
				return fromAbove ? Math.min(midIndex + 1, events.size()) : midIndex;
			}
			else {
				return fromAbove ? midIndex : Math.max(midIndex - 1, 0);
			}
		}
		
		public List<Event> getSlice(Long startNumber, Long endNumber) {
			if (events.isEmpty())
				return events;
			int startIndex = startNumber == null ? 
					0 : getIndexWithClosestTimestamp(startNumber, true);
			int endIndex = endNumber == null ? 
					events.size() : getIndexWithClosestTimestamp(endNumber, true);
			if (startIndex > endIndex)
				return new ArrayList<Event>();
			List<Event> result = events.subList(startIndex, endIndex);
			return result;
		}
		
		public void refresh(Long currentTime) {
			if (events.isEmpty())
				return;
			int numberOfRemovedEvents = 0;
			Event currEvent = events.get(0);
			while (currEvent.getTimestamp() + timeWindow < currentTime) {
				events.remove(0);
				numberOfRemovedEvents++;
				if (events.isEmpty()) {
					break;
				}
				currEvent = events.get(0);
			}
			Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferRemovals,
																		 					  numberOfRemovedEvents);
		}
		
		public int numberOfEvents() {
			return events.size();
		}

		public EventType getType() {
			return type;
		}
		
		public List<Event> getAllEvents() {
			return events;
		}
	}

	private HashMap<EventType, TypeBuffer> events;
	private long timeWindow;
	
	public EfficientInputBuffer(Pattern pattern) {
		this(pattern, false);
	}
	
	public EfficientInputBuffer(Pattern pattern, boolean negativeOnly) {
		this(negativeOnly ? ((CompositePattern)pattern).getNegativeEventTypes() : pattern.getEventTypes(), 
			 pattern.getTimeWindow());
	}
	
	public EfficientInputBuffer(List<EventType> targetEventTypes, long timeWindow) {
		this.timeWindow = timeWindow;
		events = new HashMap<EventType, TypeBuffer>();
		if (targetEventTypes == null) {
			return;
		}
		for (EventType eventType : targetEventTypes) {
			addTypeBuffer(eventType);
		}
	}
	
	public void store(Event event) {
		events.get(event.getType()).store(event);
	}
	
	public void storeAll(List<Event> eventsToStore) {
		if (eventsToStore.isEmpty()) {
			return;
		}
		events.get(eventsToStore.get(0).getType()).storeAll(eventsToStore);
	}

	public void storeAllWithoutCopy(List<Event> eventsToStore) {
		if (eventsToStore.isEmpty()) {
			return;
		}
		TypeBuffer typeBuffer = events.get(eventsToStore.get(0).getType());
		typeBuffer.events = eventsToStore;
	}

	
	public List<Event> getTypeBuffer(EventType type) {
		TypeBuffer buffer = events.get(type);
		if (buffer == null) {
			return null;
		}
		return buffer.getAllEvents();
	}
	
	public boolean hasTypeBuffer(EventType type) {
		return (events.get(type) != null);
	}
	
	public void addTypeBuffer(EventType type) {
		addTypeBuffer(type, TypeBuffer.initialSize);
	}
	
	public void addTypeBuffer(EventType type, int initialSize) {
		TypeBuffer typeBuffer = new TypeBuffer(type, initialSize);
		events.put(typeBuffer.getType(), typeBuffer);
	}
	
	public List<Event> getSlice(EventType type, Event startEvent, Event endEvent) {
		Long startNumber = startEvent == null ? null : startEvent.getSequenceNumber();
		Long endNumber = endEvent == null ? null : endEvent.getSequenceNumber();
		return getSlice(type, startNumber, endNumber);
	}
	
	public List<Event> getSlice(EventType type, Long startNumber, Long endNumber) {
		return events.get(type).getSlice(startNumber, endNumber);
	}
	
	public void refresh(Long currentTime) {
		for (TypeBuffer typeBuffer : events.values()) {
			typeBuffer.refresh(currentTime);
		}
	}
	
	public int numberOfEvents() {
		int size = 0;
		for (TypeBuffer typeBuffer : events.values()) {
			size += typeBuffer.numberOfEvents();
		}
		return size;
	}
	
	public long size() {
		return numberOfEvents() * EventTypesManager.getInstance().getAverageEventSize();
	}
}
