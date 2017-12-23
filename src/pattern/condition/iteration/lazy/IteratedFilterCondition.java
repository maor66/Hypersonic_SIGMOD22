package sase.pattern.condition.iteration.lazy;

import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.iteration.IteratedEventExternalCondition;

public abstract class IteratedFilterCondition extends IteratedEventExternalCondition {

	public IteratedFilterCondition(EventType iteratedType, EventType nonIteratedType) {
		super(iteratedType, nonIteratedType);
	}

	@Override
	protected boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent) {
		for (Event event : internalEvents) {
			if (!filterEvent(event, externalEvent)) {
				return false;
			}
		}
		return true;
	}
	
	public abstract boolean filterEvent(Event iteratedEvent, Event nonIteratedEvent);

}
