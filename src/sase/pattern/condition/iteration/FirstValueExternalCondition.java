package sase.pattern.condition.iteration;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.pattern.condition.base.DoubleEventCondition;

public class FirstValueExternalCondition extends IteratedEventExternalCondition {

	private DoubleEventCondition nestedCondition;
	private boolean isLast;
	
	public FirstValueExternalCondition(DoubleEventCondition nestedCondition, boolean isLast) {
		super(nestedCondition.getLeftEventType(), nestedCondition.getRightEventType());
		this.nestedCondition = nestedCondition;
		this.isLast = isLast;
	}

	@Override
	protected boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent) {
		if (internalEvents.isEmpty()) {
			return false;
		}
		List<Event> eventsToVerify = new ArrayList<Event>();
		int index = isLast ? internalEvents.size() - 1 : 0;
		eventsToVerify.add(internalEvents.get(index));
		eventsToVerify.add(externalEvent);
		return nestedCondition.verify(eventsToVerify);
	}

}
