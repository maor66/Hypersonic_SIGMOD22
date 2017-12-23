package order.algorithm.adaptive.greedy;

import adaptive.monitoring.invariant.InvariantInput;
import base.EventType;
import pattern.condition.base.CNFCondition;

public class GreedyInvariantInput extends InvariantInput {

	public EventType eventType;
	public CNFCondition condition;
	
	public GreedyInvariantInput(EventType eventType, CNFCondition condition) {
		this.eventType = eventType;
		this.condition = condition;
	}
}
