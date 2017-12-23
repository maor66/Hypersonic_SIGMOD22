package evaluation.tree;

import java.util.List;

import base.EventType;
import evaluation.tree.cost.ThroughputLatencyTreeCostModel;
import evaluation.tree.cost.ThroughputTreeCostModel;

public class TreeCostModelFactory {

	@SuppressWarnings("unchecked")
	public static ITreeCostModel createTreeCostModel(TreeCostModelTypes treeCostModelType, Object[] arguments) {
		switch(treeCostModelType) {
			case THROUGHPUT:
				return new ThroughputTreeCostModel();
			case THROUGHPUT_LATENCY:
				return new ThroughputLatencyTreeCostModel((List<EventType>)arguments[0], (Double)arguments[1]);
			case NONE:
			default:
				return null;
		}
	}

}
