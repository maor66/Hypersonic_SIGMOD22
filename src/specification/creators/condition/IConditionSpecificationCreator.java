package sase.specification.creators.condition;

import sase.specification.condition.ConditionSpecification;
import sase.specification.condition.DoubleEventConditionSpecification;

public interface IConditionSpecificationCreator {

	public DoubleEventConditionSpecification createDoubleEventCondition(String firstTypeName,
																		String secondTypeName);
	public ConditionSpecification createIteratedEventCondition(String precedingTypeName, 
															   String iteratedTypeName, 
															   String succeedingTypeName);
}
