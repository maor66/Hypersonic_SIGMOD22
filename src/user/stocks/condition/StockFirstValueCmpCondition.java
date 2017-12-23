package user.stocks.condition;

import base.Event;
import base.EventType;
import pattern.condition.base.DoubleEventCondition;
import user.stocks.StockEventTypesManager;

/**
 * Represents a condition on a correlation rate between appearances of two given primitive events.
 */
public class StockFirstValueCmpCondition extends DoubleEventCondition {
	
	public enum ComparisonOperation {
		SMALLER,
		EQUALS,
		BIGGER
	};
	
	private ComparisonOperation operation;
	
	public StockFirstValueCmpCondition(EventType firstType, EventType secondType, ComparisonOperation operation) {
		super(firstType, secondType);
		this.operation = operation;
	}

	private double getStockFirstValue(Event event) {
		return (Double)event.getAttributeValue(StockEventTypesManager.firstStockMeasurementIndex);
	}
	
	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		int firstEventValue = ((Double)getStockFirstValue(firstEvent)).intValue();
		int secondEventValue = ((Double)getStockFirstValue(secondEvent)).intValue();
		switch (operation) {
		case BIGGER:
			return (firstEventValue > secondEventValue);
		case EQUALS:
			return (firstEventValue == secondEventValue);
		case SMALLER:
			return (firstEventValue < secondEventValue);
		default:
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of first price values of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
