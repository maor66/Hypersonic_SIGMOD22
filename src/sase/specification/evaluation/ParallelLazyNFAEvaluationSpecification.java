package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;

public class ParallelLazyNFAEvaluationSpecification extends CostBasedLazyNFAEvaluationSpecification {

	public int numOfThreads;
	// This should be between 0 and 1. The number indicates what percentage of threads will go to input threads
	public double inputMatchThreadRatio;
	
    public ParallelLazyNFAEvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType, Double throughputToLatencyRatio, 
    		int numOfThreads, double inputMatchThreadRatio) {
        super(orderingAlgorithmType, costModelType, throughputToLatencyRatio);
        this.type = EvaluationMechanismTypes.LAZY_CHAIN_PARALLEL;
        this.numOfThreads = numOfThreads;
        this.inputMatchThreadRatio = inputMatchThreadRatio;
    }
}
