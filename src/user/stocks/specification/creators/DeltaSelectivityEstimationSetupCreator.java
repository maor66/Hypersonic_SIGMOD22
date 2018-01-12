package user.stocks.specification.creators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import config.SimulationConfig;
import order.OrderingAlgorithmTypes;
import order.cost.CostModelTypes;
import pattern.creation.PatternTypes;
import specification.ConditionSpecification;
import specification.EvaluationSpecification;
import specification.PatternSpecification;
import specification.SimulationSpecification;
import specification.creators.SelectivityEstimationSetupCreator;
import user.stocks.StockEventTypesManager;
import user.stocks.specification.StockDeltaOrderingConditionSpecification;

public class DeltaSelectivityEstimationSetupCreator extends SelectivityEstimationSetupCreator {

	@Override
	protected String[] getEventTypeNames() {
		List<String> result = new ArrayList<String>(Arrays.asList(StockEventTypesManager.largeCompaniesEventTypeNames));
		result.addAll(Arrays.asList(StockEventTypesManager.mediumCompaniesEventTypeNames));
		result.addAll(Arrays.asList(StockEventTypesManager.smallCompaniesEventTypeNames));
		return result.toArray(new String[0]);
	}

	@Override
	protected List<SimulationSpecification> createSpecificationsForEventTypePair(String firstEventName,
																				 String secondEventName) {
		StockDeltaOrderingConditionSpecification conditionSpecification = 
				new StockDeltaOrderingConditionSpecification(firstEventName, secondEventName);
		String[][][] structure = new String[][][]{ new String[][] { new String[] { firstEventName },
																	new String[] { secondEventName }},};
		PatternSpecification patternSpecification = 
				new PatternSpecification("", 
										 PatternTypes.STOCK_PATTERN, 
										 SimulationConfig.timeWindowForPatternGenerator, 
										 structure, 
										 new ConditionSpecification[] {
												 conditionSpecification	 
										 });
		SimulationSpecification simulationSpecification = 
				new SimulationSpecification(patternSpecification,
											new EvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
																		CostModelTypes.THROUGHPUT_LATENCY,
																		0.0));
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		result.add(simulationSpecification);
		return result;
	}

}
