package sase.evaluation.nfa.lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.NFA;
import sase.evaluation.nfa.eager.elements.Instance;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.lazy.elements.EfficientInputBuffer;
import sase.evaluation.nfa.lazy.elements.LazyInstance;
import sase.evaluation.nfa.lazy.elements.LazyTransition;
import sase.evaluation.nfa.lazy.elements.LazyTransitionType;
import sase.evaluation.nfa.lazy.optimizations.BufferPreprocessor;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.TrivialCondition;
import sase.pattern.condition.contiguity.PartialContiguityCondition;
import sase.pattern.condition.time.EventTemporalPositionCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public abstract class LazyNFA extends NFA {

	protected NFAState rejectingState = null;
	protected final EfficientInputBuffer inputBuffer;
	protected final List<EventType> unboundedIterativeEventTypes;
	protected PartialContiguityCondition contiguityVerifier = null;

	public LazyNFA(Pattern pattern) {
		super(pattern);
		inputBuffer = new EfficientInputBuffer(pattern);
		unboundedIterativeEventTypes = getUnboundedIterativeEventTypes();
		//NOTE: will not work in the multi-pattern setting
		if (MainConfig.selectionStrategy == EventSelectionStrategies.CONTUGUITY && 
			pattern.getType() == PatternOperatorTypes.SEQ) {
			contiguityVerifier = new PartialContiguityCondition(pattern.getEventTypes());
		}
	}
	
	public NFAState getRejectingState() {
		return rejectingState;
	}
	
	public boolean verifyContiguityConditions(Event firstEvent, Event secondEvent) {
		//Maor: probably checks if the event that trying to add fits in the correct order. Maybe should call this when a event arrives and is compared to all partial matches.
		if (contiguityVerifier == null) {
			return true;
		}
		return contiguityVerifier.verify(Arrays.asList(new Event[] {firstEvent, secondEvent}));
	}

	private List<List<EventType>> getSequencesWithBoundEventType(EventType eventType) {
		List<List<EventType>> result = new ArrayList<List<EventType>>();
		for (List<EventType> sequence : sequences) {
			int indexOfEventType = sequence.indexOf(eventType);
			if (indexOfEventType != -1 && indexOfEventType < sequence.size() - 1) {
				result.add(sequence);
			}
		}
		return result;
	}
	
	private List<EventType> getUnboundedIterativeEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		for (EventType eventType : iterativeTypes) {
			if (getSequencesWithBoundEventType(eventType).isEmpty()) {
				result.add(eventType);
			}
		}
		return result;
	}

	@Override
	protected Instance createInstance() {
		return new LazyInstance(this, initialState);
	}

	public void validateTimeWindowForState(long currentTime, Event event) {
		if (!(supportedEventTypes.contains(event.getType()))) {
			// irrelevant event
			return;
		}
		instances.validateTimeWindow(currentTime, false);
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime, Event event) {
		if (!(supportedEventTypes.contains(event.getType()))) {
			// irrelevant event
			return null;
		}
		lastKnownGlobalTime = currentTime;
		instances.validateTimeWindow(currentTime, true);
		inputBuffer.refresh(currentTime);
		//TODO: very bad programming + may (and will) skip matches in negation patterns
		return new ArrayList<Match>();
	}

	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		if (!(supportedEventTypes.contains(event.getType()))) {
			// irrelevant event
			return null;
		}
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.events);
		List<Instance> instancesToAdd = new ArrayList<Instance>();
		List<Instance> instancesToRemove = new ArrayList<Instance>();
		List<Match> matches = new LinkedList<Match>();
		List<List<Instance>> instancesToCheck = instances.getInstancesForEvent(event, canStartInstance);
		if (instancesToCheck == null) {
			return null;
		}
		if (shouldActivateUnboundedIterativeMode()) {
			performInstanceLoopWithUnboundedIterativeEvents(event, instancesToCheck, instancesToAdd, 
					   										instancesToRemove, matches);
		}
		else {
			performRegularInstanceLoop(event, instancesToCheck, instancesToAdd, instancesToRemove, matches);
		}
		getPendingMatches(matches, instancesToRemove, false);


		handleInstanceStorageUpdate(instancesToCheck, instancesToAdd, instancesToRemove);
		if (instances.shouldStoreEvent(event)) {
			inputBuffer.store(event);
			Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.bufferInsertions);
		}
		Environment.getEnvironment().getStatisticsManager().updateDiscreteIfBigger(Statistics.peakInstances,
																				   instances.getInstancesNumber());
		Environment.getEnvironment().getStatisticsManager().updateDiscreteIfBigger(Statistics.peakBufferedEvents,
																				   inputBuffer.numberOfEvents());
		if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY) {
			retainFirstElementOnly(matches);
		}
		return matches.size() > 0 ? matches : null;
	}
	
	private void performRegularInstanceLoop(Event event, List<List<Instance>> instancesToCheck,
											List<Instance> instancesToAdd, List<Instance> instancesToRemove,
											List<Match> matches) {
		Long startTimestamp = System.currentTimeMillis();
		for (List<Instance> instanceList : instancesToCheck) {
			boolean shouldStop = false;
			List<Instance> instancesToRelocate = new ArrayList<Instance>();
			NFAState currentState = instanceList.get(0).getCurrentState();
			for (Instance instance : instanceList) {
				if (Environment.getEnvironment().isTimeoutReached(System.currentTimeMillis() - startTimestamp)) {
					shouldStop = true;
					break;
				}
				performSingleInstanceLoopIteration(event, instance, instancesToAdd, instancesToRemove);
				if (currentState != instance.getCurrentState()) {
					instancesToRelocate.add(instance);
				}
				if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY && !instancesToAdd.isEmpty()) {
					retainFirstElementOnly(instancesToAdd);
					shouldStop = true;
					break;
				}
			}
			relocateInstances(instanceList, instancesToRelocate);
			if (shouldStop) {
				break;
			}
		}
	}
	
	private void relocateInstances(List<Instance> listFromStorage, List<Instance> instancesToRelocate) {
		listFromStorage.removeAll(instancesToRelocate);
		for (Instance instance : instancesToRelocate) {
			instances.add(instance);
		}
		
	}
	
	private void performInstanceLoopWithUnboundedIterativeEvents(Event event,
																 List<List<Instance>> instancesToCheck,
																 List<Instance> instancesToAdd, 
																 List<Instance> instancesToRemove,
																 List<Match> matches) {
		
		for (List<Instance> instanceList : instancesToCheck) {
			boolean shouldStop = false;
			for (Instance instance : instanceList) {
				if (isInUnboundedIterativeTypeState(instance)) {
					instancesToAdd.addAll(attemptBufferEvaluationWithPendingEvent((LazyInstance)instance, event));
				}
				else {
					//TODO: the modification in performRegularInstanceLoop for avoiding instance storage inconsistency
					//when an instance switches to another state yet remains in the same list should be applied here as well
					performSingleInstanceLoopIteration(event, instance, instancesToAdd, instancesToRemove);
				}
				if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY && !instancesToAdd.isEmpty()) {
					retainFirstElementOnly(instancesToAdd);
					shouldStop = true;
					break;
				}
			}
			if (shouldStop) {
				break;
			}
		}
	}
	
	private void performSingleInstanceLoopIteration(Event event, Instance instance,
												   List<Instance> instancesToAdd,
												   List<Instance> instancesToRemove) {
		LazyInstance lazyInstance = (LazyInstance) instance;
		instancesToAdd.addAll(processNewEventOnInstance(event, lazyInstance));
		instancesToAdd.remove(instance);// just in case we somehow
										// occasionally inserted an existing
										// instance
		if (lazyInstance.getCurrentState().isRejecting()) {
			instancesToRemove.add(lazyInstance);
		}
	}
	
	public boolean shouldActivateUnboundedIterativeMode() {
		return !unboundedIterativeEventTypes.isEmpty();
	}
	
	private boolean isInUnboundedIterativeTypeState(Instance instance) {
		return (getOutgoingMatchTransitionForTypes(instance.getCurrentState(), unboundedIterativeEventTypes) != null);
	}
	
	protected Transition getOutgoingMatchTransitionForTypes(NFAState state, List<EventType> eventTypes) {
		for (Transition transition : state.getOutgoingTransitions()) {
			if (transition.isMatchTransition() && eventTypes.contains(transition.getEventType())) {
				return transition; //for now, we assume there is only one such transition
			}
		}
		return null;
	}

	@Override
	protected boolean checkInstanceForMatch(Instance instance, List<Match> listOfMatches,
			List<Instance> listOfInstancesToBeRemoved) {
		LazyInstance lazyInstance = (LazyInstance) instance;
		Match match = lazyInstance.getMatch();
		if (match == null) {
			return false;
		}
		if (lazyInstance.shouldReportMatch()) {
			listOfMatches.add(match);
		}
		if (listOfInstancesToBeRemoved != null && lazyInstance.shouldDiscardWithMatch()) {
			listOfInstancesToBeRemoved.add(lazyInstance);
		}
		return true;
	}

	private List<LazyInstance> processNewEventOnInstance(Event event, LazyInstance instance) {
		List<LazyInstance> newInstances = new ArrayList<LazyInstance>();
		for (Transition transition : instance.getCurrentState().getOutgoingTransitions()) {
			newInstances.addAll(attemptTransitionOnInstance(event, instance, (LazyTransition) transition));
			if (instance.getAndResetShouldStopEvaluation()) {
				break;// cannot continue (e.g. due to a timeout)
			}
		}
		return newInstances;
	}

	private List<LazyInstance> attemptTransitionOnInstance(Event event, LazyInstance instance,
			LazyTransition transition) {
		List<LazyInstance> newInstances = new ArrayList<LazyInstance>();
		if (!(instance.isTransitionPossible(event, transition)))
			return newInstances;

		boolean shouldCloneInstance = transition.shouldCloneInstance();
		LazyInstance immediateTransitionInstance;
		if (shouldCloneInstance) {
			immediateTransitionInstance = instance.clone();
		} else {
			immediateTransitionInstance = instance;
		}
		immediateTransitionInstance.executeTransition(event, transition);
		boolean hasFinishedWithMatch = immediateTransitionInstance.getCurrentState().isAccepting();
		boolean hasFinished = hasFinishedWithMatch || immediateTransitionInstance.getCurrentState().isRejecting();

		if (hasFinishedWithMatch || (shouldCloneInstance && canWaitForInput(immediateTransitionInstance))) {
			newInstances.add(immediateTransitionInstance);
		}

		if (transition.canProceedToBufferEvaluation() && !hasFinished) {
			newInstances.addAll(attemptBufferEvaluation(immediateTransitionInstance));
		}

		return newInstances;
	}

	private boolean canWaitForInput(Instance instance) {
		List<Event> matchBuffer = instance.getEventsFromMatchBuffer();
		for (Transition transition : instance.getCurrentState().getOutgoingTransitions()) {
			if (!transition.isMatchTransition()) {
				continue;
			}
			LazyTransition lazyTransition = (LazyTransition) transition;
			if ((lazyTransition.getActualSucceedingEvent(matchBuffer) == null)) {
				return true;
			}
		}
		return false;
	}

	private List<LazyInstance> attemptBufferEvaluation(LazyInstance instance) {
		return attemptBufferEvaluationWithPendingEvent(instance, null);
	}
	
	private List<LazyInstance> attemptBufferEvaluationWithPendingEvent(LazyInstance instance, Event pendingEvent) {
		List<LazyInstance> newInstances = new ArrayList<LazyInstance>();
		LazyTransition searchFailedTransition = null;
		for (Transition transition : instance.getCurrentState().getOutgoingTransitions()) {
			LazyTransition lazyTransition = (LazyTransition) transition;
			switch (lazyTransition.getType()) {
			case REGULAR:
				if (!lazyTransition.isMatchTransition()) {
					break;
				}
				List<Event> bufferedEvents = getRelevantBufferedEvents(instance, lazyTransition, pendingEvent);
				lazyTransition.enableCache();
				for (Event bufferedEvent : bufferedEvents) {
					newInstances.addAll(attemptTransitionOnInstance(bufferedEvent, instance, lazyTransition));
					if (instance.getCurrentState().isRejecting()) {
						// we cannot continue in this case
						newInstances.clear();
						newInstances.add(instance);
						return newInstances;
					}
				}
				lazyTransition.disableCache();
				break;
			case SEARCH_FAILED:
				searchFailedTransition = lazyTransition;
				break;
			case TIMEOUT:
				newInstances.addAll(attemptTransitionOnInstance(null, instance, lazyTransition));
				break;
			default:
				break;
			}
		}
		if (newInstances.isEmpty() && searchFailedTransition != null) {
			newInstances.addAll(attemptTransitionOnInstance(null, instance, searchFailedTransition));
		}
		return newInstances;
	}

	private List<Event> getEventsFromInputBuffer(LazyInstance instance, LazyTransition transition) {
		List<Event> matchBuffer = instance.getEventsFromMatchBuffer();
		Event lowerBoundEvent = transition.getActualPrecedingEvent(matchBuffer);
		Event upperBoundEvent = transition.getActualSucceedingEvent(matchBuffer);
		return inputBuffer.getSlice(transition.getEventType(), lowerBoundEvent, upperBoundEvent);
	}
	
	private List<Event> computeAggregatedBufferedEvents(LazyInstance instance, LazyTransition transition) {
		List<Event> bufferedEvents = getEventsFromInputBuffer(instance, transition);
		List<BufferPreprocessor> preprocessors = transition.getBufferPreprocessors();
		if (preprocessors.isEmpty()) {
			return BufferPreprocessor.createAggregatedEventsFromBufferedEvents(bufferedEvents);
		}
		for (int i = 0; i < preprocessors.size(); ++i) {
			Environment.getEnvironment().getStatisticsManager().updateDiscreteStatistic(Statistics.computations,
																						bufferedEvents.size());
			bufferedEvents = preprocessors.get(i).preprocessEvents(instance, bufferedEvents, 
																   i == preprocessors.size() - 1);
		}
		return bufferedEvents;
	}
	
	private List<Event> prepareAggregatedBufferedEvents(LazyInstance instance, LazyTransition transition,
											    		Event pendingEvent) {
		if (!shouldActivateUnboundedIterativeMode() || pendingEvent == null) {
			return computeAggregatedBufferedEvents(instance, transition);
		}
		
		List<Event> aggregatedEvents = null;
		EfficientInputBuffer lastAggregatedEventsBuffer = instance.getLastAggregatedEvents();
		
		if (lastAggregatedEventsBuffer == null) {
			throw new RuntimeException("Unexpected error: aggregated events cache is not initialized");
		}
		
		EventType pendingEventType = pendingEvent.getType();
		if (lastAggregatedEventsBuffer.hasTypeBuffer(pendingEventType)) {
			lastAggregatedEventsBuffer.refresh(pendingEvent.getTimestamp());
			aggregatedEvents = new ArrayList<Event>(lastAggregatedEventsBuffer.getTypeBuffer(pendingEventType));
		}
		else {
			aggregatedEvents = computeAggregatedBufferedEvents(instance, transition);
			lastAggregatedEventsBuffer.addTypeBuffer(pendingEventType, aggregatedEvents.size());
		}
		
		//TODO: pendingEvent bypasses preprocessors!
		//Has to be fixed when iterations and the end of the pattern will be evaluated
		for (Event aggregatedEvent : aggregatedEvents) {
			((AggregatedEvent)aggregatedEvent).addPrimitiveEvent(pendingEvent);
		}
		
		lastAggregatedEventsBuffer.storeAll(aggregatedEvents);		
		return aggregatedEvents;
	}
	
	private List<Event> getRelevantBufferedEvents(LazyInstance instance, 
												  LazyTransition transition, Event pendingEvent) {
		if (transition.getType() != LazyTransitionType.REGULAR) {
			// something unexpected has happened
			return new ArrayList<Event>();
		}
		Transition.Action action = transition.getAction();
		switch(action) {
			case TAKE:
				return getEventsFromInputBuffer(instance, transition);
			case ITERATE:
				return prepareAggregatedBufferedEvents(instance, transition, pendingEvent);
			case IGNORE:
			case STORE:
			default:
				throw new RuntimeException(String.format("Illegal edge action for buffer evaluation: %s", action));
		}
	}

	private void addTransition(NFAState sourceState, NFAState destinationState, LazyTransitionType type,
			Transition.Action action, EventType eventType, Condition condition,
			EventTemporalPositionCondition temporalCondition) {
		Transition transition = new LazyTransition(type, sourceState, destinationState, action, eventType, condition,
				temporalCondition);
		transitions.add(transition);
	}

	protected void addRegularTransition(NFAState sourceState, NFAState destinationState, Transition.Action action,
			EventType eventType, Condition condition, EventTemporalPositionCondition temporalCondition) {
		addTransition(sourceState, destinationState, LazyTransitionType.REGULAR, action, eventType, condition,
				temporalCondition);
	}

	protected void addTakeTransition(NFAState sourceState, NFAState destinationState, EventType eventType,
									 Condition condition, EventTemporalPositionCondition temporalCondition) {
		addRegularTransition(sourceState, destinationState, Transition.Action.TAKE, eventType, condition,
				temporalCondition);
	}
	
	protected void addIterateTransition(NFAState sourceState, NFAState destinationState, EventType eventType,
									    Condition condition, EventTemporalPositionCondition temporalCondition) {
		addRegularTransition(sourceState, destinationState, Transition.Action.ITERATE, 
							 eventType, condition, temporalCondition);
	}

	protected void addStoreTransition(NFAState state, EventType eventType) {
		addRegularTransition(state, state, Transition.Action.STORE, eventType, new TrivialCondition(), null);
	}

	protected void addTimeoutTransition(NFAState sourceState, NFAState destinationState) {
		addTransition(sourceState, destinationState, LazyTransitionType.TIMEOUT, Transition.Action.IGNORE, null,
				new TrivialCondition(), null);
	}

	protected void addSearchFailedTransition(NFAState sourceState, NFAState destinationState) {
		addTransition(sourceState, destinationState, LazyTransitionType.SEARCH_FAILED, Transition.Action.IGNORE, null,
				new TrivialCondition(), null);
	}

	@Override
	public List<Match> getLastMatches() {
		for (Instance instance : instances.getAllInstances()) {
			((LazyInstance) instance).forceTimeout();
		}
		return super.getLastMatches();
	}

	@Override
	protected void appendNFA(NFA nfa) {
		if (nfa == this || !(nfa instanceof LazyNFA)) {
			return;
		}
		LazyNFA lazyNFA = (LazyNFA) nfa;
		lazyNFA.replaceState(lazyNFA.rejectingState, rejectingState);
		super.appendNFA(nfa);
		states.remove(lazyNFA.rejectingState);// was mistakenly inserted by
												// superclass method
	}

	@Override
	public long size() {
		return super.size() + inputBuffer.size();
	}
}
