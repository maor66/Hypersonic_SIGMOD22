package sase.specification.creators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.config.SimulationConfig;
import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.creation.PatternTypes;
import sase.specification.SimulationSpecification;
import sase.specification.condition.ConditionSpecification;
import sase.specification.creators.condition.IConditionSpecificationCreator;
import sase.specification.creators.condition.IConditionSpecificationSetCreator;
import sase.specification.workload.PatternSpecification;
import sase.specification.workload.SinglePatternWorkloadSpecification;
import sase.user.stocks.StockEventTypesManager;

public abstract class RandomPatternSpecificationCreator implements ISimulationSpecificationCreator {
	
	public static List<String> eventTypeListToEventNameList(List<EventType> eventTypes) {
		List<String> eventTypeNames = new ArrayList<String>();
		for (EventType eventType : eventTypes) {
			eventTypeNames.add(eventType.getName());
		}
		return eventTypeNames;
	}
	
	protected static String[] filteredEventTypes = {};
	
	private IConditionSpecificationCreator conditionCreator;
	private IConditionSpecificationSetCreator conditionSetCreator;
	
	public RandomPatternSpecificationCreator(IConditionSpecificationCreator conditionCreator,
											 IConditionSpecificationSetCreator conditionSetCreator) {
		this.conditionCreator = conditionCreator;
		this.conditionSetCreator = conditionSetCreator;
	}
	
	@Override
	public SimulationSpecification[] createSpecifications() {
		if (!EventTypesManager.isInitialized()) {
			EventTypesManager.setInstance(new StockEventTypesManager());
			EventTypesManager.getInstance().initializeTypes();
		}
		PatternSpecification[] patternSpecifications = createPatternSpecifications();
		int numberOfSpecifications = SimulationConfig.evaluationSpecifications.length * patternSpecifications.length;
		SimulationSpecification[] specifications = new SimulationSpecification[numberOfSpecifications];
		for (int i = 0; i < patternSpecifications.length; ++i) {
			for (int j = 0; j < SimulationConfig.evaluationSpecifications.length; ++j) {
				int currentSpecificationIndex = i * SimulationConfig.evaluationSpecifications.length + j;
				specifications[currentSpecificationIndex] = 
						new SimulationSpecification(new SinglePatternWorkloadSpecification(patternSpecifications[i]), 
													SimulationConfig.evaluationSpecifications[j]);
			}
		}
		return specifications;
	}
	
	private PatternSpecification[] createPatternSpecifications() {
		int numberOfPatterns = SimulationConfig.patternSizes.length * SimulationConfig.patternsPerLength;
		PatternSpecification[] result = new PatternSpecification[numberOfPatterns];
		for (int i = 0; i < SimulationConfig.patternSizes.length; ++i) {
			int currentLength = SimulationConfig.patternSizes[i] + SimulationConfig.negatedEventsNumber;
			if (SimulationConfig.mainOperatorType == PatternOperatorTypes.OR) {
				currentLength *= SimulationConfig.numberOfDisjunctions;
			}
			for (int j = 0; j < SimulationConfig.patternsPerLength; ++j) {
				result[i*SimulationConfig.patternsPerLength + j] = createPatternSpecification(currentLength);
			}
		}
		return result;
	}
	
	private PatternSpecification createPatternSpecification(int length) {
		List<EventType> negatedEventTypes = createNegatedEventTypes();
		List<EventType> iteratedEventTypes = createIteratedEventTypes();
		List<EventType> eventTypes = createEventTypesForPattern(length, negatedEventTypes, iteratedEventTypes);
		String[][][] patternStructure = createPatternStructure(eventTypes, negatedEventTypes, iteratedEventTypes);
		ConditionSpecification[] conditionSpecifications = 
				conditionSetCreator.createConditionSpecifications(eventTypes, negatedEventTypes, 
																  iteratedEventTypes, patternStructure,
																  conditionCreator);
		return new PatternSpecification(createPatternName(eventTypes, conditionSpecifications, 
														  negatedEventTypes, iteratedEventTypes),
										createPatternType(), 
										getTimeWindowForPatternGenerator(), patternStructure,
										eventTypeListToEventNameList(negatedEventTypes).toArray(new String[0]), 
										eventTypeListToEventNameList(iteratedEventTypes).toArray(new String[0]),
				 						conditionSpecifications, SlaVerifierTypes.NONE);
	}
	
	protected List<EventType> createEventTypesForPattern(int length, 
														 List<EventType> negatedEventTypes, 
														 List<EventType> iteratedEventTypes) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		for (EventType eventType : EventTypesManager.getInstance().getKnownEventTypes()) {
			boolean shouldFilterEventType = false;
			for (String filteredTypeName : filteredEventTypes) {
				if (filteredTypeName.equals(eventType.getName())) {
					shouldFilterEventType = true;
					break;
				}
			}
			if (!shouldFilterEventType && !negatedEventTypes.contains(eventType) && !iteratedEventTypes.contains(eventType)) {
				eventTypes.add(eventType);
			}
		}
		int actualLength = length - negatedEventTypes.size() - iteratedEventTypes.size();
		Random random = new Random();
		while (eventTypes.size() > actualLength) {
			int randomIndex = random.nextInt(eventTypes.size());
			eventTypes.remove(randomIndex);
		}
		eventTypes.addAll(negatedEventTypes);
		eventTypes.addAll(iteratedEventTypes);
		return eventTypes;
	}
	
	protected String createPatternName(List<EventType> eventTypes, ConditionSpecification[] conditionSpecifications,
									   List<EventType> negatedEventTypes, List<EventType> iteratedEventTypes) {
		String typesString = "";
		for (EventType eventType : eventTypes) {
			if (negatedEventTypes.contains(eventType)) {
				typesString += "!";
			}
			typesString += EventTypesManager.getInstance().getShortNameByLongName(eventType.getName());
			typesString += "|";
		}
		String conditionsString = "";
		for (ConditionSpecification conditionSpecification : conditionSpecifications) {
			conditionsString += conditionSpecification.getShortDescription();
			conditionsString += "|";
		}
		return String.format("%s(%s)", typesString, conditionsString);
	}
	
	protected Long getTimeWindowForPatternGenerator() {
		return SimulationConfig.timeWindowForPatternGenerator;
	}
	
	protected abstract PatternTypes createPatternType();
	protected abstract String[][][] createPatternStructure(List<EventType> eventTypes,
			  											   List<EventType> negatedEventTypes,
			  											   List<EventType> iteratedEventTypes);
	protected abstract List<EventType> createNegatedEventTypes();
	protected abstract List<EventType> createIteratedEventTypes();
}
