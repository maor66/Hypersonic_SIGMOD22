package sase.specification.creators.condition;

import sase.specification.ConditionSpecification;
import sase.specification.DoubleEventConditionSpecification;

public interface IConditionSpecificationCreator {

	public DoubleEventConditionSpecification createDoubleEventCondition(String firstTypeName,
																		String secondTypeName);
	public ConditionSpecification createIteratedEventCondition(String precedingTypeName, 
															   String iteratedTypeName, 
															   String succeedingTypeName);
}
