package user.stocks.condition;

import base.Event;
import base.EventType;
import pattern.condition.base.DoubleEventCondition;
import user.stocks.StockEventTypesManager;

public class StockDeltaOrderingCondition extends DoubleEventCondition {

	public StockDeltaOrderingCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}
	
	public StockDeltaOrderingCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}
	
	private Double calculateDelta(Event event) {
		Double firstValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex);
		Double secondValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex + 1);
		return Math.abs(firstValue - secondValue);
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		return calculateDelta(firstEvent) < calculateDelta(secondEvent);
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of relative deltas of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}

}
