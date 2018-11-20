package sase.evaluation.nfa.lazy.order.algorithm;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IIncrementalOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.CompositePattern;

public class GreedyCostModelOrderingAlgorithm extends GreedyOrderingAlgorithm implements IIncrementalOrderingAlgorithm {

	protected EventType selectNextEventType(CompositePattern compositePattern, ICostModel costModel,
										  List<EventType> prefix, List<EventType> remainingTypes) {
		EventType currentBestEventType = null;
		Double currentLowestCost = null;
		for (EventType candidateEventType : remainingTypes) {
			List<EventType> tempList = new ArrayList<EventType>(prefix);
			tempList.add(candidateEventType);
			CompositePattern tempPattern = compositePattern.getSubPattern(tempList);
			Double currentCost = costModel.getOrderCost(tempPattern, tempList);
			if (currentBestEventType == null || currentCost < currentLowestCost) {
				currentBestEventType = candidateEventType;
				currentLowestCost = currentCost;
			}
		}
		return currentBestEventType;
	}

}
