package sase.user.stocks.specification;


import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.DoubleEventConditionSpecification;
import sase.user.stocks.condition.StockDeltaOrderingCondition;

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
