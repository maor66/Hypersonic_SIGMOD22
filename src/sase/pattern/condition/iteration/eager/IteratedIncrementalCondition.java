package sase.pattern.condition.iteration.eager;

import java.util.List;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.iteration.IteratedEventInternalCondition;

public abstract class IteratedIncrementalCondition extends IteratedEventInternalCondition {

	public IteratedIncrementalCondition(EventType type) {
		super(type);
	}

	@Override
	protected boolean verifyAggregatedEvent(AggregatedEvent event) {
		List<Event> primitiveEvents = event.getPrimitiveEvents();
		if (primitiveEvents.size() < 2) {
			return true;
		}
		Event lastEvent = primitiveEvents.get(primitiveEvents.size() - 1);
		Event eventBeforeLast = primitiveEvents.get(primitiveEvents.size() - 2);
		return verifyAdjacentEvents(lastEvent, eventBeforeLast);
	}

	public abstract boolean verifyAdjacentEvents(Event firstEvent, Event secondEvent);
}
