package sase.pattern.condition.iteration.eager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.pattern.condition.iteration.IteratedEventExternalCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class IterationTriggerCondition extends IteratedEventExternalCondition {

	private DoubleEventCondition triggerCondition;
	private HashMap<Event, Boolean> triggerConditionVerificationCache = null;
	
	public IterationTriggerCondition(DoubleEventCondition triggerCondition) {
		super(triggerCondition.getLeftEventType(), triggerCondition.getRightEventType());
		this.triggerCondition = triggerCondition;
	}

	@Override
	protected boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent) {
		//we decrease this statistic by one since this condition invokes an internal condition, which increases
		//this counter again
		Environment.getEnvironment().getStatisticsManager().decrementDiscreteStatistic(Statistics.computations);
		if (internalEvents.isEmpty()) {
			throw new RuntimeException("Empty list of events encountered");
		}
		Event leadingEvent = internalEvents.get(0);
		if (isCacheEnabled() && triggerConditionVerificationCache.containsKey(leadingEvent)) {
			return triggerConditionVerificationCache.get(leadingEvent);
		}
		List<Event> eventsToVerifyWithTriggerCondition = new ArrayList<Event>();
		//NOTE: external event is always added first, since we assume the underlying condition to accept the
		//non-iterated event first (intuitively, it goes 'before' the iterated one)
		eventsToVerifyWithTriggerCondition.add(externalEvent);
		eventsToVerifyWithTriggerCondition.add(leadingEvent);
		boolean result = triggerCondition.verify(eventsToVerifyWithTriggerCondition);
		if (isCacheEnabled()) {
			triggerConditionVerificationCache.put(leadingEvent, result);
		}
		return result;
	}
	
	public void enableCache() {
		triggerConditionVerificationCache = new HashMap<Event, Boolean>();
	}

	public void disableCache() {
		triggerConditionVerificationCache = null;
	}
	
	public boolean isCacheEnabled() {
		return (triggerConditionVerificationCache != null);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
