package evaluation.nfa.lazy.elements;

import base.Event;
import evaluation.common.EventBuffer;
import evaluation.nfa.NFA;
import evaluation.nfa.eager.elements.Instance;
import evaluation.nfa.eager.elements.NFAState;
import evaluation.nfa.eager.elements.Transition;
import evaluation.nfa.lazy.LazyNFA;

public class LazyInstance extends Instance {

	private boolean shouldStoreCurrentEvent;
	private boolean shouldStopEvaluation;
	
	private final EfficientInputBuffer lastAggregatedEvents;
	
	public LazyInstance(LazyNFA nfa, NFAState initialState) {
		this(nfa, initialState, null);
	}
	
	public LazyInstance(LazyNFA nfa, NFAState initialState, EventBuffer eventBuffer) {
		super(nfa, initialState, eventBuffer);
		shouldStoreCurrentEvent = false;
		shouldStopEvaluation = false;
		lastAggregatedEvents = nfa.shouldActivateUnboundedIterativeMode() ? 
									null : new EfficientInputBuffer(null, nfa.getTimeWindow());
	}
	
	public boolean getAndResetShouldStoreCurrentEvent() {
		boolean prevShouldStoreCurrentEvent = shouldStoreCurrentEvent;
		shouldStoreCurrentEvent = false;
		return prevShouldStoreCurrentEvent;
	}
	
	public boolean getAndResetShouldStopEvaluation() {
		boolean prevShouldStopEvaluation = shouldStopEvaluation;
		shouldStopEvaluation = false;
		return prevShouldStopEvaluation;
	}
	
	public boolean forceTimeout() {
		for (Transition transition : currentState.getOutgoingTransitions()) {
			LazyTransition lazyTransition = (LazyTransition)transition;
			if (lazyTransition.getType() != LazyTransitionType.TIMEOUT) {
				continue;
			}
			executeTransition(null, lazyTransition);
			return true;
		}
		return false;
	}
	
	private void executeRegularTransition(Event event, Transition transition) {
		if (event == null || event.getType().getName().equals(NFA.inputBufferEventTypeName))
			return;
		switch (transition.getAction()) {
			case TAKE:
			case ITERATE:
				executeMatchBufferTransition(event);
				break;
			case STORE:
				shouldStoreCurrentEvent = true;
				break;
			case IGNORE:
			default:
				break;
		}
		if (currentState.isRejecting()) {
			shouldStopEvaluation = true;
		}
	}
	
	private void executeSearchFailedTransition(Event event, Transition transition) {
	}
	
	private void executeTimeoutTransition(Event event, Transition transition) {
		shouldStopEvaluation = true;
	}

	public EfficientInputBuffer getLastAggregatedEvents() {
		return lastAggregatedEvents;
	}

	@Override
	public void executeTransition(Event event, Transition transition) {
		currentState = transition.getDestination();
		switch(((LazyTransition)transition).getType()) {
			case REGULAR:
				executeRegularTransition(event, transition);
				break;
			case SEARCH_FAILED:
				executeSearchFailedTransition(event, transition);
				break;
			case TIMEOUT:
				executeTimeoutTransition(event, transition);
				break;
			default:
				break;
		}
	}
	
	@Override
	public boolean isTransitionPossible(Event event, Transition transition) {
		switch(((LazyTransition)transition).getType()) {
			case REGULAR:
				return super.isTransitionPossible(event, transition);
			case SEARCH_FAILED:
				return true; //we assume this type of edge to only be traversed when all conditions hold
			case TIMEOUT:
				return isExpired();
			default:
				return false;
		}
	}
	
	@Override
	public LazyInstance clone() {
		return new LazyInstance((LazyNFA)automaton, currentState, matchBuffer);
	}
}
