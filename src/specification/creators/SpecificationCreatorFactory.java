package sase.specification.creators;

import sase.config.SimulationConfig;
import sase.specification.creators.condition.ConditionSpecificationCreatorFactory;
import sase.specification.creators.condition.IConditionSpecificationCreator;
import sase.specification.creators.condition.IConditionSpecificationSetCreator;
import sase.user.stocks.specification.creators.CorrelationSelectivityEstimationSetupCreator;
import sase.user.stocks.specification.creators.DeltaSelectivityEstimationSetupCreator;
import sase.user.stocks.specification.creators.StockPatternSpecificationCreator;

public class SpecificationCreatorFactory {

	public static ISimulationSpecificationCreator createSpecificationCreator() {
		IConditionSpecificationCreator conditionCreator = 
										ConditionSpecificationCreatorFactory.createConditionSpecificationCreator();
		IConditionSpecificationSetCreator conditionSetCreator = 
										ConditionSpecificationCreatorFactory.createConditionSpecificationSetCreator();
		switch(SimulationConfig.specificationCreatorType) {
			case CORRELATION_SELECTIVITY_ESTIMATION:
				return new CorrelationSelectivityEstimationSetupCreator();
			case DELTA_SELECTIVITY_ESTIMATION:
				return new DeltaSelectivityEstimationSetupCreator();
			case CROSS_PRODUCT:
				return new CrossProductSimulationSpecificationCreator();
			case STOCK_EVALUATION:
				return new StockPatternSpecificationCreator(conditionCreator, conditionSetCreator);
			case NONE:
			default:
				return null;
		}
	}

}
