package sase.specification.evaluation;

import sase.config.MainConfig;
import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;

public class ParallelSplitDuplicateNFAEvaluationSpecification extends ParallelLazyNFAEvaluationSpecification{

    public ParallelSplitDuplicateNFAEvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType, Double throughputToLatencyRatio, int numOfThreads, double inputMatchThreadRatio) {
        super(orderingAlgorithmType, costModelType, throughputToLatencyRatio, numOfThreads, inputMatchThreadRatio);
        this.type = EvaluationMechanismTypes.LAZY_CHAIN_PARALLEL_WITH_SPLIT_AND_DUPLICATE;
    }
}

