package sase.order.algorithm;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.order.IOrderingAlgorithm;
import sase.order.cost.ICostModel;
import sase.pattern.CompositePattern;

public class GreedyCostModelOrderingAlgorithm extends GreedyOrderingAlgorithm implements IOrderingAlgorithm {

	protected EventType selectNextEventType(CompositePattern compositePattern, ICostModel costModel,
										  List<EventType> prefix, List<EventType> remainingTypes) {
		EventType currentBestEventType = null;
		Double currentLowestCost = 0.0;
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
