package sase.evaluation.nfa.lazy;

import java.util.HashMap;

import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.DisjunctionEvaluationPlan;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;

public class LazyMultiChainNFA extends LazyNFA {

	private final HashMap<Pattern, EvaluationPlan> nestedPlans;
	protected final LazyNFANegationTypes negationType;
	
	public LazyMultiChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
		super(pattern);
		if (pattern.getType() != PatternOperatorTypes.OR) {
			throw new RuntimeException(String.format("Illegal sase.pattern provided: %s", pattern));
		}
		this.negationType = negationType;
		nestedPlans = ((DisjunctionEvaluationPlan)evaluationPlan).getRepresentation();
	}

	@Override
	protected void initNFAStructure() {
		initialState = new NFAState("Initial State", true, false, false);
		states.add(initialState);
		finalState = new NFAState("Final State", false, true, true);
		states.add(finalState);
		rejectingState = new NFAState("Rejecting State", false, true, false);
		states.add(rejectingState);
		
		for (Pattern nestedPattern : nestedPlans.keySet()) {
			EvaluationPlan nestedPlan = nestedPlans.get(nestedPattern);
			LazyChainNFA nfaForNestedPattern = new LazyChainNFA(nestedPattern, nestedPlan, negationType);
			nfaForNestedPattern.initNFAStructure();
			appendNFA(nfaForNestedPattern);
		}
	}

	@Override
	public String getStructureSummary() {
		String result = "Disjunction of Chain NFA:\n";
		for (EvaluationPlan plan : nestedPlans.values()) {
			result += String.format("%s\n", plan);
		}
		return result;
	}

}
