package sase.user.stocks.specification;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.ConditionSpecification;
import sase.user.stocks.condition.StockDeltaOrderingCondition;

public class StockDeltaOrderingConditionSpecification extends ConditionSpecification {

	private final String firstEventName;
	private final String secondEventName;
	
	public StockDeltaOrderingConditionSpecification(String firstEventName, String secondEventName) {
		this.firstEventName = firstEventName;
		this.secondEventName = secondEventName;
	}

	public String getFirstEventName() {
		return firstEventName;
	}

	public String getSecondEventName() {
		return secondEventName;
	}
	
	@Override
	public List<AtomicCondition> createConditions() {
		EventType firstType = EventTypesManager.getInstance().getTypeByName(firstEventName);
		EventType secondType = EventTypesManager.getInstance().getTypeByName(secondEventName);
		StockDeltaOrderingCondition condition = new StockDeltaOrderingCondition(firstType, secondType);
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
