package specification.creators.condition;

import specification.ConditionSpecification;
import specification.DoubleEventConditionSpecification;

public interface IConditionSpecificationCreator {

	public DoubleEventConditionSpecification createDoubleEventCondition(String firstTypeName,
																		String secondTypeName);
	public ConditionSpecification createIteratedEventCondition(String precedingTypeName, 
															   String iteratedTypeName, 
															   String succeedingTypeName);
}
