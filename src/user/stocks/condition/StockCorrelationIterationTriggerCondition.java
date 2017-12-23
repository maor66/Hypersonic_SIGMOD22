package user.stocks.condition;

import pattern.condition.iteration.eager.IterationTriggerCondition;

public class StockCorrelationIterationTriggerCondition extends IterationTriggerCondition {

	public StockCorrelationIterationTriggerCondition(StockCorrelationCondition triggerCondition) {
		super(triggerCondition);
	}

}
