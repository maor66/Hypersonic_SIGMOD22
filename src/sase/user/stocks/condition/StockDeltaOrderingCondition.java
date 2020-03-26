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
	
	private Double calculateDelta(Event event) {
		Double firstValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex);
		Double secondValue = (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex + 1);
//		int val = 0;
//		for (int i=0; i < 10; i++) {for (int j=0; j < 83; j++) {val += i+j;}}
//		int Double val = Math.abs(Math.pow(firstValue, 102377065) - Math.pow(secondValue,100773465));
//		Double val = firstValue - secondValue;
//		if (val < 0.0D) {
//			val = 0.0D -val;
//		}
//		return val;
		return Math.abs(firstValue - secondValue);
	}

	@Override
	public boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		Boolean b =  calculateDelta(firstEvent) < calculateDelta(secondEvent);
//		System.out.println("Comparing " + firstEvent.toString() + " with " + secondEvent.toString() + " result is "+ b);
		return b;
//		return calculateDelta(firstEvent) < calculateDelta(secondEvent) + 1000000; //TODO: CHANGE!!!111
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
