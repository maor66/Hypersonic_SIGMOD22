package user.stocks.condition;

import base.EventType;
import pattern.condition.iteration.lazy.GroupByAttributeCondition;
import user.stocks.StockEventTypesManager;

public class StockSameCompanyCondition extends GroupByAttributeCondition {

	public StockSameCompanyCondition(EventType iteratedType) {
		super(iteratedType, StockEventTypesManager.labelAttributeIndex);
	}

}
