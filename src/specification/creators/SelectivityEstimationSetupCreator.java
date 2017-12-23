package specification.creators;

import java.util.ArrayList;
import java.util.List;

import order.OrderingAlgorithmTypes;
import order.cost.CostModelTypes;
import pattern.creation.PatternTypes;
import specification.ConditionSpecification;
import specification.EvaluationSpecification;
import specification.PatternSpecification;
import specification.SimulationSpecification;
import user.stocks.StockEventTypesManager;
import user.stocks.specification.StockCorrelationConditionSpecification;

public class SelectivityEstimationSetupCreator implements ISimulationSpecificationCreator {

	private static final double minCorrelationLimit = 0.5;
	private static final double maxCorrelationLimit = 0.9;
	private static final int numberOfSteps = 5;
	private static final long timeWindow = 30;
	
	@Override
	public SimulationSpecification[] createSpecifications() {
		String[] eventTypeNames = StockEventTypesManager.regionEventTypeNames;
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		for (int i = 0; i < eventTypeNames.length; ++i) {
			for (int j = i + 1; j < eventTypeNames.length; ++j) {
				for (int k = 0; k < numberOfSteps; ++k) {
					String firstEventTypeName = eventTypeNames[i];
					String secondEventTypeName = eventTypeNames[j];
					double correlationLimit = 
							minCorrelationLimit + (maxCorrelationLimit - minCorrelationLimit) / (numberOfSteps -1) * k;
					StockCorrelationConditionSpecification currentConditionSpecification = 
							new StockCorrelationConditionSpecification(firstEventTypeName, secondEventTypeName, correlationLimit);
					String[][][] currentStructure = new String[][][]{ new String[][] { new String[] {
							firstEventTypeName, secondEventTypeName
							},},};
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
			}
		}
		return result.toArray(new SimulationSpecification[0]);
	}

}
