package specification;

import java.util.List;

import pattern.condition.base.AtomicCondition;

public abstract class ConditionSpecification {
	
	public abstract List<AtomicCondition> createConditions();
	public abstract String getShortDescription();
}
