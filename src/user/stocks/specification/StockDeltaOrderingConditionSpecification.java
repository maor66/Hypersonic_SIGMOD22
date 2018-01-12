package user.stocks.specification;


import base.EventType;
import pattern.condition.base.AtomicCondition;
import specification.DoubleEventConditionSpecification;
import user.stocks.condition.StockDeltaOrderingCondition;

public class StockDeltaOrderingConditionSpecification extends DoubleEventConditionSpecification {

	public StockDeltaOrderingConditionSpecification(String firstEventName, String secondEventName, Double selectivity) {
		super(firstEventName, secondEventName, selectivity);
	}
	
	public StockDeltaOrderingConditionSpecification(String firstEventName, String secondEventName) {
		super(firstEventName, secondEventName, null);
	}
	
	@Override
	public String toString() {
		return String.format("Delta comparison between %s and %s", firstEventName, secondEventName);
	}

	@Override
	protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
														 Double conditionSelectivity) {
		return new StockDeltaOrderingCondition(firstType, secondType, selectivity);
	}
}
