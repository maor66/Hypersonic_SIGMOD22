package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;

public class ParallelLazyNFAStateDynamicEvaluationSpecification extends ParallelLazyNFAEvaluationSpecification {
    public ParallelLazyNFAStateDynamicEvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType, Double throughputToLatencyRatio, int numOfThreads, double inputMatchThreadRatio) {
        super(orderingAlgorithmType, costModelType, throughputToLatencyRatio, numOfThreads, inputMatchThreadRatio);
        this.type = EvaluationMechanismTypes.LAZY_CHAIN_PARALLEL_STATE_DYNAMIC;
    }

}
