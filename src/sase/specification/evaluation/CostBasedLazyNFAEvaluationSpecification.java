package sase.specification.evaluation;

import sase.evaluation.nfa.lazy.LazyNFANegationTypes;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;

public class CostBasedLazyNFAEvaluationSpecification extends LazyNFAEvaluationSpecification {

	public OrderingAlgorithmTypes orderingAlgorithmType;
	public CostModelTypes costModelType;
	public Double throughputToLatencyRatio;
	
	public CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, 
												   CostModelTypes costModelType,
												   Double throughputToLatencyRatio,
												   LazyNFANegationTypes negationType) {
		super(negationType);
		this.orderingAlgorithmType = orderingAlgorithmType;
		this.costModelType = costModelType;
		this.throughputToLatencyRatio = throughputToLatencyRatio;
	}
	
	public CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, 
			   									   CostModelTypes costModelType,
			   									   Double throughputToLatencyRatio) {
		this(orderingAlgorithmType, costModelType, throughputToLatencyRatio, LazyNFANegationTypes.NONE);
	}
	
	@Override
	public String getShortDescription() {
		return String.format("%s|%s|%s|%.1f", type, orderingAlgorithmType, costModelType, throughputToLatencyRatio);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (ordering algorithm %s, cost model: %s, throughput/latency threshold: %.1f)", 
				 			 type, orderingAlgorithmType, costModelType, throughputToLatencyRatio);
	}

}
