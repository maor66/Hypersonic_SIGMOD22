package specification.creators.condition;

import java.util.List;

import base.EventType;
import specification.ConditionSpecification;

public interface IConditionSpecificationSetCreator {

	public ConditionSpecification[] createConditionSpecifications(List<EventType> eventTypes,
			 													  List<EventType> negatedEventTypes, 
			 													  List<EventType> iteratedEventTypes, 
			 													  String[][][] patternStructure,
			 													  IConditionSpecificationCreator conditionCreator);
}
