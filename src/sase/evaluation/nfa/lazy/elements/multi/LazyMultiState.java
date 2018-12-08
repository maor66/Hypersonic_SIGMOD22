package sase.evaluation.nfa.lazy.elements.multi;

import sase.evaluation.nfa.eager.elements.NFAState;

public class LazyMultiState extends NFAState {

	private Long timeWindow;
	
	public LazyMultiState(String name, boolean isInitial, boolean isFinal, boolean isAccepting, Long timeWindow) {
		super(name, isInitial, isFinal, isAccepting);
		this.timeWindow = timeWindow;
	}
	
	public LazyMultiState(String name, Long timeWindow) {
		this(name, false, false, false, timeWindow);
	}

	public Long getTimeWindow() {
		return timeWindow;
	}
	

}
