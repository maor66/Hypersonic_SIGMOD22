package sase.evaluation.nfa.eager.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.NFA;
import sase.evaluation.nfa.lazy.LazyNFA;
import sase.simulator.Environment;
import sase.statistics.Statistics;


public class InstanceStorage {

	private NFA nfa;
	private HashMap<NFAState, List<Instance>> instancesByCurrentState;
	private List<NFAState> acceptingStates;
	private HashMap<EventType, List<NFAState>> eventTypeToExpectingTakeStates;
	private HashMap<EventType, List<NFAState>> eventTypeToExpectingStoreStates;
	
	public InstanceStorage(NFA nfa) {
		this.nfa = nfa;
		initializeInstanceToTypeHash();
		initializeTypeToStateHashes();
	}
	
	private void initializeInstanceToTypeHash() {
		instancesByCurrentState = new HashMap<NFAState, List<Instance>>();
		acceptingStates = new ArrayList<NFAState>();
		for (NFAState state : nfa.getStates()) {
			instancesByCurrentState.put(state, new ArrayList<Instance>());
			if (state.isAccepting()) {
				acceptingStates.add(state);
			}
		}
	}
	
	private void initializeTypeToStateHashes() {
		eventTypeToExpectingTakeStates = new HashMap<EventType, List<NFAState>>();
		eventTypeToExpectingStoreStates = new HashMap<EventType, List<NFAState>>();
		for (Transition transition : nfa.getTransitions()) {
			HashMap<EventType, List<NFAState>> currHash;
			switch(transition.getAction()) {
				case ITERATE:
				case TAKE:
					currHash = eventTypeToExpectingTakeStates;
					break;
				case STORE:
					currHash = eventTypeToExpectingStoreStates;
					break;
				case IGNORE:
				default:
					continue;
			}
			EventType eventType = transition.getEventType();
			List<NFAState> currStateList;
			if (currHash.containsKey(eventType)) {
				currStateList = currHash.get(eventType);
			}
			else {
				currStateList = new ArrayList<NFAState>();
				currHash.put(eventType, currStateList);
			}
			if (!currStateList.contains(transition.getSource())) {
				currStateList.add(transition.getSource());
			}
		}
	}
	
	public void add(Instance instance) {
		instancesByCurrentState.get(instance.getCurrentState()).add(instance);
	}
	
	public boolean remove(Instance instance) {
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.instanceDeletions);
		return instancesByCurrentState.get(instance.getCurrentState()).remove(instance);
	}
	
	public void addAll(List<Instance> instancesToAdd) {
		//We assume here that all instances in the list have the same current state!
		//Validating this assumption may result in horrible bugs.
		if (instancesToAdd.isEmpty()) {
			return;
		}
		instancesByCurrentState.get(instancesToAdd.get(0).getCurrentState()).addAll(instancesToAdd);
	}
	
	public boolean removeAll(List<Instance> instancesToRemove) {
		//We assume here that all instances in the list have the same current state!
		//Violating this assumption may result in horrible bugs.
		if (instancesToRemove.isEmpty()) {
			return false;
		}
		Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.instanceDeletions,
																						  instancesToRemove.size());
		return instancesByCurrentState.get(instancesToRemove.get(0).getCurrentState()).removeAll(instancesToRemove);
	}
	
	public List<List<Instance>> getInstancesForEvent(Event event, boolean canStartInstance) {
		List<NFAState> statesExpectingEvent = eventTypeToExpectingTakeStates.get(event.getType());
		if (statesExpectingEvent == null) {
			return null;
		}
		List<List<Instance>> result = new ArrayList<List<Instance>>();
		for (NFAState state : statesExpectingEvent) {
			if (!canStartInstance && state == nfa.getInitialState()) {
				continue;
			}
			List<Instance> currentInstances = instancesByCurrentState.get(state);
			if (!currentInstances.isEmpty()) {
				result.add(currentInstances);
			}
		}
		return result;
	}
	
	public boolean shouldStoreEvent(Event event) {
		//note that we assume here that 'store' edges are always unconditional!
		List<NFAState> statesStoringEvent = eventTypeToExpectingStoreStates.get(event.getType());
		if (statesStoringEvent == null) {
			return false;
		}
		for (NFAState state : statesStoringEvent) {
			if (instancesByCurrentState.get(state).size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public int getInstancesNumber() {
		int result = 0;
		for (List<Instance> instanceList : instancesByCurrentState.values()) {
			result += instanceList.size();
		}
		return result;
	}
	
	public long size() {
		int result = 0;
		for (List<Instance> instanceList : instancesByCurrentState.values()) {
			if (instanceList.isEmpty()) {
				continue;
			}
			result += instanceList.size() * instanceList.get(0).size();
		}
		return result;
	}
	
	public List<Instance> getInstancesInAcceptingStates() {
		List<Instance> result = new ArrayList<Instance>();
		for (NFAState state : acceptingStates) {
			result.addAll(instancesByCurrentState.get(state));
		}
		return result;
	}
	
	private List<Instance> validateTimeWindowForInstancesInState(long currentTime, NFAState state) {
		List<Instance> expiredInstances = new ArrayList<Instance>();
		List<Instance> instances = instancesByCurrentState.get(state);
		for (Instance instance : instances) {
			if (instance.isExpired(currentTime)) {
				expiredInstances.add(instance);
			}
		}
		instances.removeAll(expiredInstances);
		Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.instanceDeletions,
																						  expiredInstances.size());
		return state.isAccepting() ? expiredInstances : new ArrayList<Instance>();
	}
	
	public List<Instance> validateTimeWindow(long currentTime, boolean rejectingStateOnly) {
		if (rejectingStateOnly) {
			if (!(nfa instanceof LazyNFA)) {
				return null;
			}
			return validateTimeWindowForInstancesInState(currentTime, ((LazyNFA)nfa).getRejectingState());
		}
		List<Instance> expiredAcceptingInstances = new ArrayList<Instance>();
		for (NFAState state : instancesByCurrentState.keySet()) {
			expiredAcceptingInstances.addAll(validateTimeWindowForInstancesInState(currentTime, state));
		}
		return expiredAcceptingInstances;
	}
	
	public List<Instance> getAllInstances() {
		List<Instance> result = new ArrayList<Instance>();
		for (List<Instance> instanceList : instancesByCurrentState.values()) {
			result.addAll(instanceList);
		}
		return result;
	}
	
	public void removeConflictingInstances(Match match) {
		List<Event> events = match.getPrimitiveEvents();
		for (List<Instance> instanceList : instancesByCurrentState.values()) {
			List<Instance> instancesToRemove = new ArrayList<Instance>();
			for (Instance instance : instanceList) {
				List<Event> currentInstanceEvents = instance.getEventsFromMatchBuffer();
				if (events == currentInstanceEvents) { //comparison by reference intended
					continue;
				}
				int originalSize = currentInstanceEvents.size();
				currentInstanceEvents.removeAll(events);
				if (currentInstanceEvents.size() < originalSize) {
					instancesToRemove.add(instance);
				}
			}
			instanceList.removeAll(instancesToRemove);
		}
	}

}
