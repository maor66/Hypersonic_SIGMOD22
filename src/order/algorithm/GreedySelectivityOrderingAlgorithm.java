package order.algorithm;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import config.EventRateConfig;
import order.IOrderingAlgorithm;
import order.cost.ICostModel;
import pattern.CompositePattern;
import pattern.condition.base.CNFCondition;

public class GreedySelectivityOrderingAlgorithm extends GreedyOrderingAlgorithm implements IOrderingAlgorithm {

	protected EventType selectNextEventType(CompositePattern compositePattern, ICostModel costModel,
										  List<EventType> prefix, List<EventType> remainingTypes) {
		if (prefix.isEmpty()) {
			return selectFirstEventType(remainingTypes);
		}
		EventType currentBestEventType = null;
		Double currentLowestSelectivity = 0.0;
		List<EventType> allEventTypes = compositePattern.getEventTypes();
		List<EventType> typesToExclude = new ArrayList<EventType>(allEventTypes);
		for (EventType eventType : prefix) {
			typesToExclude.remove(eventType);
		}
		CNFCondition mainCondition = (CNFCondition)compositePattern.getCondition();
		for (EventType candidateEventType : remainingTypes) {
			List<EventType> currTypesToExclude = new ArrayList<EventType>(typesToExclude);
			currTypesToExclude.remove(candidateEventType);
			CNFCondition conditionForCandidateType = 
					mainCondition.getConditionExcludingTypes(currTypesToExclude).getConditionForType(candidateEventType, false);
			Double currentSelectivity = conditionForCandidateType.getSelectivity();
			if (currentBestEventType == null || currentSelectivity < currentLowestSelectivity) {
				currentBestEventType = candidateEventType;
				currentLowestSelectivity = currentSelectivity;
			}
		}
		return currentBestEventType;
	}

	private EventType selectFirstEventType(List<EventType> remainingTypes) {
		EventType currentBestEventType = null;
		Integer currentLowestRate = 0;
		for (EventType eventType : remainingTypes) {
			Integer currentRate = EventRateConfig.eventRate.get(eventType.getName());
			if (currentBestEventType == null || currentRate < currentLowestRate) {
				currentBestEventType = eventType;
				currentLowestRate = currentRate;
			}
		}
		return currentBestEventType;
	}

}
