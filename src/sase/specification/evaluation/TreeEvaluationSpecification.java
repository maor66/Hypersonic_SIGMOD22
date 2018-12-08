package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.tree.TopologyCreatorTypes;
import sase.evaluation.tree.TreeCostModelTypes;

public class TreeEvaluationSpecification extends EvaluationSpecification {

	public TopologyCreatorTypes topologyCreatorType;
	public TreeCostModelTypes treeCostModelType;
	public Double throughputToLatencyRatio;

	public TreeEvaluationSpecification(TopologyCreatorTypes topologyCreatorType,
									   TreeCostModelTypes treeCostModelType,
									   Double throughputToLatencyRatio) {
		super(EvaluationMechanismTypes.TREE);
		this.topologyCreatorType = topologyCreatorType;
		this.treeCostModelType = treeCostModelType;
		this.throughputToLatencyRatio = throughputToLatencyRatio;
	}
	
	@Override
	public String getShortDescription() {
		return String.format("%s|%s|%s|%.1f", type, topologyCreatorType, treeCostModelType, throughputToLatencyRatio);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (topology algorithm %s, cost model: %s, throughput/latency threshold: %.1f)", 
				 			 type, topologyCreatorType, treeCostModelType, throughputToLatencyRatio);
	}

}
