package sase.specification;

import java.util.List;

import sase.pattern.condition.base.AtomicCondition;

public abstract class ConditionSpecification {
	
	public abstract List<AtomicCondition> createConditions();
	public abstract String getShortDescription();
}
