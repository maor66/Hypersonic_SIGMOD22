package sase.specification;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.LazyNFANegationTypes;
import sase.evaluation.tree.TopologyCreatorTypes;
import sase.evaluation.tree.TreeCostModelTypes;
import sase.order.OrderingAlgorithmTypes;
import sase.order.cost.CostModelTypes;
import sase.pattern.EventTypesManager;

public class EvaluationSpecification {

	public EvaluationMechanismTypes type;
	public LazyNFANegationTypes negationType;
	public OrderingAlgorithmTypes orderingAlgorithmType;
	public CostModelTypes costModelType;
	public TopologyCreatorTypes topologyCreatorType;
	public TreeCostModelTypes treeCostModelType;
	public String[] evaluationOrder;
	public Double throughputToLatencyRatio;
	
	public EvaluationSpecification(EvaluationMechanismTypes type, LazyNFANegationTypes negationType, 
							OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType,
							String[] evaluationOrder, Double throughputToLatencyRatio, 
							TopologyCreatorTypes topologyCreatorType, TreeCostModelTypes treeCostModelType) {
		this.type = type;
		this.negationType = negationType;
		this.orderingAlgorithmType = orderingAlgorithmType;
		this.costModelType = costModelType;
		this.evaluationOrder = evaluationOrder;
		this.topologyCreatorType = topologyCreatorType;
		this.treeCostModelType = treeCostModelType;
		this.throughputToLatencyRatio = throughputToLatencyRatio;
	}
	
	public EvaluationSpecification(EvaluationMechanismTypes type, LazyNFANegationTypes negationType, 
			OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType,
			String[] evaluationOrder, Double throughputToLatencyRatio) {
		this(type, negationType, orderingAlgorithmType, costModelType, evaluationOrder, throughputToLatencyRatio,
			 TopologyCreatorTypes.NONE, TreeCostModelTypes.NONE);
	}
	
	public EvaluationSpecification(EvaluationMechanismTypes type, String[] evaluationOrder) {
		this(type, LazyNFANegationTypes.NONE, OrderingAlgorithmTypes.FIXED, CostModelTypes.NONE, evaluationOrder, 0.0);
	}
	
	public EvaluationSpecification(EvaluationMechanismTypes type) {
		this(type, LazyNFANegationTypes.NONE, OrderingAlgorithmTypes.TRIVIAL, CostModelTypes.NONE, 0.0);
	}
	
	public EvaluationSpecification(EvaluationMechanismTypes type, LazyNFANegationTypes negationType, 
							OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType,
							Double throughputToLatencyRatio) {
		this(type, negationType, orderingAlgorithmType, costModelType, null, throughputToLatencyRatio);
	}
	
	public EvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType,
								   Double throughputToLatencyRatio) {
		this(EvaluationMechanismTypes.LAZY_CHAIN, LazyNFANegationTypes.NONE, 
			 orderingAlgorithmType, costModelType, null, throughputToLatencyRatio);
	}
	
	public EvaluationSpecification(TopologyCreatorTypes topologyCreatorType, TreeCostModelTypes treeCostModelType,
								   Double throughputToLatencyRatio) {
		this(EvaluationMechanismTypes.TREE, LazyNFANegationTypes.NONE, OrderingAlgorithmTypes.TRIVIAL, CostModelTypes.NONE, 
			 null, throughputToLatencyRatio, topologyCreatorType, treeCostModelType);
	}

	private String getShortEvaluationOrder() {
		if (evaluationOrder == null)
			return "";
		String result = "";
		for (int i = 0; i < evaluationOrder.length; ++i) {
			String currentIdentifier = EventTypesManager.getInstance().getShortNameByLongName(evaluationOrder[i]);
			result += currentIdentifier;
			if (i < evaluationOrder.length - 1) {
				result += ",";
			}
		}
		return result;
	}
	
	public String getShortDescription() {
		switch (type) {
			case EAGER:
				return type.toString();
			case LAZY_CHAIN:
				switch (orderingAlgorithmType) {
					case FIXED:
						return String.format("%s|%s|%s", type, getShortEvaluationOrder(), negationType);
					default:
						return String.format("%s|%s|%s|%.1f", type, 
											 orderingAlgorithmType, costModelType, throughputToLatencyRatio);					
				}
			case TREE:
				return String.format("%s|%s|%s|%.1f", type, topologyCreatorType, treeCostModelType, throughputToLatencyRatio);
			case LAZY_TREE:
			default:
				return "";
		}
	}
	
	private String evaluationOrderAsString() {
		if (evaluationOrder == null)
			return "";
		String result = "";
		for (int i = 0; i < evaluationOrder.length; ++i) {
			result += evaluationOrder[i];
			if (i < evaluationOrder.length - 1) {
				result += ",";
			}
		}
		return result;
	}
	
	public String getLongDescription() {
		if (type == EvaluationMechanismTypes.TREE) {
			return String.format("%s (topology algorithm %s, cost model: %s, throughput/latency threshold: %.1f)", 
								 type, topologyCreatorType, treeCostModelType, throughputToLatencyRatio);
		}
		if (evaluationOrder == null) {
			return String.format("%s (ordering algorithm %s, cost model: %s, throughput/latency threshold: %.1f)", 
								 type, orderingAlgorithmType, costModelType, throughputToLatencyRatio);
		}
		return String.format("%s (fixed order (%s), negation support: %s)", type, evaluationOrderAsString(), negationType);
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}
}
