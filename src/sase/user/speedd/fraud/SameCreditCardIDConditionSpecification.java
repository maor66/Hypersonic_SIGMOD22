package sase.user.speedd.fraud;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.ConditionSpecification;

public class SameCreditCardIDConditionSpecification extends ConditionSpecification {

	private final String firstEventName;
	private final String secondEventName;

	public SameCreditCardIDConditionSpecification(String firstEventName, String secondEventName) {
		this.firstEventName = firstEventName;
		this.secondEventName = secondEventName;
	}

	@Override
	public List<AtomicCondition> createConditions() {
		EventType firstType = EventTypesManager.getInstance().getTypeByName(firstEventName);
		EventType secondType = EventTypesManager.getInstance().getTypeByName(secondEventName);
		SameCreditCardIDCondition condition = new SameCreditCardIDCondition(firstType, secondType);
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		conditions.add(condition);
		return conditions;
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}

}
