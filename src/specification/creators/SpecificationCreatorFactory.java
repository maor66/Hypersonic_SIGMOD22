package specification.creators;

import config.SimulationConfig;
import specification.creators.condition.ConditionSpecificationCreatorFactory;
import specification.creators.condition.IConditionSpecificationCreator;
import specification.creators.condition.IConditionSpecificationSetCreator;
import user.stocks.specification.creators.CorrelationSelectivityEstimationSetupCreator;
import user.stocks.specification.creators.DeltaSelectivityEstimationSetupCreator;
import user.stocks.specification.creators.StockPatternSpecificationCreator;

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
