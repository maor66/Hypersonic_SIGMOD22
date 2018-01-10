package sase.user.stocks.specification.creators;

import java.util.Random;

import sase.config.SimulationConfig;
import sase.specification.ConditionSpecification;
import sase.specification.DoubleEventConditionSpecification;
import sase.specification.creators.condition.IConditionSpecificationCreator;
import sase.user.stocks.specification.StockCorrelationConditionSpecification;

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
