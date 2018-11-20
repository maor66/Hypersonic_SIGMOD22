package sase.user.synthetic;
import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.ConditionSpecification;

public class SyntheticConditionSpecification extends ConditionSpecification {

	private final String firstEventName;
	private final String secondEventName;
	
	public SyntheticConditionSpecification(String firstEventName, String secondEventName) {
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
		SyntheticCondition condition = new SyntheticCondition(firstType, secondType);
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		conditions.add(condition);
		return conditions;
	}
	
	@Override
	public String toString() {
		return String.format("Synthetic condition %s:%s", firstEventName, secondEventName);
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}
}
