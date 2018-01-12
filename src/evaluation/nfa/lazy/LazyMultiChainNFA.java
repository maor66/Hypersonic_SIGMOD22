package evaluation.nfa.lazy;

import java.util.HashMap;

import evaluation.EvaluationPlan;
import evaluation.nfa.eager.elements.NFAState;
import pattern.Pattern;
import pattern.Pattern.PatternOperatorType;

public class LazyMultiChainNFA extends LazyNFA {

	private final HashMap<Pattern, EvaluationPlan> nestedPlans;
	protected final LazyNFANegationTypes negationType;
	
	public LazyMultiChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
		super(pattern);
		if (pattern.getType() != PatternOperatorType.OR) {
			throw new RuntimeException(String.format("Illegal pattern provided: %s", pattern));
		}
		this.negationType = negationType;
		nestedPlans = evaluationPlan.getNestedPlans();
	}

	@Override
	protected void initNFAStructure(Pattern pattern) {
		initialState = new NFAState("Initial State", true, false, false);
		states.add(initialState);
		finalState = new NFAState("Final State", false, true, true);
		states.add(finalState);
		rejectingState = new NFAState("Rejecting State", false, true, false);
		states.add(rejectingState);
		
		for (Pattern nestedPattern : nestedPlans.keySet()) {
			EvaluationPlan nestedPlan = nestedPlans.get(nestedPattern);
			LazyChainNFA nfaForNestedPattern = new LazyChainNFA(nestedPattern, nestedPlan, negationType);
			nfaForNestedPattern.initNFAStructure(nestedPattern);
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
