package user.stocks.specification.creators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import base.EventType;
import config.SimulationConfig;
import pattern.creation.PatternTypes;
import specification.creators.RandomPatternSpecificationCreator;
import specification.creators.condition.IConditionSpecificationCreator;
import specification.creators.condition.IConditionSpecificationSetCreator;
import user.stocks.StockEventTypesManager;

public class StockPatternSpecificationCreator extends RandomPatternSpecificationCreator {

	protected static String[] filteredEventTypes = new String[] {};

	public StockPatternSpecificationCreator(IConditionSpecificationCreator conditionCreator,
											IConditionSpecificationSetCreator conditionSetCreator) {
		super(conditionCreator, conditionSetCreator);
	}
	
	@Override
	protected PatternTypes createPatternType() {
		return PatternTypes.STOCK_PATTERN;
	}
	
	@Override
	protected String[][][] createPatternStructure(List<EventType> eventTypes, 
												  List<EventType> negatedEventTypes,
												  List<EventType> iteratedEventTypes) {
		switch (SimulationConfig.mainOperatorType) {
			case SEQ:
				return createSequencePatternStructure(eventTypes, negatedEventTypes, iteratedEventTypes);
			case AND_SEQ:
				return createConjunctionPatternStructure(eventTypes, negatedEventTypes, iteratedEventTypes);
			case OR:
				return createDisjunctionPatternStructure(eventTypes, negatedEventTypes, iteratedEventTypes);
			default:
				return null;
		}
	}

	private String[][][] createSequencePatternStructure(List<EventType> eventTypes, 
														List<EventType> negatedEventTypes,
														List<EventType> iteratedEventTypes) {
		String[][][] structure = new String[][][]{ new String[][] { new String[eventTypes.size()],},};
		Collections.shuffle(eventTypes);
		while (containsUnboundedSpecialEventType(eventTypes, negatedEventTypes, iteratedEventTypes)) {
			Collections.shuffle(eventTypes);
		}
		for (int i = 0; i < eventTypes.size(); ++i) {
			structure[0][0][i] = eventTypes.get(i).getName();
		}
		return structure;
	}

	private boolean containsUnboundedSpecialEventType(List<EventType> eventTypes, 
													  List<EventType> negatedEventTypes,
													  List<EventType> iteratedEventTypes) {
		if (eventTypes.isEmpty()) {
			return false;
		}
		if (negatedEventTypes.contains(eventTypes.get(0)) || iteratedEventTypes.contains(eventTypes.get(0))) {
			return true;
		}
		return (negatedEventTypes.contains(eventTypes.get(eventTypes.size() - 1)) || 
				iteratedEventTypes.contains(eventTypes.get(eventTypes.size() - 1)));
	}

	@Override
	protected List<EventType> createNegatedEventTypes() {
		List<EventType> negatedEventTypes = new ArrayList<EventType>();
		for (String eventName : StockEventTypesManager.mediumCompaniesEventTypeNames) {
			negatedEventTypes.add(StockEventTypesManager.getInstance().getTypeByName(eventName));
		}
		return createSpecialEventTypes(negatedEventTypes, SimulationConfig.negatedEventsNumber);
	}

	@Override
	protected List<EventType> createIteratedEventTypes() {
		List<EventType> iteratedEventTypes = new ArrayList<EventType>();
		for (String eventName : StockEventTypesManager.smallCompaniesEventTypeNames) {
			iteratedEventTypes.add(StockEventTypesManager.getInstance().getTypeByName(eventName));
		}
		return createSpecialEventTypes(iteratedEventTypes, SimulationConfig.iteratedEventsNumber);
	}
	
	private List<EventType> createSpecialEventTypes(List<EventType> availableEventTypes, int numberOfSpecialTypes) {
		if (numberOfSpecialTypes >= availableEventTypes.size()) {
			throw new RuntimeException("Illegal number of negated or iterated types specified.");
		}
		List<EventType> result = new ArrayList<EventType>();
		int remainingTypesNumber = numberOfSpecialTypes;
		Random random = new Random();
		while (remainingTypesNumber > 0) {
			int randomIndex = random.nextInt(availableEventTypes.size());
			EventType currentEventType = availableEventTypes.get(randomIndex);
			if (!result.contains(currentEventType)) {
				result.add(currentEventType);
				remainingTypesNumber--;
			}
		}
		return result;
	}
	
	private String[][][] createConjunctionPatternStructure(List<EventType> eventTypes,
														   List<EventType> negatedEventTypes,
														   List<EventType> iteratedEventTypes) {
		if (!negatedEventTypes.isEmpty() || !iteratedEventTypes.isEmpty()) {
			throw new RuntimeException("Negated/iterated events are unsupported for conjunction patterns");
		}
		Collections.shuffle(eventTypes);
		String[][][] structure = new String[][][]{ new String[eventTypes.size()][],};
		for (int i = 0; i < eventTypes.size(); ++i) {
			structure[0][i] = new String[] { eventTypes.get(i).getName() };
		}
		return structure;
	}

	private String[][][] createDisjunctionPatternStructure(List<EventType> eventTypes,
														   List<EventType> negatedEventTypes,
														   List<EventType> iteratedEventTypes) {
		if (!negatedEventTypes.isEmpty() || !iteratedEventTypes.isEmpty()) {
			throw new RuntimeException("Negated/iterated events are unsupported for disjunction patterns");
		}
		int disjunctionsNumber = SimulationConfig.numberOfDisjunctions;
		if (disjunctionsNumber < 2) {
			throw new RuntimeException("A disjunction must contain at least two clauses");
		}
		int normalSequenceSize = eventTypes.size() / disjunctionsNumber;
		int lastSequenceSize = normalSequenceSize + eventTypes.size() % disjunctionsNumber;
		Collections.shuffle(eventTypes);
		String[][][] structure = new String[disjunctionsNumber][][];
		for (int i = 0; i < disjunctionsNumber; ++i) {
			int currentSequenceSize = (i < disjunctionsNumber - 1) ? normalSequenceSize : lastSequenceSize;
			structure[i] = new String[][] { new String[currentSequenceSize] };
			for (int j = 0; j < currentSequenceSize; ++j) {
				structure[i][0][j] = eventTypes.get(i * normalSequenceSize + j).getName();
			}
		}
		return structure;
	}
}
