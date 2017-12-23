package sase.user.speedd.traffic;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

public class TrafficSimilarVehicleIDCondition extends DoubleEventCondition {
	
	private static final long maxDiff = 0;

	public TrafficSimilarVehicleIDCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}

	public TrafficSimilarVehicleIDCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		Long firstID = (Long) firstEvent.getAttributeValue(TrafficEventTypesManager.vehicleIDAttributeIndex);
		Long secondID = (Long) secondEvent.getAttributeValue(TrafficEventTypesManager.vehicleIDAttributeIndex);
		return Math.abs(firstID - secondID) <= maxDiff;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
