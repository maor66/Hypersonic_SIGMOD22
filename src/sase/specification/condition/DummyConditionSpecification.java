package sase.specification.condition;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.DummyDoubleEventCondition;

public class DummyConditionSpecification extends DoubleEventConditionSpecification {
	
	private double trueProbability;
	private int timeTillDone;

	public DummyConditionSpecification(String firstEventName, String secondEventName, Double selectivity,
			double trueProbability, int timeTillDone) {
		super(firstEventName, secondEventName, selectivity);
		this.trueProbability = trueProbability;
		this.timeTillDone = timeTillDone;
	}
	
	@Override
	public String getShortDescription() {
		String firstShortName = EventTypesManager.getInstance().getShortNameByLongName(firstEventName);
		String secondShortName = EventTypesManager.getInstance().getShortNameByLongName(secondEventName);
		return String.format("%s:%s:%f:%d", firstShortName, secondShortName, trueProbability, timeTillDone);
	}

	@Override
	protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
			Double conditionSelectivity) {
		return new DummyDoubleEventCondition(firstType, secondType, trueProbability, timeTillDone);
	}

}
