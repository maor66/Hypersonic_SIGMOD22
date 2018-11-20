package sase.specification.creators;

import sase.config.SimulationConfig;
import sase.specification.creators.condition.ConditionSpecificationCreatorFactory;
import sase.specification.creators.condition.IConditionSpecificationCreator;
import sase.specification.creators.condition.IConditionSpecificationSetCreator;
import sase.user.stocks.specification.creators.CorrelationSelectivityEstimationSetupCreator;
import sase.user.stocks.specification.creators.DeltaSelectivityEstimationSetupCreator;
import sase.user.stocks.specification.creators.StockMultiPatternSpecificationCreator;
import sase.user.stocks.specification.creators.StockPatternSpecificationCreator;

public class SpecificationCreatorFactory {

	public static ISimulationSpecificationCreator createSpecificationCreator() {
		switch(SimulationConfig.specificationCreatorType) {
			case CORRELATION_SELECTIVITY_ESTIMATION:
				return new CorrelationSelectivityEstimationSetupCreator();
			case DELTA_SELECTIVITY_ESTIMATION:
				return new DeltaSelectivityEstimationSetupCreator();
			case CROSS_PRODUCT_SINGLE:
				return new SinglePatternCrossProductSimulationSpecificationCreator();
			case STOCK_EVALUATION:
				IConditionSpecificationCreator conditionCreator = 
								ConditionSpecificationCreatorFactory.createConditionSpecificationCreator();
				IConditionSpecificationSetCreator conditionSetCreator = 
								ConditionSpecificationCreatorFactory.createConditionSpecificationSetCreator();
				return new StockPatternSpecificationCreator(conditionCreator, conditionSetCreator);
			case CROSS_PRODUCT_MULTI_STOCK:
				return new StockMultiPatternSpecificationCreator();
			case NONE:
			default:
				return null;
		}
	}

}
