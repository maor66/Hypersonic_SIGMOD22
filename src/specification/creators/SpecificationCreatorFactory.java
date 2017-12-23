package sase.specification.creators;

import sase.config.SimulationConfig;
import sase.user.stocks.specification.StockSequencePatternSpecificationCreator;

public class SpecificationCreatorFactory {

	public static ISimulationSpecificationCreator createSpecificationCreator() {
		switch(SimulationConfig.specificationCreatorType) {
			case SELECTIVITY_ESTIMATION:
				return new SelectivityEstimationSetupCreator();
			case STOCK_SEQUENCES:
				return new StockSequencePatternSpecificationCreator();
			case CROSS_PRODUCT:
				return new CrossProductSimulationSpecificationCreator();
			case NONE:
			default:
				return null;
		}
	}

}
