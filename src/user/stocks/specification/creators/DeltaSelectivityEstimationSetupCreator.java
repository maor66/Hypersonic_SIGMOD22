package sase.user.stocks.specification.creators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sase.config.SimulationConfig;
import sase.order.OrderingAlgorithmTypes;
import sase.order.cost.CostModelTypes;
import sase.pattern.creation.PatternTypes;
import sase.specification.ConditionSpecification;
import sase.specification.EvaluationSpecification;
import sase.specification.PatternSpecification;
import sase.specification.SimulationSpecification;
import sase.specification.creators.SelectivityEstimationSetupCreator;
import sase.user.stocks.StockEventTypesManager;
import sase.user.stocks.specification.StockDeltaOrderingConditionSpecification;

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
