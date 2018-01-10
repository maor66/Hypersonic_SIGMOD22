package sase.specification.creators.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.order.algorithm.DynamicOrderingAlgorithm;
import sase.specification.DoubleEventConditionSpecification;
import sase.specification.creators.RandomPatternSpecificationCreator;

public class TriplesConditionSpecificationSetCreator extends BasicConditionSpecificationSetCreator {

	@Override
	protected List<DoubleEventConditionSpecification> createPositiveNonIteratedConditionSpecifications(
											List<EventType> eventTypes, IConditionSpecificationCreator conditionCreator) {
		List<List<EventType>> subsetsOfThree = DynamicOrderingAlgorithm.getAllSubSetsOfSize(eventTypes, 3);
		List<List<String>> subsetsOfEventNames = extractEventTypeNames(subsetsOfThree);
		List<DoubleEventConditionSpecification> conditionSpecifications = new ArrayList<DoubleEventConditionSpecification>();
		List<List<String>> tripletsWithoutCondition = getTripletsWithoutCondition(subsetsOfEventNames, conditionSpecifications);
		while (!tripletsWithoutCondition.isEmpty()) {
			Random random = new Random();
			List<String> currentTriplet = tripletsWithoutCondition.get(random.nextInt(tripletsWithoutCondition.size()));
			int randomIndex = random.nextInt(3);
			int firstIndex = (randomIndex + 1) % 3;
			int secondIndex = (randomIndex + 2) % 3;
			conditionSpecifications.add(conditionCreator.createDoubleEventCondition(currentTriplet.get(firstIndex), 
																   					currentTriplet.get(secondIndex)));
			tripletsWithoutCondition = getTripletsWithoutCondition(subsetsOfEventNames, conditionSpecifications);
		}
		return conditionSpecifications;
	}
	
	private List<List<String>> extractEventTypeNames(List<List<EventType>> setsOfEventTypes) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<EventType> list : setsOfEventTypes) {
			result.add(RandomPatternSpecificationCreator.eventTypeListToEventNameList(list));
		}
		return result;
	}
	
	private List<List<String>> getTripletsWithoutCondition(List<List<String>> triplets,
									  					   List<DoubleEventConditionSpecification> conditionSpecifications) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<String> triplet : triplets) {
			if (!containsCondition(triplet, conditionSpecifications)) {
				result.add(triplet);
			}
		}
		return result;
	}
	
	private boolean containsCondition(List<String> eventTypeNameTriplet,
									  List<DoubleEventConditionSpecification> conditionSpecifications) {
		for (DoubleEventConditionSpecification conditionSpecification : conditionSpecifications) {
			if (eventTypeNameTriplet.contains(conditionSpecification.getFirstEventName()) &&
				eventTypeNameTriplet.contains(conditionSpecification.getSecondEventName())) {
				return true;
			}
		}
		return false;
	}
}
