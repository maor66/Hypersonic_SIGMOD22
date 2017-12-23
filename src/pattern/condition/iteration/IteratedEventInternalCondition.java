package sase.pattern.condition.iteration;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.SingleEventCondition;

public abstract class IteratedEventInternalCondition extends SingleEventCondition {

	public IteratedEventInternalCondition(EventType type) {
		super(type);
	}

	@Override
	protected boolean verifySingleEvent(Event event) {
		if (!(event instanceof AggregatedEvent)) {
			throw new RuntimeException(
				String.format("Aggregated event of type %s expected, primitive event %s received instead", type, event));
		}
		AggregatedEvent aggregatedEvent = (AggregatedEvent)event;
		return verifyAggregatedEvent(aggregatedEvent);
	}
	
	protected abstract boolean verifyAggregatedEvent(AggregatedEvent event);

}
