package sase.specification.creators.condition;

import sase.config.SimulationConfig;
import sase.user.stocks.specification.creators.StockCorrelationConditionCreator;
import sase.user.stocks.specification.creators.StockDeltaConditionCreator;

public class ConditionSpecificationCreatorFactory {

	public static IConditionSpecificationSetCreator createConditionSpecificationSetCreator() {
		switch (SimulationConfig.conditionSetCreatorType) {
			case COUNTERS:
				return new CountersConditionSpecificationSetCreator();
			case TRIPLES:
				return new TriplesConditionSpecificationSetCreator();
			default:
				return null;
		}
	}
	
	public static IConditionSpecificationCreator createConditionSpecificationCreator() {
		return createConditionSpecificationCreator(SimulationConfig.conditionCreatorType);
	}
	
	public static IConditionSpecificationCreator createConditionSpecificationCreator(ConditionSpecificationCreatorTypes type) {
		switch (type) {
			case STOCK_CORRELATION:
				return new StockCorrelationConditionCreator();
			case STOCK_DELTA:
				return new StockDeltaConditionCreator();
			default:
				return null;
		}
	}

}
