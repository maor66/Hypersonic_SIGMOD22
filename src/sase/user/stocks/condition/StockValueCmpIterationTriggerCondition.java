package sase.user.stocks.condition;

import sase.pattern.condition.iteration.eager.IterationTriggerCondition;

public class StockValueCmpIterationTriggerCondition extends IterationTriggerCondition {

	public StockValueCmpIterationTriggerCondition(StockFirstValueCmpCondition triggerCondition) {
		super(triggerCondition);
	}

}
