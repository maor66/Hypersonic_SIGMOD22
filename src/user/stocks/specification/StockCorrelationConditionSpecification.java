package user.stocks.specification;

import base.EventType;
import pattern.EventTypesManager;
import pattern.condition.base.AtomicCondition;
import specification.DoubleEventConditionSpecification;
import user.stocks.condition.StockCorrelationCondition;

public class StockCorrelationConditionSpecification extends DoubleEventConditionSpecification {

	private final double correlationLimit;
	
	public StockCorrelationConditionSpecification(String firstEventName,
												  String secondEventName,
												  double correlationLimit,
												  Double selectivity) {
		super(firstEventName, secondEventName, selectivity);
		this.correlationLimit = correlationLimit;
	}
	
	public StockCorrelationConditionSpecification(String firstEventName,
			  									  String secondEventName,
			  									  double correlationLimit) {
		this(firstEventName, secondEventName, correlationLimit, null);
	}

	public double getCorrelationLimit() {
		return correlationLimit;
	}
	
	@Override
	public String toString() {
		String baseString = String.format("Correlation of %f between %s and %s ", 
			 	 						  correlationLimit, firstEventName, secondEventName);
		if (selectivity != null) {
			return baseString + String.format("(selectivity:%f)", selectivity);
		}
		return baseString + "(selectivity unknown)";
	}

	@Override
	public String getShortDescription() {
		String firstShortName = EventTypesManager.getInstance().getShortNameByLongName(firstEventName);
		String secondShortName = EventTypesManager.getInstance().getShortNameByLongName(secondEventName);
		return String.format("%s:%s:%.1f", firstShortName, secondShortName, correlationLimit);
	}

	@Override
	protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
														 Double conditionSelectivity) {
		return new StockCorrelationCondition(firstType, secondType, correlationLimit, selectivity);
	}
}
