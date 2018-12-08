package sase.pattern.condition.contiguity;

import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;

public class PartialContiguityCondition extends AtomicCondition {

	private static final double conditionSelectivity = 1.0;
	
	private final List<EventType> order;
	
	public PartialContiguityCondition(List<EventType> order) {
		super(conditionSelectivity);
		this.order = order;
	}

	@Override
	protected boolean actuallyVerify(List<Event> events) {
		if (events.size() != 2) {
			throw new RuntimeException(String.format("Unexpected number of events: %d", events.size()));
		}
		Event firstEvent = events.get(0);
		Event secondEvent = events.get(1);
		int firstIndex = order.indexOf(firstEvent.getType());
		int secondIndex = order.indexOf(secondEvent.getType());
		if (firstIndex == -1 || secondIndex == -1) {
			throw new RuntimeException(String.format("Events not found: %s %s", firstEvent, secondEvent));
		}
		return (firstIndex - secondIndex == firstEvent.getSequenceNumber() - secondEvent.getSequenceNumber());
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public String toString() {
		return String.format("Partial contiguity according to order %s", order);
	}

}
