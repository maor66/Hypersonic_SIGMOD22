package sase.pattern.condition.iteration.eager;

import sase.base.Event;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.user.stocks.StockEventTypesManager;

public class IteratedIncrementalDoubleEventCondition extends IteratedIncrementalCondition {
	
	private DoubleEventCondition nestedCondition;

	public IteratedIncrementalDoubleEventCondition(DoubleEventCondition nestedCondition) {
		super(nestedCondition.getLeftEventType());
		this.nestedCondition = nestedCondition;
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
	public boolean verifyAdjacentEvents(Event firstEvent, Event secondEvent)
	{
		Boolean b = calculateDelta(firstEvent) < calculateDelta(secondEvent);
//		System.out.println("Comparing " + firstEvent.toString() + " with " + secondEvent.toString() + b);
		return nestedCondition.verifyDoubleEvent(firstEvent, secondEvent);
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
