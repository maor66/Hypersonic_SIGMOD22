package sase.evaluation.nfa.lazy.order.algorithm;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.config.EventRateConfig;
import sase.evaluation.nfa.lazy.order.IIncrementalOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.CompositePattern;
import sase.pattern.condition.base.CNFCondition;

public class GreedySelectivityOrderingAlgorithm extends GreedyOrderingAlgorithm implements IIncrementalOrderingAlgorithm {

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
		Double currentLowestRate = null;
		for (EventType eventType : remainingTypes) {
			Double currentRate = EventRateConfig.eventRate.get(eventType.getName());
			if (currentBestEventType == null || currentRate < currentLowestRate) {
				currentBestEventType = eventType;
				currentLowestRate = currentRate;
			}
		}
		return currentBestEventType;
	}

}
