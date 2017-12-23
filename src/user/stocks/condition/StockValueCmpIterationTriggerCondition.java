package user.stocks.condition;

import pattern.condition.iteration.eager.IterationTriggerCondition;

public class StockValueCmpIterationTriggerCondition extends IterationTriggerCondition {

	public StockValueCmpIterationTriggerCondition(StockFirstValueCmpCondition triggerCondition) {
		super(triggerCondition);
	}

}
