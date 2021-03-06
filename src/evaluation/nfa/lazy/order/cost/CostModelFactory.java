package sase.evaluation.nfa.lazy.order.cost;

import java.util.List;

import sase.base.EventType;

public class CostModelFactory {

	@SuppressWarnings("unchecked")
	public static ICostModel createCostModel(CostModelTypes costModelType, Object[] arguments) {
		switch(costModelType) {
			case THROUGHPUT:
				return new ThroughputCostModel();
			case THROUGHPUT_LATENCY:
				return new ThroughputLatencyCostModel((List<EventType>)arguments[0], (Double)arguments[1]);
			case SHARING_DEGREE:
				return new SharingDegreeCostModel();
			case NONE:
				return null;
			default:
				throw new RuntimeException(String.format("Unexpected cost model type: %s", costModelType));
		}
	}

}
