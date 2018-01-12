package user.stocks.specification.creators;

import specification.ConditionSpecification;
import specification.DoubleEventConditionSpecification;
import specification.creators.condition.IConditionSpecificationCreator;
import user.stocks.specification.IterativeValueCmpStockCorrelationConditionSpecification;
import user.stocks.specification.StockDeltaOrderingConditionSpecification;

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
