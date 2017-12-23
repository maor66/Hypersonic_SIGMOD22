package pattern.condition.iteration;

import java.util.ArrayList;
import java.util.List;

import aggregator.VectorAggregator;
import base.AggregatedEvent;
import base.Event;
import base.EventType;
import pattern.condition.Condition;

public class AggregatedExternalCondition extends IteratedEventExternalCondition {

	private final VectorAggregator aggregator;
	private final Condition condition;
	
	public AggregatedExternalCondition(EventType iteratedType, EventType nonIteratedType,
									   VectorAggregator aggregator, Condition condition) {
		super(iteratedType, nonIteratedType);
		this.aggregator = aggregator;
		this.condition = condition;
	}

	@Override
	protected boolean verifyListOfEventsWithExternalEvent(List<Event> internalEvents, Event externalEvent) {
		if (internalEvents.size() == 0) {
			throw new RuntimeException("Empty list of events encountered");
		}
		
		AggregatedEvent aggregatedEvent = new AggregatedEvent(internalEvents);
		aggregatedEvent.aggregatePrimitiveEvents(aggregator);
		
		List<Event> eventsToVerify = new ArrayList<Event>();
		eventsToVerify.add(aggregatedEvent);
		eventsToVerify.add(externalEvent);
		return condition.verify(eventsToVerify);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
