package sase.pattern.condition.time;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class EventTemporalPositionCondition extends AtomicCondition {

	private final EventType targetEventType;
	private final List<EventType> precedingEventTypes;
	private final List<EventType> succeedingEventTypes;
	
	public EventTemporalPositionCondition(EventType targetEventType,
										  List<EventType> precedingEventTypes,
										  List<EventType> succeedingEventTypes) {
		this.targetEventType = targetEventType;
		this.precedingEventTypes = precedingEventTypes;
		this.succeedingEventTypes = succeedingEventTypes;
	}
	
	public EventType getTargetEventType() {
		return targetEventType;
	}

	public List<EventType> getPrecedingEventTypes() {
		return precedingEventTypes;
	}

	public List<EventType> getSucceedingEventTypes() {
		return succeedingEventTypes;
	}

	private Event getEventByType(List<Event> events, EventType eventType) {
		for (Event event : events) {
			if (event.getType() == eventType)
				return event;
		}
		return null;
	}
	
	//TODO: horrible code duplication with PairTemporalOrderCondition! Refactor!
	private boolean isEarlier(Event firstEvent, Event secondEvent) {
		return firstEvent.getSequenceNumber() < secondEvent.getSequenceNumber();
	}
	
	@Override
	protected boolean actuallyVerify(List<Event> events) {
		Event targetEvent = getEventByType(events, targetEventType);
		if (targetEvent == null)
			throw new RuntimeException("Cannot verify condition - the target event was not found.");
		for (EventType eventType : precedingEventTypes) {
//			Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.timeComparisons);
//			Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.parallelTimeComparisons);
			Event precedingEvent = getEventByType(events, eventType);
			if (isEarlier(targetEvent, precedingEvent)) {
				return false;
			}
		}
		for (EventType eventType : succeedingEventTypes) {
//			Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.timeComparisons);
//			Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.parallelTimeComparisons);
			Event succeedingEvent = getEventByType(events, eventType);
			if (isEarlier(succeedingEvent, targetEvent)) {
				return false;
			}
		}
		return true;
	}
	
	public Event getActualPrecedingEvent(List<Event> events) {
		Event precedingEvent = null;
		for (int i = 0; i < precedingEventTypes.size(); ++i) {
			EventType candidateType = precedingEventTypes.get(i);
			Event candidateEvent = getEventByType(events, candidateType);
			if (precedingEvent == null) {
				precedingEvent = candidateEvent;
			}
			else if (candidateEvent != null && isEarlier(precedingEvent, candidateEvent)) {
				precedingEvent = candidateEvent;
			}
		}
		return precedingEvent;
	}
	
	public Event getActualSucceedingEvent(List<Event> events) {
		Event succeedingEvent = null;
		for (int i = 0; i < succeedingEventTypes.size(); ++i) {
			EventType candidateType = succeedingEventTypes.get(i);
			Event candidateEvent = getEventByType(events, candidateType);
			if (succeedingEvent == null) {
				succeedingEvent = candidateEvent;
			}
			else if (candidateEvent != null && isEarlier(candidateEvent, succeedingEvent)) {
				succeedingEvent = candidateEvent;
			}
		}
		return succeedingEvent;
	}
	
	@Override
	public String toString() {
		return String.format("Temporal Condition: (%s) => %s => (%s)", 
							 precedingEventTypes, targetEventType, succeedingEventTypes);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public Double getSelectivity() {
		return 1.0; //for now, we ignore the selectivity of all temporal constraints
	}
	
	@Override
	protected boolean shouldIgnoreSelectivityMeasurements() {
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EventTemporalPositionCondition)) {
			return false;
		}
		EventTemporalPositionCondition condition = (EventTemporalPositionCondition)other;
		if (targetEventType != condition.targetEventType) {
			return false;
		}
		if (!(new HashSet<EventType>(precedingEventTypes).equals(new HashSet<EventType>(condition.precedingEventTypes)))) {
			return false;
		}
		return (new HashSet<EventType>(succeedingEventTypes).equals(new HashSet<EventType>(condition.succeedingEventTypes)));
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(targetEventType, precedingEventTypes, succeedingEventTypes);
    }

}
