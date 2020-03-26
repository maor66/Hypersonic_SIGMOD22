package sase.user.traffic;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

public class TrafficSpeedToVehiclesNumberCorrelationCondition extends DoubleEventCondition {

	private static final int minSpeedDiff = 30;
	
	public TrafficSpeedToVehiclesNumberCorrelationCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
    public boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		Integer firstPointID = (Integer) firstEvent.getAttributeValue(AarhusTrafficEventTypesManager.pointIDAttributeName);
		Integer secondPointID = (Integer) secondEvent.getAttributeValue(AarhusTrafficEventTypesManager.pointIDAttributeName);
		if (firstPointID == secondPointID) {
			return false;
		}
		Integer firstSpeed = 
				(Integer) firstEvent.getAttributeValue(AarhusTrafficEventTypesManager.averageSpeedAttributeName);
		Integer secondSpeed = 
				(Integer) secondEvent.getAttributeValue(AarhusTrafficEventTypesManager.averageSpeedAttributeName);
		Integer firstNumberOfVehicles = 
				(Integer) firstEvent.getAttributeValue(AarhusTrafficEventTypesManager.numberOfVehiclesAttributeName);
		Integer secondNumberOfVehicles = 
				(Integer) secondEvent.getAttributeValue(AarhusTrafficEventTypesManager.numberOfVehiclesAttributeName);
		boolean requestSpeedIncrease = 
				(firstNumberOfVehicles == secondNumberOfVehicles) ? (firstPointID < secondPointID) :
																	(firstNumberOfVehicles > secondNumberOfVehicles);
		return requestSpeedIncrease ? (firstSpeed + minSpeedDiff < secondSpeed) : (secondSpeed + minSpeedDiff < firstSpeed);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
