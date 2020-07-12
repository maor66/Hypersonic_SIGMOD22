package sase.evaluation.nfa;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.nfa.eager.elements.Instance;
import sase.evaluation.nfa.eager.elements.InstanceStorage;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public abstract class NFA implements IEvaluationMechanism, IEvaluationMechanismInfo {

	public static final String inputBufferEventTypeName = "InputBufferEvent";
	public static final String inputBufferCountersAttributeName = "InputBufferCounters";
	public static EventType inputEventType = null;

	private boolean isCreationCompleted;

	protected final long timeWindow;
	public final List<EventType> supportedEventTypes;
	protected final List<List<EventType>> sequences;
	protected final List<EventType> negativeTypes;
	protected final List<EventType> iterativeTypes;

	protected List<NFAState> states = new ArrayList<NFAState>();
	protected List<Transition> transitions = new ArrayList<Transition>();
	protected NFAState initialState = null;
	protected NFAState finalState = null;

	protected InstanceStorage instances;

	protected long lastKnownGlobalTime = 0;

	public NFA(Pattern pattern) {
		if (inputEventType == null) {
			Attribute[] attributes = { new Attribute(Datatype.TEXT, inputBufferCountersAttributeName) };
			inputEventType = new EventType(inputBufferEventTypeName, attributes);
		}

		timeWindow = pattern.getTimeWindow();
		supportedEventTypes = pattern.getEventTypes();
		if (pattern instanceof CompositePattern) {
			CompositePattern compositePattern = (CompositePattern) pattern;
			sequences = compositePattern.extractSequences(false);
			//NOTE: not guaranteed to work in the multi-pattern setting (what happens if an event type is positive in one
			//pattern and negative in another one?)
			negativeTypes = compositePattern.getNegativeEventTypes();
			iterativeTypes = compositePattern.getIterativeEventTypes();
		} else {
			sequences = new ArrayList<List<EventType>>();
			negativeTypes = new ArrayList<EventType>();
			iterativeTypes = new ArrayList<EventType>();
		}
		isCreationCompleted = false;
	}

	public void completeCreation(List<Pattern> patterns) {
		if (isCreationCompleted)
			return;
		initNFAStructure();
		isCreationCompleted = true;
		Environment.getEnvironment().getStatisticsManager().replaceDiscreteStatistic(Statistics.automatonStatesNumber,
																					 states.size());
		Environment.getEnvironment().getStatisticsManager().replaceDiscreteStatistic(Statistics.automatonTransitionsNumber,
																					 transitions.size());
		instances = new InstanceStorage(this);
		Instance initialInstance = createInstance();
		instances.add(initialInstance);
	}

	protected Instance createInstance() {
		return new Instance(this, initialState);
	}

	private List<Instance> executeEmptyTransitions(Instance instance) {
		List<Instance> newInstances = new ArrayList<Instance>();
		for (Transition transition : instance.getCurrentState().getOutgoingTransitions()) {
			if (transition.getEventType() != null)
				continue;
			Instance newInstance = instance.clone();
			newInstance.executeTransition(null, transition);
			newInstances.add(newInstance);
		}
		return newInstances;
	}

	private List<Instance> processNewEventOnInstance(Event event, Instance instance) {
		List<Instance> newInstances = new ArrayList<Instance>();
		for (Transition transition : instance.getCurrentState().getOutgoingTransitions()) {
			if (!(instance.isTransitionPossible(event, transition)))
				continue;

			Instance newInstance = instance.clone();
			newInstances.add(newInstance);
			newInstance.executeTransition(event, transition);
			newInstances.addAll(executeEmptyTransitions(newInstance));
		}
		return newInstances;
	}
	
	protected void handleInstanceStorageUpdate(List<List<Instance>> instancesToCheck, 
											   List<Instance> instancesToAdd, 
											   List<Instance> instancesToRemove) {
		for (Instance instance : instancesToAdd) {
			instances.add(instance);
		}
		for (Instance instance : instancesToRemove) {
			instances.remove(instance);
		}
	}
	
	protected void retainFirstElementOnly(List<? extends Object> list) {
		if (list != null && !list.isEmpty()) {
			list = list.subList(0, 1);
		}
	}

	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		if (!(supportedEventTypes.contains(event.getType()))) {
			// irrelevant event
			return null;
		}
		List<Instance> instancesToAdd = new ArrayList<Instance>();
		List<Instance> instancesToRemove = new ArrayList<Instance>();
		List<Match> matches = new LinkedList<Match>();
		List<List<Instance>> instancesToCheck = instances.getInstancesForEvent(event, canStartInstance);
		if (instancesToCheck == null) {
			return null;
		}
		Long startTimestamp = System.currentTimeMillis();
		for (List<Instance> instanceList : instancesToCheck) {
			boolean shouldStop = false;
			for (Instance instance : instanceList) {
				if (Environment.getEnvironment().isTimeoutReached(System.currentTimeMillis() - startTimestamp)) {
					shouldStop = true;
					break;
				}
				instancesToAdd.addAll(processNewEventOnInstance(event, instance));
				if (instance.shouldInvalidate()) {
					instancesToRemove.add(instance);
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
		getPendingMatches(matches, instancesToRemove, false);
		handleInstanceStorageUpdate(instancesToCheck, instancesToAdd, instancesToRemove);
		Environment.getEnvironment().getStatisticsManager().updateDiscreteIfBigger(Statistics.peakInstances,
																				   getInstancesNumber());
		Environment.getEnvironment().getStatisticsManager().updateDiscreteIfBigger(Statistics.peakBufferedEvents,
																				   getBufferedEventsNumber());
		if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY) {
			retainFirstElementOnly(matches);
		}
		return matches.size() > 0 ? matches : null;
	}

	protected Match performMatchPostProcessing(Instance instance) {
		return instance.getMatch(); // to be overridden by subclasses
	}

	protected int getInstancesNumber() {
		return instances.getInstancesNumber();
	}

	protected int getBufferedEventsNumber() {
		return 0; // to be overridden by subclasses
	}

	public long getLastKnownGlobalTime() {
		return lastKnownGlobalTime;
	}

	public long getTimeWindow() {
		return timeWindow;
	}
	
	protected boolean getPendingMatches(List<Match> listOfMatches,
									 	List<Instance> listOfInstancesToBeRemoved,
									 	boolean stopOnFirstMatch) {
		for (Instance instance : instances.getInstancesInAcceptingStates()) {
			checkInstanceForMatch(instance, listOfMatches, listOfInstancesToBeRemoved);
			if (stopOnFirstMatch && (!listOfMatches.isEmpty())) {
				return true;
			}
		}
		return (!listOfMatches.isEmpty());
	}

	protected boolean checkInstanceForMatch(Instance instance, List<Match> listOfMatches,
											List<Instance> listOfInstancesToBeRemoved) {
		Match initialMatch = instance.getMatch();
		Match actualMatch = performMatchPostProcessing(instance);
		if (initialMatch == null) {
			return false;
		}
		// the evaluation was completed - the instance should be removed
		// regardless of success
		if (actualMatch != null && instance.shouldReportMatch()) {
			// an actual match was detected
			listOfMatches.add(actualMatch);
		}
		if (listOfInstancesToBeRemoved != null && instance.shouldDiscardWithMatch()) {
			listOfInstancesToBeRemoved.add(instance);
		}
		return true;
	}

	private boolean checkInstanceForMatch(Instance instance, List<Match> listOfMatches) {
		return checkInstanceForMatch(instance, listOfMatches, null);
	}

	public List<Match> getLastMatches() {
		List<Match> matches = new ArrayList<Match>();
		for (Instance instance : instances.getInstancesInAcceptingStates()) {
			checkInstanceForMatch(instance, matches);
		}
		return matches;
	}

	public List<Match> validateTimeWindow(long currentTime, Event event) {
		lastKnownGlobalTime = currentTime;
		List<Instance> expiredAcceptingInstances = instances.validateTimeWindow(currentTime, false);
		List<Match> matches = new ArrayList<Match>();
		for (Instance instance : expiredAcceptingInstances) {
			checkInstanceForMatch(instance, matches);// it's possible that
													 // this instance is
													 // expired with a
													 // match
		}
		return matches;
	}

	public boolean isCreationCompleted() {
		return isCreationCompleted;
	}

	protected void addTransition(NFAState sourceState, NFAState destinationState, Transition.Action action,
			EventType eventType, Condition condition) {
		Transition transition = new Transition(sourceState, destinationState, action, eventType, condition);
		transitions.add(transition);
	}

	protected void addEmptyTransition(NFAState sourceState, NFAState destinationState, Transition.Action action,
			Condition condition) {
		addTransition(sourceState, destinationState, action, null, condition);
	}

	protected void replaceState(NFAState oldState, NFAState newState) {
		replaceState(oldState, newState, false);
	}

	protected void replaceState(NFAState oldState, NFAState newState, boolean moveSelfLoops) {
		List<Transition> transitionsFromOldState = new ArrayList<Transition>();
		List<Transition> transitionsToOldState = new ArrayList<Transition>();
		for (Transition transition : transitions) {
			NFAState source = transition.getSource();
			NFAState destination = transition.getDestination();
			if (source == oldState && (moveSelfLoops || destination != oldState)) {
				transitionsFromOldState.add(transition);
			}
			if (destination == oldState && (moveSelfLoops || source != oldState)) {
				transitionsToOldState.add(transition);
			}
		}
		for (Transition transition : transitionsFromOldState) {
			transition.setSource(newState);
		}
		for (Transition transition : transitionsToOldState) {
			transition.setDestination(newState);
		}
		if (MainConfig.debugMode) {
			System.out.println(String.format("%s replaces %s", newState, oldState));
		}
	}

	protected void appendNFA(NFA nfa) {
		if (nfa == this) {
			return;
		}
		nfa.replaceState(nfa.initialState, initialState, true);
		nfa.replaceState(nfa.finalState, finalState);
		for (NFAState state : nfa.states) {
			if (state.isInitial() || state.isFinal()) {
				continue;
			}
			states.add(state);
		}
		transitions.addAll(nfa.transitions);
	}

	public List<EventType> getNegativeTypes() {
		return negativeTypes;
	}

	public List<EventType> getIterativeTypes() {
		return iterativeTypes;
	}
	
	public List<Transition> getTransitions() {
		return transitions;
	}

	public List<NFAState> getStates() {
		return states;
	}
	
	public NFAState getInitialState() {
		return initialState;
	}
	
	public NFAState getAcceptingState() {
		return finalState;
	}

	public long size() {
		return instances.size();
	}

	@Override
	public State getStateByAtomicCondition(AtomicCondition condition) {
		for (Transition transition : transitions) {
			Condition currentCondition = transition.getCondition();
			if (currentCondition == null || !(currentCondition instanceof CNFCondition)) {
				continue;
			}
			CNFCondition cnfCondition = (CNFCondition) currentCondition;
			if (cnfCondition.getAtomicConditions().contains(condition)) {
				return transition.getDestination();
			}
		}
		return null;
	}
	
	private List<Transition> getPathFromInitial(NFAState state) {
		if (state == initialState) {
			return new ArrayList<Transition>();
		}
		for (Transition transition : state.getIncomingTransitions()) {
			if (transition.getSource() == state) {
				continue;
			}
			List<Transition> restOfPath = getPathFromInitial(transition.getSource());
			if (restOfPath != null) {
				restOfPath.add(transition);
				return restOfPath;
			}
		}
		return null;
	}

	@Override
	public double getStateReachProbability(State state) {
		List<Transition> pathFromInitial = getPathFromInitial((NFAState)state);
		if (pathFromInitial == null) {
			throw new RuntimeException("No path from the initial state found!");
		}
		double result = 1.0;
		for (Transition transition : pathFromInitial) {
			result *= transition.getCondition().getSelectivity();
		}
		return result;
	}
	
	@Override
	public void removeConflictingInstances(List<Match> matches) {
		if (matches == null) {
			return;
		}
		for (Match match : matches) {
			instances.removeConflictingInstances(match);
		}
	}

	protected abstract void initNFAStructure();
	public ArrayList<Match> waitForGroupToFinish ()
	{
		//Do nothing
		return null;
	}
}
