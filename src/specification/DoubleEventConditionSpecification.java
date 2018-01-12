package specification;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.EventTypesManager;
import pattern.condition.base.AtomicCondition;

public abstract class DoubleEventConditionSpecification extends ConditionSpecification {

	protected final String firstEventName;
	protected final String secondEventName;
	protected final Double selectivity;
	
	public DoubleEventConditionSpecification(String firstEventName,
											 String secondEventName,
											 Double selectivity) {
		this.firstEventName = firstEventName;
		this.secondEventName = secondEventName;
		this.selectivity = selectivity;
	}

	public String getFirstEventName() {
		return firstEventName;
	}

	public String getSecondEventName() {
		return secondEventName;
	}

	public double getSelectivity() {
		return selectivity;
	}

	@Override
	public List<AtomicCondition> createConditions() {
		EventType firstType = EventTypesManager.getInstance().getTypeByName(firstEventName);
		EventType secondType = EventTypesManager.getInstance().getTypeByName(secondEventName);
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		conditions.add(createDoubleEventCondition(firstType, secondType, selectivity));
		return conditions;
	}

	@Override
	public String getShortDescription() {
		String firstShortName = EventTypesManager.getInstance().getShortNameByLongName(firstEventName);
		String secondShortName = EventTypesManager.getInstance().getShortNameByLongName(secondEventName);
		return String.format("%s:%s", firstShortName, secondShortName);
	}

	protected abstract AtomicCondition createDoubleEventCondition(EventType firstType,
																  EventType secondType,
																  Double conditionSelectivity);

}
