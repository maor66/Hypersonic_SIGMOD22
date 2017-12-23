package user.stocks.specification;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.EventTypesManager;
import pattern.condition.base.AtomicCondition;
import specification.ConditionSpecification;
import user.stocks.condition.StockFirstValueCmpCondition;
import user.stocks.condition.StockFirstValueCmpCondition.ComparisonOperation;

public class StockFirstValueCmpConditionSpecification extends ConditionSpecification {

	private final String firstEventName;
	private final String secondEventName;
	private final ComparisonOperation operation;
	
	public StockFirstValueCmpConditionSpecification(String firstEventName, String secondEventName,
													ComparisonOperation operation) {
		this.firstEventName = firstEventName;
		this.secondEventName = secondEventName;
		this.operation = operation;
	}

	public String getFirstEventName() {
		return firstEventName;
	}

	public String getSecondEventName() {
		return secondEventName;
	}
	
	public ComparisonOperation getOperation() {
		return operation;
	}
	
	@Override
	public List<AtomicCondition> createConditions() {
		EventType firstType = EventTypesManager.getInstance().getTypeByName(firstEventName);
		EventType secondType = EventTypesManager.getInstance().getTypeByName(secondEventName);
		StockFirstValueCmpCondition condition = new StockFirstValueCmpCondition(firstType, secondType, operation);
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		conditions.add(condition);
		return conditions;
	}
	
	@Override
	public String toString() {
		return String.format("Value equality between %s and %s", firstEventName, secondEventName);
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}
}
