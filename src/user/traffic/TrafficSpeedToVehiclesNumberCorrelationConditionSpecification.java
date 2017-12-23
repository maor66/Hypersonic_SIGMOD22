package user.traffic;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.EventTypesManager;
import pattern.condition.base.AtomicCondition;
import specification.ConditionSpecification;

public class TrafficSpeedToVehiclesNumberCorrelationConditionSpecification extends ConditionSpecification {

	private final Integer firstEventNameIndex;
	private final Integer secondEventNameIndex;
	
	public TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(Integer firstEventNameIndex,
																		 Integer secondEventNameIndex) {
		this.firstEventNameIndex = firstEventNameIndex;
		this.secondEventNameIndex = secondEventNameIndex;
	}

	public Integer getFirstEventNameIndex() {
		return firstEventNameIndex;
	}

	public Integer getSecondEventNameIndex() {
		return secondEventNameIndex;
	}
	
	@Override
	public List<AtomicCondition> createConditions() {
		String firstEventName = AarhusTrafficEventTypesManager.getEventTypeNames()[firstEventNameIndex];
		String secondEventName = AarhusTrafficEventTypesManager.getEventTypeNames()[secondEventNameIndex];
		EventType firstType = EventTypesManager.getInstance().getTypeByName(firstEventName);
		EventType secondType = EventTypesManager.getInstance().getTypeByName(secondEventName);
		TrafficSpeedToVehiclesNumberCorrelationCondition condition = 
					new TrafficSpeedToVehiclesNumberCorrelationCondition(firstType, secondType);
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		conditions.add(condition);
		return conditions;
	}
	
	@Override
	public String toString() {
		return String.format("Speed to vehicle number correlation between %dth and %dth vehicle number bounds", 
							 firstEventNameIndex, secondEventNameIndex);
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}
}
