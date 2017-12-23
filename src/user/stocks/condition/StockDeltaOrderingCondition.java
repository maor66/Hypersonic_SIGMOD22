package user.stocks.condition;

import base.Event;
import base.EventType;
import pattern.condition.base.DoubleEventCondition;
import user.stocks.StockEventTypesManager;

public class StockDeltaOrderingCondition extends DoubleEventCondition {

	public StockDeltaOrderingCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}
	
	private Double calculateDelta(Event event) {
		Double firstValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex);
		Double secondValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex + 1);
		return firstValue - secondValue;
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		return calculateDelta(firstEvent) < calculateDelta(secondEvent);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of deltas of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}

}
