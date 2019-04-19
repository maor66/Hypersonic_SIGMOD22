package sase.evaluation.data_parallel;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.LazyChainNFA;
import sase.evaluation.nfa.lazy.LazyNFANegationTypes;
import sase.evaluation.nfa.lazy.ParallelLazyChainNFA;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.specification.evaluation.ParallelDummyEvaluationSpecification;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;

// Dummy class for testing Maor's algorithm compared to trivial solution.
// One input and one match thread will be created for each state
public class ParallelDummyEvaluationMechanism extends ParallelLazyChainNFA {

	public ParallelDummyEvaluationMechanism(Pattern pattern, EvaluationPlan evaluationPlan,
			ParallelDummyEvaluationSpecification specification) {
		super(pattern, evaluationPlan, new ParallelLazyNFAEvaluationSpecification(specification.orderingAlgorithmType,
				specification.costModelType,
				specification.throughputToLatencyRatio, 0, null));
	}
	
	@Override
	public void completeCreation(List<Pattern> patterns) {
		// Hack to do completeCreation of parent of parent
		try {
			super.completeCreation(patterns);
		} catch(RuntimeException e) {
			// do nothing...
		}
		initallizeThreadAllocation();
    }
	
	@Override
	public void initallizeThreadAllocation() {
    	List<TypedNFAState> nfaStates = getWorkerStates();
    	int listIndex = 0;
        for (TypedNFAState state : nfaStates) {
            stateToIBThreads.put(state, 1);
            stateToMBThreads.put(state, 1);
        }
        initializeThreads();
	}
}
