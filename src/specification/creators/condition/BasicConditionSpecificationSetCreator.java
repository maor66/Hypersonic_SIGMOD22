package specification.creators.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import base.EventType;
import config.SimulationConfig;
import specification.ConditionSpecification;
import specification.DoubleEventConditionSpecification;

public abstract class BasicConditionSpecificationSetCreator implements IConditionSpecificationSetCreator {

	public ConditionSpecification[] createConditionSpecifications(List<EventType> eventTypes,
																  List<EventType> negatedEventTypes, 
																  List<EventType> iteratedEventTypes, 
																  String[][][] patternStructure,
																  IConditionSpecificationCreator conditionCreator) {
		if (isDisjunction(patternStructure)) {
			return createDisjunctionConditionSpecifications(eventTypes, patternStructure, conditionCreator);
		}
		List<EventType> positiveNonIteratedEventTypes = 
									getPositiveNonIteratedEventTypes(eventTypes, negatedEventTypes, iteratedEventTypes);
		List<DoubleEventConditionSpecification> positiveNonIteratedConditionSpecifications = 
										createPositiveNonIteratedConditionSpecifications(positiveNonIteratedEventTypes,
																						 conditionCreator);
		List<DoubleEventConditionSpecification> negativeConditionSpecifications = 
													createNegativeConditionSpecifications(positiveNonIteratedEventTypes,
																						  negatedEventTypes,
																						  conditionCreator);
		List<ConditionSpecification> iterativeConditionSpecifications = 
																createIteratedConditionSpecifications(eventTypes,
																									  iteratedEventTypes,
																									  conditionCreator);
		List<ConditionSpecification> conditionSpecifications = 
							new ArrayList<ConditionSpecification>(positiveNonIteratedConditionSpecifications);
		conditionSpecifications.addAll(negativeConditionSpecifications);
		conditionSpecifications.addAll(iterativeConditionSpecifications);
		return conditionSpecifications.toArray(new ConditionSpecification[0]);
	}
	
	private ConditionSpecification[] createDisjunctionConditionSpecifications(List<EventType> eventTypes,
			  String[][][] patternStructure, IConditionSpecificationCreator conditionCreator) {
		//Note: for now we assume disjunctions to contain no negated or iterated events
		List<DoubleEventConditionSpecification> conditionSpecifications = new ArrayList<DoubleEventConditionSpecification>();
		for (String[][] disjunctiveClause : patternStructure) {
			List<EventType> currentEventTypes = new ArrayList<EventType>();
			for (String[] conjunctiveClause : disjunctiveClause) {
				for (String eventName : conjunctiveClause) {
					currentEventTypes.add(getTypeByName(eventName, eventTypes));
				}
			}
			conditionSpecifications.addAll(createPositiveNonIteratedConditionSpecifications(currentEventTypes,
																							conditionCreator));
		}
		return conditionSpecifications.toArray(new ConditionSpecification[0]);
	}

	private EventType getTypeByName(String eventName, List<EventType> eventTypes) {
		for (EventType eventType : eventTypes) {
			if (eventType.getName().equals(eventName)) {
				return eventType;
			}
		}
		return null;
	}

	private boolean isDisjunction(String[][][] patternStructure) {
		return patternStructure.length > 1;
	}

	private List<ConditionSpecification> createIteratedConditionSpecifications(List<EventType> eventTypes,
								List<EventType> iteratedEventTypes, IConditionSpecificationCreator conditionCreator) {
		List<ConditionSpecification> conditionSpecifications = new ArrayList<ConditionSpecification>();
		for (EventType iteratedEventType : iteratedEventTypes) {
			//TODO: we assume the iterated types to be bounded - should validate that
			int iteratedTypeIndex = eventTypes.indexOf(iteratedEventType);
			EventType precedingEventType = eventTypes.get(iteratedTypeIndex - 1);
			EventType succeedingEventType = eventTypes.get(iteratedTypeIndex + 1);
			conditionSpecifications.add(conditionCreator.createIteratedEventCondition(precedingEventType.getName(),
																	 				  iteratedEventType.getName(),
																	 				  succeedingEventType.getName()));
		}
		return conditionSpecifications;
	}

	private List<DoubleEventConditionSpecification> createNegativeConditionSpecifications(
			List<EventType> positiveNonIteratedEventTypes, List<EventType> negatedEventTypes,
			IConditionSpecificationCreator conditionCreator) {
		List<DoubleEventConditionSpecification> conditionSpecifications = new ArrayList<DoubleEventConditionSpecification>();
		Random random = new Random();
		for (EventType negatedEventType : negatedEventTypes) {
			if (random.nextDouble() > SimulationConfig.negatedConditionProbability) {
				continue;
			}
			int randomIndex = random.nextInt(positiveNonIteratedEventTypes.size());
			EventType positiveEventType = positiveNonIteratedEventTypes.get(randomIndex);
			conditionSpecifications.add(conditionCreator.createDoubleEventCondition(positiveEventType.getName(), 
																					negatedEventType.getName()));
		}
		return conditionSpecifications;
	}

	private List<EventType> getPositiveNonIteratedEventTypes(List<EventType> eventTypes,
															 List<EventType> negatedEventTypes,
															 List<EventType> iteratedEventTypes) {
		List<EventType> result = new ArrayList<EventType>(eventTypes);
		result.removeAll(negatedEventTypes);
		result.removeAll(iteratedEventTypes);
		return result;
	}

	protected abstract List<DoubleEventConditionSpecification> createPositiveNonIteratedConditionSpecifications(
											List<EventType> eventTypes, IConditionSpecificationCreator conditionCreator);
}
