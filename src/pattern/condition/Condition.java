package sase.pattern.condition;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.statistics.ConditionSelectivityCollector;

/**
 * Represents a condition to be satisfied by a complex event.
 */
public abstract class Condition {
	
	protected List<EventType> eventTypes;
	private Double selectivity;
	
	public Condition() {
		this(new ArrayList<EventType>());
	}
	
	public Condition(List<EventType> types) {
		this(types, null);
	}
	
	public Condition(Double selectivity) {
		this(new ArrayList<EventType>(), selectivity);
	}
	
	public Condition(List<EventType> types, Double selectivity) {
		eventTypes = types;
		setSelectivity(selectivity);
	}
	
	public List<EventType> getEventTypes() {
		return eventTypes;
	}
	
	public Double getSelectivity() {
		return selectivity;
	}
	
	public void setSelectivity(Double selectivity) {
		this.selectivity = selectivity;
	}
	
	protected void setSelectivityByEstimate() {
		setSelectivity(ConditionSelectivityCollector.getInstance().getSelectivityEstimate(getConditionKey()));
	}
	
	@Override
	public String toString() {
		return String.format("Condition on types %s", eventTypes);
	}
	
	public abstract boolean verify(List<Event> events);
	protected abstract String getConditionKey();
}

