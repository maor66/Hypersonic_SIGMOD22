package evaluation.nfa.eager;

import evaluation.nfa.eager.elements.NFAState;
import pattern.Pattern;
import pattern.Pattern.PatternOperatorType;

public class OR_AND_SEQ_NFA extends AND_SEQ_NFA {

	public OR_AND_SEQ_NFA(Pattern pattern) {
		super(pattern);
		if (this.pattern.getType() != PatternOperatorType.OR) {
			throw new RuntimeException(String.format("Illegal pattern provided: %s", pattern));
		}
	}
	
	@Override
	protected void initNFAStructure(Pattern pattern) {
		initialState = new NFAState("Initial State", true, false, false);
		states.add(initialState);
		finalState = new NFAState("Final State", false, true, true);
		states.add(finalState);
		
		for (Pattern nestedPattern : this.pattern.getNestedPatterns()) {
			AND_SEQ_NFA nfaForNestedPattern = new AND_SEQ_NFA(nestedPattern);
			nfaForNestedPattern.initNFAStructure(nestedPattern);
			appendNFA(nfaForNestedPattern);
		}
	}

}
