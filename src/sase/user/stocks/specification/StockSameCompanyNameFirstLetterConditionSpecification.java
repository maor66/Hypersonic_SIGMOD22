package sase.user.stocks.specification;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.DoubleEventConditionSpecification;
import sase.user.stocks.condition.StockSameCompanyNameFirstLetterCondition;

public class StockSameCompanyNameFirstLetterConditionSpecification extends DoubleEventConditionSpecification {

	public StockSameCompanyNameFirstLetterConditionSpecification(String firstEventName, String secondEventName,
			Double selectivity) {
		super(firstEventName, secondEventName, selectivity);
		// TODO Auto-generated constructor stub
	}
	
	public StockSameCompanyNameFirstLetterConditionSpecification(String firstEventName, String secondEventName) {
		super(firstEventName, secondEventName, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
			Double conditionSelectivity) {
		return new StockSameCompanyNameFirstLetterCondition(firstType, secondType, selectivity);
	}
	
	// MAX : Not sure how to implement toString and getShortDescription for this ...

}
