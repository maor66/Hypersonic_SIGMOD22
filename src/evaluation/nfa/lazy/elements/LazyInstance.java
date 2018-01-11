package sase.evaluation.nfa.lazy.elements;

import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.config.MainConfig;
import sase.evaluation.common.EventBuffer;
import sase.evaluation.nfa.NFA;
import sase.evaluation.nfa.eager.elements.Instance;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.lazy.LazyNFA;
import sase.evaluation.nfa.lazy.elements.EfficientInputBuffer;

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
	
	private boolean isRegularTransitionPossible(Event event, LazyTransition transition) {
		if (MainConfig.selectionStrategy == EventSelectionStrategies.CONTUGUITY) {
			LazyNFA lazyNfa = (LazyNFA)automaton;
			Event precedingEvent = transition.getActualPrecedingEvent(getEventsFromMatchBuffer());
			if (precedingEvent != null && !lazyNfa.verifyContiguityConditions(event, precedingEvent)) {
				return false;
			}
			Event succeedingEvent = transition.getActualSucceedingEvent(getEventsFromMatchBuffer());
			if (succeedingEvent != null && !lazyNfa.verifyContiguityConditions(event, succeedingEvent)) {
				return false;
			}
		}
		return super.isTransitionPossible(event, transition);
	}
	
	@Override
	public boolean isTransitionPossible(Event event, Transition transition) {
		LazyTransition lazyTransition = (LazyTransition)transition;
		switch(lazyTransition.getType()) {
			case REGULAR:
				return isRegularTransitionPossible(event, lazyTransition);
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
