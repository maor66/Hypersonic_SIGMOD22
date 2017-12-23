package evaluation.nfa.lazy;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import evaluation.EvaluationMechanismTypes;
import evaluation.EvaluationPlan;
import evaluation.nfa.eager.elements.NFAState;
import pattern.CompositePattern;
import pattern.Pattern;
import pattern.Pattern.PatternOperatorType;

public class LazyMultiChainNFA extends LazyChainNFA {

	private final CompositePattern pattern;
	
	public LazyMultiChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
		super(pattern, evaluationPlan, negationType);
		this.pattern = (CompositePattern)pattern;
		if (this.pattern.getType() != PatternOperatorType.OR) {
			throw new RuntimeException(String.format("Illegal pattern provided: %s", pattern));
		}
	}
	
	private List<EventType> getEvaluationOrderForSubPattern(Pattern pattern) {
		List<EventType> result = new ArrayList<EventType>();
		List<EventType> validEventsForSubPattern = pattern.getEventTypes();
		List<EventType> fullEvaluationOrder = evaluationOrder.getFullEvaluationOrder();
		for (EventType eventType : fullEvaluationOrder) {
			if (validEventsForSubPattern.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}

	@Override
	protected void initNFAStructure(Pattern pattern) {
		initialState = new NFAState("Initial State", true, false, false);
		states.add(initialState);
		finalState = new NFAState("Final State", false, true, true);
		states.add(finalState);
		rejectingState = new NFAState("Rejecting State", false, true, false);
		states.add(rejectingState);
		
		for (Pattern nestedPattern : this.pattern.getNestedPatterns()) {
			List<EventType> evaluationOrderForNestedPattern = getEvaluationOrderForSubPattern(nestedPattern);
			EvaluationPlan nestedEvaluationPlan = new EvaluationPlan(EvaluationMechanismTypes.LAZY_CHAIN,
																	 evaluationOrderForNestedPattern);
			LazyChainNFA nfaForNestedPattern = new LazyChainNFA(nestedPattern, nestedEvaluationPlan, negationType);
			nfaForNestedPattern.initNFAStructure(nestedPattern);
			appendNFA(nfaForNestedPattern);
		}
	}

}
