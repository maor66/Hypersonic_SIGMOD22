package sase.pattern.condition.iteration;

import java.util.List;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

public abstract class IteratedEventExternalCondition extends DoubleEventCondition {
	
	public IteratedEventExternalCondition(EventType iteratedType, EventType nonIteratedType) {
		super(iteratedType, nonIteratedType);
	}

	@Override
    public boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		if (firstEvent instanceof AggregatedEvent) {
			return verifyListOfEventsWithExternalEvent(((AggregatedEvent)firstEvent).getPrimitiveEvents(), 
													   secondEvent);
		}
		if (secondEvent instanceof AggregatedEvent) {
			return verifyListOfEventsWithExternalEvent(((AggregatedEvent)secondEvent).getPrimitiveEvents(), 
													   firstEvent);
		}
		throw new RuntimeException(
						String.format("Aggregated event expected, primitive events %s and %s received instead", 
						firstEvent, secondEvent));
	}

	protected abstract boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent);
}
