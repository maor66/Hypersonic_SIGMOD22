package sase.specification.creators.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.config.SimulationConfig;
import sase.specification.condition.DoubleEventConditionSpecification;

public class CountersConditionSpecificationSetCreator extends BasicConditionSpecificationSetCreator {
	
	private static final int minNumberOfConditions = 1;
	
	@Override
	protected List<DoubleEventConditionSpecification> createPositiveNonIteratedConditionSpecifications(
			List<EventType> eventTypes, IConditionSpecificationCreator conditionCreator) {
		int numberOfConditions = getNumberOfConditions(eventTypes.size());
		List<DoubleEventConditionSpecification> conditionSpecifications = new ArrayList<DoubleEventConditionSpecification>();
		while (conditionSpecifications.size() < numberOfConditions) {
			List<String> eventNamesForCurrentCondition = selectUnoccupiedEventNames(eventTypes, conditionSpecifications);
			conditionSpecifications.add(conditionCreator.createDoubleEventCondition(eventNamesForCurrentCondition.get(0), 
																					eventNamesForCurrentCondition.get(1)));
		}
		return conditionSpecifications;
	}
	
	private int getNumberOfConditions(int numberOfEvents) {
		int minPatternSize = SimulationConfig.patternSizes[0];
		if (numberOfEvents < minPatternSize) {
			return minNumberOfConditions; 
		}
		if (SimulationConfig.conditionToEventRatio == null) {
			return SimulationConfig.numbersOfConditions[numberOfEvents - minPatternSize];
		}
		return (int) (numberOfEvents * SimulationConfig.conditionToEventRatio);
	}

	private List<String> selectUnoccupiedEventNames(List<EventType> eventTypes,
												   	List<DoubleEventConditionSpecification> conditionSpecifications) {
		while (true) {
			String firstEventName = getRandomEventName(eventTypes);
			String secondEventName = getRandomEventName(eventTypes);
			while (firstEventName.equals(secondEventName)) {
				secondEventName = getRandomEventName(eventTypes);
			}
			boolean areEventsOccupied = false;
			for (DoubleEventConditionSpecification conditionSpecification : conditionSpecifications) {
				if (consistsOfEventNames(conditionSpecification, firstEventName, secondEventName)) {
					areEventsOccupied = true;
					break;
				}
			}
			if (!areEventsOccupied) {
				return Arrays.asList(new String[] {firstEventName, secondEventName});
			}
		}
	}
	
	private String getRandomEventName(List<EventType> eventTypes) {
		return eventTypes.get(new Random().nextInt(eventTypes.size())).getName();
	}
	
	private boolean consistsOfEventNames(DoubleEventConditionSpecification conditionSpecification,
										 String firstEventName, String secondEventName) {
		if (conditionSpecification.getFirstEventName().equals(firstEventName) && 
			conditionSpecification.getSecondEventName().equals(secondEventName)) {
				return true;
		}
		return (conditionSpecification.getFirstEventName().equals(secondEventName) && 
				conditionSpecification.getSecondEventName().equals(firstEventName));
	}

}
