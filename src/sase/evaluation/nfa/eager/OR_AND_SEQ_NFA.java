package sase.evaluation.nfa.eager;

import sase.evaluation.nfa.eager.elements.NFAState;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;

public class OR_AND_SEQ_NFA extends AND_SEQ_NFA {

	public OR_AND_SEQ_NFA(Pattern pattern) {
		super(pattern);
		if (this.pattern.getType() != PatternOperatorTypes.OR) {
			throw new RuntimeException(String.format("Illegal sase.pattern provided: %s", pattern));
		}
	}
	
	@Override
	protected void initNFAStructure() {
		initialState = new NFAState("Initial State", true, false, false);
		states.add(initialState);
		finalState = new NFAState("Final State", false, true, true);
		states.add(finalState);
		
		for (Pattern nestedPattern : this.pattern.getNestedPatterns()) {
			AND_SEQ_NFA nfaForNestedPattern = new AND_SEQ_NFA(nestedPattern);
			nfaForNestedPattern.initNFAStructure();
			appendNFA(nfaForNestedPattern);
		}
	}

}
