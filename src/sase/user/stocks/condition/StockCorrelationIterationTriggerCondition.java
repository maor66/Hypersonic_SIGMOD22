package sase.user.stocks.condition;

import sase.pattern.condition.iteration.eager.IterationTriggerCondition;

public class StockCorrelationIterationTriggerCondition extends IterationTriggerCondition {

	public StockCorrelationIterationTriggerCondition(StockCorrelationCondition triggerCondition) {
		super(triggerCondition);
	}

}
