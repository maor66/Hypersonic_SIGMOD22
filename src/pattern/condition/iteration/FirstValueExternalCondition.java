package sase.pattern.condition.iteration;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.pattern.condition.base.DoubleEventCondition;

public class FirstValueExternalCondition extends IteratedEventExternalCondition {

	private DoubleEventCondition nestedCondition;
	
	public FirstValueExternalCondition(DoubleEventCondition nestedCondition) {
		super(nestedCondition.getLeftEventType(), nestedCondition.getRightEventType());
		this.nestedCondition = nestedCondition;
	}

	@Override
	protected boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent) {
		if (internalEvents.isEmpty()) {
			return false;
		}
		List<Event> eventsToVerify = new ArrayList<Event>();
		eventsToVerify.add(internalEvents.get(0));
		eventsToVerify.add(externalEvent);
		return nestedCondition.verify(eventsToVerify);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
