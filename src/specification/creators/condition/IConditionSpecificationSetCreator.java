package sase.specification.creators.condition;

import java.util.List;

import sase.base.EventType;
import sase.specification.ConditionSpecification;

public interface IConditionSpecificationSetCreator {

	public ConditionSpecification[] createConditionSpecifications(List<EventType> eventTypes,
			 													  List<EventType> negatedEventTypes, 
			 													  List<EventType> iteratedEventTypes, 
			 													  String[][][] patternStructure,
			 													  IConditionSpecificationCreator conditionCreator);
}
