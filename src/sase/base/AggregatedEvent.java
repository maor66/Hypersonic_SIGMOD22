package sase.base;

import java.util.ArrayList;
import java.util.List;

import sase.aggregator.VectorAggregator;

public class AggregatedEvent extends Event {

	private final List<Event> events;

	public AggregatedEvent(EventType type) {
		this(type, null);
	}
	
	public AggregatedEvent(List<Event> events) {
		this(events.get(0).getType(), events);
	}
	
	public AggregatedEvent(EventType type, List<Event> events) {
		super(type, null);
		this.events = events == null ? new ArrayList<Event>() : new ArrayList<Event>(events);
		
		if (this.events.size() > 0) {
			payload = this.events.get(0).getSignature();
		}
	}
	
	public void addPrimitiveEvent(Event event) {
		if (event.getType() != type) {
			throw new RuntimeException(String.format("Attempted to add illegal event %s to aggregated event %s",
													 event, this));
		}
		if (events.size() == 0) {
			//this is the first event to be added
			payload = event.getSignature();
		}
		events.add(event);
	}
	
	public void addAggregatedEvent(AggregatedEvent aggregatedEvent) {
		events.addAll(aggregatedEvent.events);
	}
	
	public List<Event> getPrimitiveEvents() {
		return events;
	}
	
	public void aggregatePrimitiveEvents(VectorAggregator aggregator) {
		List<Object[]> primitiveEventsPayloads = new ArrayList<Object[]>();
		for (Event event : events) {
			primitiveEventsPayloads.add(event.payload);
		}
		payload = aggregator.aggregateVectors(primitiveEventsPayloads);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AggregatedEvent)) {
			return false;
		}
		AggregatedEvent otherAggregatedEvent = (AggregatedEvent)other;
		for (Event event : events) {
			if (!(otherAggregatedEvent.events.contains(event))) {
				return false;
			}
		}
		for (Event event : otherAggregatedEvent.events) {
			if (!(events.contains(event))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public AggregatedEvent clone() {
		return new AggregatedEvent(type, events);
	}
	
	@Override
	public String toString() {
		return String.format("%s(Aggregated, %d events)", type, events.size());
	}

}
