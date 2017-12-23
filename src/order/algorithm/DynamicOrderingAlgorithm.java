package sase.order.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.order.IOrderingAlgorithm;
import sase.order.cost.ICostModel;
import sase.pattern.Pattern;

public class DynamicOrderingAlgorithm implements IOrderingAlgorithm {
	
	public static List<List<EventType>> getAllSubSetsOfSize(List<EventType> events, int size) {
		List<List<EventType>> result = new ArrayList<List<EventType>>();
		if (events.size() < size || size <= 0) {
			return result;
		}
		if (size == 1) {
			for (EventType eventType : events) {
				List<EventType> listForEventType = new ArrayList<EventType>();
				listForEventType.add(eventType);
				result.add(listForEventType);
			}
			return result;
		}
		if (events.size() == size) {
			result.add(new ArrayList<EventType>(events));
			return result;
		}
		List<EventType> eventsWithoutFirst = new ArrayList<EventType>(events);
		EventType firstEventType = eventsWithoutFirst.remove(0);
		result.addAll(getAllSubSetsOfSize(eventsWithoutFirst, size - 1));
		for (List<EventType> list : result) {
			list.add(0, firstEventType);
		}
		result.addAll(getAllSubSetsOfSize(eventsWithoutFirst, size));
		return result;
	}
	
	protected List<EventType> calculateBestOrder(Pattern pattern, ICostModel costModel,
											   	 List<EventType> events, HashMap<List<EventType>, List<EventType>> prevMap) {
		if (events.size() == 1) {
			return new ArrayList<EventType>(events);
		}
		Double currentLowestCost = 0.0;
		List<EventType> currentBestOrder = null;
		for (EventType eventType : events) {
			List<EventType> currentList = new ArrayList<EventType>(events);
			currentList.remove(eventType);
			List<EventType> currentBestSubOrder = new ArrayList<EventType>(prevMap.get(currentList));
			currentBestSubOrder.add(eventType);
			Double currentCost = costModel.getOrderCost(pattern, currentBestSubOrder);
			if (currentBestOrder == null || currentCost < currentLowestCost) {
				currentBestOrder = currentBestSubOrder;
				currentLowestCost = currentCost;
			}
		}
		return currentBestOrder;
	}

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		List<EventType> events = pattern.getEventTypes();
		HashMap<List<EventType>, List<EventType>> currMap = new HashMap<List<EventType>, List<EventType>>();
		HashMap<List<EventType>, List<EventType>> prevMap = null;
		for (int i = 1; i < events.size(); ++i) {
			List<List<EventType>> subsets = getAllSubSetsOfSize(events, i);
			for (List<EventType> subset : subsets) {
				List<EventType> bestOrder = calculateBestOrder(pattern, costModel, subset, prevMap);
				currMap.put(subset, bestOrder);
			}
			prevMap = currMap;
			currMap = new HashMap<List<EventType>, List<EventType>>();
		}
		return calculateBestOrder(pattern, costModel, events, prevMap);
	}

}
