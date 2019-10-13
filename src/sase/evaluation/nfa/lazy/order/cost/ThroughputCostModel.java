package sase.evaluation.nfa.lazy.order.cost;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.multi.MultiPatternTree;
import sase.multi.MultiPatternTreeNode;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class ThroughputCostModel implements ICostModel {
	
	protected HashMap<EventType, Double> costsOfLastOrderStates = null;

	public Double getCostOfSingleState(CNFCondition filteredCondition, EventType typeOfState, double previousStateCost) {
//		List<CNFCondition> conditionsByOrder = filteredCondition.getSubConditionsByOrder(Arrays.asList(typeOfState), true);
		return previousStateCost * typeOfState.getRate() * filteredCondition.getSelectivity();
	}

	@Override
	public Double getOrderCost(Pattern pattern, List<EventType> order) {
		double orderCost = 1.0;
		double currentStateCost, previousStateCost = 1.0;
		CNFCondition condition = (CNFCondition) pattern.getCondition();
		//the second parameter is 'true' since we also call this function on partial orders,
		//when some of the event types of the pattern may not be a part of the order
		List<CNFCondition> conditionsByOrder = condition.getSubConditionsByOrder(order, true);
		costsOfLastOrderStates = new HashMap<EventType, Double>();
		for (int i = 0; i < order.size(); ++i) {
			EventType currentEventType = order.get(i);
			double currentEventRate = currentEventType.getRate();
			Double currentConditionSelectivity = conditionsByOrder.get(i).getSelectivity();
			if (currentConditionSelectivity == null) {
				return null;
			}
			currentStateCost = previousStateCost * currentEventRate * currentConditionSelectivity;
			costsOfLastOrderStates.put(currentEventType, currentStateCost);
			orderCost += currentStateCost;
			previousStateCost = currentStateCost;
		}

		return orderCost;
	}

	@Override
	public Double getMPTCost(MultiPatternTree mpt) {
		return recursiveGetCost(mpt.getRoot(), 1.0);
	}

	private Double recursiveGetCost(MultiPatternTreeNode node, Double parentCost) {
		Double nodeCost = parentCost;
		if (!node.isRoot()) {
			Long nodeTimeWindow = node.getTimeWindow();
			nodeCost *= node.getEventType().getRate() * node.getTimeWindow() * node.getCondition().getSelectivity();
			Long parentTimeWindow = node.getParent().getTimeWindow();
			if (parentTimeWindow != null && parentTimeWindow != nodeTimeWindow) {
				nodeCost = nodeCost / parentTimeWindow * nodeTimeWindow;
			}
		}
		Double totalCost = nodeCost;
		for (MultiPatternTreeNode child : node.getAllChildren()) {
			totalCost += recursiveGetCost(child, nodeCost);
		}
		return totalCost;
	}
}
