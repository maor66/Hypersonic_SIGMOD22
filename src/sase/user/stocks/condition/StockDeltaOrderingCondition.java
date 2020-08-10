package sase.user.stocks.condition;

import java.util.Objects;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.user.stocks.StockEventTypesManager;

public class StockDeltaOrderingCondition extends DoubleEventCondition {

	public StockDeltaOrderingCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}
	
	public StockDeltaOrderingCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}
	
	protected Double calculateDelta(Event event) {
		Double firstValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex);
		Double secondValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex + 1);
//		double val = 0;
//		for (long i=0; i < 2L; i++) {for (int j=0; j < 853; j++) {val *= i+j;}}
//		 Double val = Math.abs(Math.pow(firstValue, 102377065) - Math.pow(secondValue,100773465));
//		Double val = firstValue - secondValue;
//		if (val < 0.0D) {
//			val = 0.0D -val;
//		}
//		return val;
		return Math.abs(firstValue - secondValue);
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		return calculateDelta(firstEvent) < calculateDelta(secondEvent);
//		return calculateDelta(firstEvent) < calculateDelta(secondEvent) + 1000000; //TODO: CHANGE!!!111
	}

	public boolean verifySpecificCondition(Event firstEvent, Event secondEvent) {
		return verifyDoubleEvent(firstEvent, secondEvent);
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of relative deltas of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StockDeltaOrderingCondition)) {
			return false;
		}
		StockDeltaOrderingCondition condition = (StockDeltaOrderingCondition)other;
		return (firstType == condition.firstType && secondType == condition.secondType);
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(firstType, secondType);
    }

}
