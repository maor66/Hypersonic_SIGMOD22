package sase.user.stocks.condition;

import sase.base.EventType;
import sase.pattern.condition.iteration.lazy.GroupByAttributeCondition;
import sase.user.stocks.StockEventTypesManager;

public class StockSameCompanyCondition extends GroupByAttributeCondition {

	public StockSameCompanyCondition(EventType iteratedType) {
		super(iteratedType, StockEventTypesManager.labelAttributeIndex);
	}

}
