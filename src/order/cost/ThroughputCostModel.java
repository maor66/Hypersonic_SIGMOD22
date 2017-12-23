package sase.order.cost;

import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;
import sase.simulator.Environment;

public class ThroughputCostModel implements ICostModel {
	
	protected HashMap<EventType, Double> costsOfLastOrderStates = null;

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
			int currentEventRate = Environment.getEnvironment().getEventRateEstimator().getEventRateEstimate(currentEventType);
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

}
