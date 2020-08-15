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
		if (events != null) {
			long lowestSEQnumber = this.sequenceNumber;
			for (Event e : events) {
				lowestSEQnumber = Math.min(e.sequenceNumber, lowestSEQnumber);
			}
			this.sequenceNumber = lowestSEQnumber;
		}
		for (Event e : this.events) {
			e.getTimestamp();
		}

	}

	@Override
	public long getTimestamp() {
		return events.get(0).getTimestamp();
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
		this.sequenceNumber = Math.min(event.sequenceNumber, this.sequenceNumber);
		events.add(event);
	}
	
	public void addAggregatedEvent(AggregatedEvent aggregatedEvent) {
		events.addAll(aggregatedEvent.events);
	}
	
	public final List<Event> getPrimitiveEvents() {
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
		if (this == other) {
			return true;
		}
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
	public boolean isAggregatedEvent() {
		return true;
	}

	@Override
	public AggregatedEvent clone() {
		return new AggregatedEvent(type, events);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s(Aggregated, %d events),: ", type, events.size()));
		for (Event e : events ) {
			builder.append(e.toString()).append(" ");
		}

		return builder.toString();
	}

	public boolean isPrimitiveNotInAggregate(Event event) {
		return  !events.contains(event);
	}

	public boolean containsLaterEvent(Event event) {
		long latestSequenceNumber = 0;
		for (Event primitiveEvent : events) {
			latestSequenceNumber = Math.max(primitiveEvent.sequenceNumber, latestSequenceNumber);
		}
		return latestSequenceNumber > event.sequenceNumber;
	}
}
