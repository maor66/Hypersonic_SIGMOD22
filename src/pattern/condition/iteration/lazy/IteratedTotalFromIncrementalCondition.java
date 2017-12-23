package pattern.condition.iteration.lazy;

import java.util.List;

import base.AggregatedEvent;
import base.Event;
import base.EventType;
import pattern.condition.iteration.IteratedEventInternalCondition;
import pattern.condition.iteration.eager.IteratedIncrementalCondition;
import simulator.Environment;
import statistics.Statistics;

public class IteratedTotalFromIncrementalCondition extends IteratedEventInternalCondition {

	private final IteratedIncrementalCondition incrementalCondition;
	
	public IteratedTotalFromIncrementalCondition(IteratedIncrementalCondition incrementalCondition) {
		super(incrementalCondition.getType());
		this.incrementalCondition = incrementalCondition;
		EventType incrementalConditionType = incrementalCondition.getType();
		if (this.type != incrementalConditionType) {
			throw new RuntimeException(String.format("Unexpected type %s accepted, %s expected",
													 incrementalConditionType, type));
		}
	}

	@Override
	protected boolean verifyAggregatedEvent(AggregatedEvent event) {
		//we decrease this statistic by one since this condition invokes an internal condition, which increases
		//this counter again
		Environment.getEnvironment().getStatisticsManager().decrementDiscreteStatistic(Statistics.computations);
		List<Event> primitiveEvents = event.getPrimitiveEvents();
		if (primitiveEvents.size() < 2) {
			return true;
		}
		Event prevEvent = null;
		for (Event primitiveEvent : primitiveEvents) {
			if (prevEvent != null) {
				if (!(incrementalCondition.verifyAdjacentEvents(prevEvent, primitiveEvent))) {
					return false;
				}
			}
			prevEvent = primitiveEvent;
		}
		return true;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
