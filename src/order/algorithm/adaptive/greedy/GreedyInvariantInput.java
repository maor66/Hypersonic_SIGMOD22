package sase.order.algorithm.adaptive.greedy;

import sase.adaptive.monitoring.invariant.InvariantInput;
import sase.base.EventType;
import sase.pattern.condition.base.CNFCondition;

public class GreedyInvariantInput extends InvariantInput {

	public EventType eventType;
	public CNFCondition condition;
	
	public GreedyInvariantInput(EventType eventType, CNFCondition condition) {
		this.eventType = eventType;
		this.condition = condition;
	}
}
