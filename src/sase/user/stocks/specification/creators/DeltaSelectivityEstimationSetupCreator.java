package sase.user.stocks.specification.creators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sase.config.SimulationConfig;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;
import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.creation.PatternTypes;
import sase.specification.SimulationSpecification;
import sase.specification.condition.ConditionSpecification;
import sase.specification.creators.SelectivityEstimationSetupCreator;
import sase.specification.evaluation.CostBasedLazyNFAEvaluationSpecification;
import sase.specification.workload.PatternSpecification;
import sase.specification.workload.SinglePatternWorkloadSpecification;
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
										 }, SlaVerifierTypes.NONE);
		SimulationSpecification simulationSpecification = 
				new SimulationSpecification(new SinglePatternWorkloadSpecification(patternSpecification),
					new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
																CostModelTypes.THROUGHPUT_LATENCY,
																0.0));
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		result.add(simulationSpecification);
		return result;
	}

}
