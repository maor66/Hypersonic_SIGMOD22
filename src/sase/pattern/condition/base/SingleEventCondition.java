package sase.pattern.condition.base;

import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.simulator.Environment;
import sase.statistics.Statistics;

/**
 * Represents a condition which only involves attributes of a single primitive event.
 */
public abstract class SingleEventCondition extends AtomicCondition {
	
	protected final EventType type;
	
	public SingleEventCondition(EventType type) {
		super();
		this.type = type;
		eventTypes.add(type);
	}
	
	@Override
	protected boolean actuallyVerify(List<Event> events) {
		for (Event event : events) {
			if (event.getType() == type) {
				Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.computations);
				return verifySingleEvent(event);
			}
		}
		return false;
	}
	
	public EventType getType() {
		return type;
	}

	protected abstract boolean verifySingleEvent(Event event);
}