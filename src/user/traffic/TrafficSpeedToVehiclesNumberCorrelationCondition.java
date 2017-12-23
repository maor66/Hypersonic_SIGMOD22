package user.traffic;

import base.Event;
import base.EventType;
import pattern.condition.base.DoubleEventCondition;

public class TrafficSpeedToVehiclesNumberCorrelationCondition extends DoubleEventCondition {

	private static final int minSpeedDiff = 30;
	
	public TrafficSpeedToVehiclesNumberCorrelationCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
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
