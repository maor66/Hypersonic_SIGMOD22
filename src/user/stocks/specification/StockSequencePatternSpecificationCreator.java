package sase.user.stocks.specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.config.SimulationConfig;
import sase.order.algorithm.DynamicOrderingAlgorithm;
import sase.pattern.EventTypesManager;
import sase.pattern.creation.PatternTypes;
import sase.specification.ConditionSpecification;
import sase.specification.PatternSpecification;
import sase.specification.SimulationSpecification;
import sase.specification.creators.ISimulationSpecificationCreator;
import sase.user.stocks.StockEventTypesManager;

public class StockSequencePatternSpecificationCreator implements ISimulationSpecificationCreator {
	
	private static final String[] filteredEventTypes = new String[] { StockEventTypesManager.australianCompanyEventTypeName };
	
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
						new SimulationSpecification(patternSpecifications[i], SimulationConfig.evaluationSpecifications[j]);
			}
		}
		return specifications;
	}
	
	private PatternSpecification[] createPatternSpecifications() {
		int numberOfPatterns = SimulationConfig.patternLengths.length * SimulationConfig.patternsPerLength;
		PatternSpecification[] result = new PatternSpecification[numberOfPatterns];
		for (int i = 0; i < SimulationConfig.patternLengths.length; ++i) {
			int currentLength = SimulationConfig.patternLengths[i];
			for (int j = 0; j < SimulationConfig.patternsPerLength; ++j) {
				result[i*SimulationConfig.patternsPerLength + j] = createPatternSpecification(currentLength);
			}
		}
		return result;
	}
	
	private PatternSpecification createPatternSpecification(int length) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		for (EventType eventType : EventTypesManager.getInstance().getKnownEventTypes()) {
			boolean shouldFilterEventType = false;
			for (String filteredTypeName : filteredEventTypes) {
				if (filteredTypeName.equals(eventType.getName())) {
					shouldFilterEventType = true;
					break;
				}
			}
			if (!shouldFilterEventType) {
				eventTypes.add(eventType);
			}
		}
		Random random = new Random();
		while (eventTypes.size() > length) {
			int randomIndex = random.nextInt(eventTypes.size());
			eventTypes.remove(randomIndex);
		}
		Collections.shuffle(eventTypes);
		String[][][] structure = new String[][][]{ new String[][] { new String[length],},};
		for (int i = 0; i < length; ++i) {
			structure[0][0][i] = eventTypes.get(i).getName();
		}
		ConditionSpecification[] conditionSpecifications = createConditionSpecifications(eventTypes);
		return new PatternSpecification(createPatternName(eventTypes, conditionSpecifications), PatternTypes.STOCK_PATTERN, 
				 										  SimulationConfig.timeWindowForPatternGenerator, structure, 
				 										  conditionSpecifications);
	}
	
	private String createPatternName(List<EventType> eventTypes, ConditionSpecification[] conditionSpecifications) {
		String typesString = "";
		for (EventType eventType : eventTypes) {
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
	
	private StockCorrelationConditionSpecification[] createConditionSpecifications(List<EventType> eventTypes) {
		List<StockCorrelationConditionSpecification> conditionSpecifications = 
													new ArrayList<StockCorrelationConditionSpecification>();
		List<List<EventType>> subsetsOfThree = DynamicOrderingAlgorithm.getAllSubSetsOfSize(eventTypes, 3);
		List<List<String>> subsetsOfEventNames = extractEventTypeNames(subsetsOfThree);
		List<List<String>> tripletsWithoutCondition = getTripletsWithoutCondition(subsetsOfEventNames, conditionSpecifications);
		while (!tripletsWithoutCondition.isEmpty()) {
			Random random = new Random();
			List<String> currentTriplet = tripletsWithoutCondition.get(random.nextInt(tripletsWithoutCondition.size()));
			int randomIndex = random.nextInt(3);
			int firstIndex = (randomIndex + 1) % 3;
			int secondIndex = (randomIndex + 2) % 3;
			conditionSpecifications.add(createCorrelationCondition(currentTriplet.get(firstIndex), 
																   currentTriplet.get(secondIndex)));
			tripletsWithoutCondition = getTripletsWithoutCondition(subsetsOfEventNames, conditionSpecifications);
		}
		return conditionSpecifications.toArray(new StockCorrelationConditionSpecification[0]);
	}
	
	private List<List<String>> extractEventTypeNames(List<List<EventType>> setsOfEventTypes) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<EventType> list : setsOfEventTypes) {
			result.add(eventTypeListToEventNameList(list));
		}
		return result;
	}
	
	private List<String> eventTypeListToEventNameList(List<EventType> eventTypes) {
		List<String> eventTypeNames = new ArrayList<String>();
		for (EventType eventType : eventTypes) {
			eventTypeNames.add(eventType.getName());
		}
		return eventTypeNames;
	}
	
	private List<List<String>> getTripletsWithoutCondition(List<List<String>> triplets,
									  List<StockCorrelationConditionSpecification> conditionSpecifications) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<String> triplet : triplets) {
			if (!containsCondition(triplet, conditionSpecifications)) {
				result.add(triplet);
			}
		}
		return result;
	}
	
	private boolean containsCondition(List<String> eventTypeNameTriplet,
									  List<StockCorrelationConditionSpecification> conditionSpecifications) {
		for (StockCorrelationConditionSpecification conditionSpecification : conditionSpecifications) {
			if (eventTypeNameTriplet.contains(conditionSpecification.getFirstEventName()) &&
				eventTypeNameTriplet.contains(conditionSpecification.getSecondEventName())) {
				return true;
			}
		}
		return false;
	}
	
	private StockCorrelationConditionSpecification createCorrelationCondition(String firstTypeName, String secondTypeName) {
		double correlationLimit = SimulationConfig.correlations[new Random().nextInt(SimulationConfig.correlations.length)];
		return new StockCorrelationConditionSpecification(firstTypeName, secondTypeName, correlationLimit);
	}

}
