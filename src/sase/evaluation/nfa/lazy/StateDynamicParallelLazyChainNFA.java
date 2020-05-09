package sase.evaluation.nfa.lazy;

import sase.base.ContainsEvent;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.parallel.ParallelQueue;
import sase.evaluation.nfa.parallel.WorkerGroup;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateDynamicParallelLazyChainNFA extends ParallelLazyChainNFA {
    public StateDynamicParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, ParallelLazyNFAEvaluationSpecification specification) {
        super(pattern, evaluationPlan, specification);
    }

    @Override
    protected Map<ParallelQueue<? extends ContainsEvent>,Map.Entry<TypedNFAState,WorkerGroup>> getInputReleventForState(
            Map<ParallelQueue<? extends ContainsEvent>, Map.Entry<TypedNFAState, WorkerGroup>> inputsToTypeAndGroup,
            TypedNFAState state)
    {
        Map<ParallelQueue<? extends ContainsEvent>, Map.Entry<TypedNFAState, WorkerGroup>> inputsOfOnlyOwnState = new HashMap<>();
        inputsToTypeAndGroup.forEach((parallelQueue, typedNFAStateWorkerGroupEntry) -> {
            if (typedNFAStateWorkerGroupEntry.getKey().getEventType() == state.getEventType()) {
                inputsOfOnlyOwnState.put(parallelQueue, typedNFAStateWorkerGroupEntry);
            }
        });
        return inputsOfOnlyOwnState;
    }

    @Override
    protected List<TypedNFAState> getStatesReleventForBufferWorkerStorage(TypedNFAState ownState)
    {
        return List.of(ownState);
    }
}