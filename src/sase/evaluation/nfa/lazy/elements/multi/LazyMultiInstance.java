package sase.evaluation.nfa.lazy.elements.multi;

import sase.evaluation.common.EventBuffer;
import sase.evaluation.nfa.lazy.LazyMultiPatternTreeNFA;
import sase.evaluation.nfa.lazy.elements.LazyInstance;

public class LazyMultiInstance extends LazyInstance {

	private boolean isMatchReported = false;
	
	public LazyMultiInstance(LazyMultiPatternTreeNFA nfa, LazyMultiState initialState) {
		super(nfa, initialState);
	}
	
	protected LazyMultiInstance(LazyMultiPatternTreeNFA nfa, LazyMultiState initialState, EventBuffer eventBuffer) {
		super(nfa, initialState, eventBuffer);
	}
	
	protected Long getInstanceTimeWindow() {
		return ((LazyMultiState)currentState).getTimeWindow();
	}
	
	@Override
	public LazyMultiInstance clone() {
		return new LazyMultiInstance((LazyMultiPatternTreeNFA)automaton, (LazyMultiState)currentState, matchBuffer);
	}
	
	@Override
	public boolean shouldDiscardWithMatch() {
		return currentState.getOutgoingTransitions().isEmpty();
	}
	
	@Override
	public boolean shouldReportMatch() {
		boolean oldIsMatchReported = isMatchReported;
		isMatchReported = true;
		return !oldIsMatchReported;
	}

}
