package sase.evaluation.tree;

import java.util.List;

import sase.base.EventType;
import sase.evaluation.tree.cost.SharingDegreeTreeCostModel;
import sase.evaluation.tree.cost.ThroughputLatencyTreeCostModel;
import sase.evaluation.tree.cost.ThroughputTreeCostModel;

public class TreeCostModelFactory {

	@SuppressWarnings("unchecked")
	public static ITreeCostModel createTreeCostModel(TreeCostModelTypes treeCostModelType, Object[] arguments) {
		switch(treeCostModelType) {
			case THROUGHPUT:
				return new ThroughputTreeCostModel();
			case THROUGHPUT_LATENCY:
				return new ThroughputLatencyTreeCostModel((List<EventType>)arguments[0], (Double)arguments[1]);
			case SHARING_DEGREE:
				return new SharingDegreeTreeCostModel();
			case NONE:
			default:
				return null;
		}
	}

}
