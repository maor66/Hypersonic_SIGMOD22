package sase.user.stocks.specification.creators;

import sase.specification.condition.ConditionSpecification;
import sase.specification.condition.DoubleEventConditionSpecification;
import sase.specification.creators.condition.IConditionSpecificationCreator;
import sase.user.stocks.specification.IterativeValueCmpStockCorrelationConditionSpecification;
import sase.user.stocks.specification.StockDeltaOrderingConditionSpecification;

public class StockDeltaConditionCreator implements IConditionSpecificationCreator {

	public DoubleEventConditionSpecification createDoubleEventCondition(String firstTypeName,
																		String secondTypeName) {
		return new StockDeltaOrderingConditionSpecification(firstTypeName, secondTypeName);
	}

	public ConditionSpecification createIteratedEventCondition(String precedingTypeName,
																  String iteratedTypeName, 
																  String succeedingTypeName) {
		return new IterativeValueCmpStockCorrelationConditionSpecification(precedingTypeName,
																		   iteratedTypeName,
																		   succeedingTypeName);
	}

}
