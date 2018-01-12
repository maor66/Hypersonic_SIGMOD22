package user.stocks.specification;

import base.EventType;
import pattern.condition.base.AtomicCondition;
import specification.DoubleEventConditionSpecification;
import user.stocks.condition.StockFirstValueCmpCondition;
import user.stocks.condition.StockFirstValueCmpCondition.ComparisonOperation;

public class StockFirstValueCmpConditionSpecification extends DoubleEventConditionSpecification {

	private final ComparisonOperation operation;
	
	public StockFirstValueCmpConditionSpecification(String firstEventName, String secondEventName,
													ComparisonOperation operation, Double selectivity) {
		super(firstEventName, secondEventName, selectivity);
		this.operation = operation;
	}
	
	public StockFirstValueCmpConditionSpecification(String firstEventName, String secondEventName,
													ComparisonOperation operation) {
		this(firstEventName, secondEventName, operation, null);
	}

	public ComparisonOperation getOperation() {
		return operation;
	}
	
	@Override
	public String toString() {
		return String.format("Value equality between %s and %s", firstEventName, secondEventName);
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}

	@Override
	protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
														 Double conditionSelectivity) {
		return new StockFirstValueCmpCondition(firstType, secondType, operation);
	}
}
