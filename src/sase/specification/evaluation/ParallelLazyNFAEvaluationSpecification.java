package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.LazyNFANegationTypes;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;

public class ParallelLazyNFAEvaluationSpecification extends CostBasedLazyNFAEvaluationSpecification {

    public ParallelLazyNFAEvaluationSpecification(OrderingAlgorithmTypes orderingAlgorithmType, CostModelTypes costModelType, Double throughputToLatencyRatio) {
        super(orderingAlgorithmType, costModelType, throughputToLatencyRatio);
        this.type = EvaluationMechanismTypes.LAZY_CHAIN_PARALLEL;
    }
}
