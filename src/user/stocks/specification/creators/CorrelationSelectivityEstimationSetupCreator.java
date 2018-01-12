package user.stocks.specification.creators;

import java.util.ArrayList;
import java.util.List;

import order.OrderingAlgorithmTypes;
import order.cost.CostModelTypes;
import pattern.creation.PatternTypes;
import specification.ConditionSpecification;
import specification.EvaluationSpecification;
import specification.PatternSpecification;
import specification.SimulationSpecification;
import specification.creators.SelectivityEstimationSetupCreator;
import user.stocks.StockEventTypesManager;
import user.stocks.specification.StockCorrelationConditionSpecification;

public class CorrelationSelectivityEstimationSetupCreator extends SelectivityEstimationSetupCreator {

	private static final double minCorrelationLimit = 0.5;
	private static final double maxCorrelationLimit = 0.9;
	private static final int numberOfSteps = 5;
	private static final long timeWindow = 30;

	@Override
	protected String[] getEventTypeNames() {
		return StockEventTypesManager.regionEventTypeNames;
	}
	
	@Override
	protected List<SimulationSpecification> createSpecificationsForEventTypePair(String firstEventName, 
			  																	 String secondEventName) {
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		for (int k = 0; k < numberOfSteps; ++k) {
			double correlationLimit = 
					minCorrelationLimit + (maxCorrelationLimit - minCorrelationLimit) / (numberOfSteps -1) * k;
			StockCorrelationConditionSpecification currentConditionSpecification = 
					new StockCorrelationConditionSpecification(firstEventName, secondEventName, correlationLimit);
			String[][][] currentStructure = new String[][][]{ new String[][] { new String[] {
					firstEventName, secondEventName	},},};
			PatternSpecification currentPatternSpecification = 
					new PatternSpecification("", 
											 PatternTypes.STOCK_PATTERN, 
											 timeWindow, 
											 currentStructure, 
											 new ConditionSpecification[] {
													 currentConditionSpecification	 
											 });
			SimulationSpecification currentSimulationSpecification = 
					new SimulationSpecification(currentPatternSpecification,
												new EvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
																			CostModelTypes.THROUGHPUT_LATENCY,
																			0.0));
			result.add(currentSimulationSpecification);
		}
		return result;
	}

}
