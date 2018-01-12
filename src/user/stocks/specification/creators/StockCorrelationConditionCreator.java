package user.stocks.specification.creators;

import java.util.Random;

import config.SimulationConfig;
import specification.ConditionSpecification;
import specification.DoubleEventConditionSpecification;
import specification.creators.condition.IConditionSpecificationCreator;
import user.stocks.specification.StockCorrelationConditionSpecification;

public class StockCorrelationConditionCreator implements IConditionSpecificationCreator {
	
	public DoubleEventConditionSpecification createDoubleEventCondition(String firstTypeName, String secondTypeName) {
		double correlationLimit = SimulationConfig.correlations[new Random().nextInt(SimulationConfig.correlations.length)];
		return new StockCorrelationConditionSpecification(firstTypeName, secondTypeName, correlationLimit);
	}

	public ConditionSpecification createIteratedEventCondition(String precedingTypeName,
																  String iteratedTypeName, 
																  String succeedingTypeName) {
		//TODO: unsupported as of now
		return null;
	}
}
