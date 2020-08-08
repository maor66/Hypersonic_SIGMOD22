package sase.evaluation.nfa.eager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.Instance;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.lazy.elements.EfficientInputBuffer;
import sase.pattern.Pattern;
import sase.pattern.UnaryPattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.base.TrivialCondition;
import sase.pattern.condition.contiguity.PairwiseContiguityCondition;
import sase.pattern.condition.contiguity.TotalContiguityCondition;
import sase.pattern.condition.iteration.eager.IteratedIncrementalCondition;
import sase.pattern.condition.iteration.eager.IterationTriggerCondition;
import sase.pattern.condition.time.PairTemporalOrderCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class AND_SEQ_NFA extends AND_NFA {

	private final HashMap<EventType, CNFCondition> negativeTemporalConditions = new HashMap<EventType, CNFCondition>();
	private final List<EventType> negativeTypesAtSequenceEnd = new ArrayList<EventType>();
	private EfficientInputBuffer negatedEventsBuffer;
	private List<Instance> frozenInstances = new LinkedList<Instance>();
	
	public AND_SEQ_NFA(Pattern pattern) {
		super(pattern);
		negatedEventsBuffer = new EfficientInputBuffer(pattern, true);
		if (MainConfig.selectionStrategy == EventSelectionStrategies.CONTUGUITY) {
			addContiguityConstraints();
		}
		initNegativeTemporalConditions();
	}
	
	private void initNegativeTemporalConditions() {
		//note: if no temporal conditions exist for some negative event type, an empty condition is still initialized
		for (EventType negativeEventType : negativeTypes) {
			for (List<EventType> sequence : sequences) {
				for (int i = 0; i < sequence.size(); ++i) {
					if (sequence.get(i) != negativeEventType) {
						continue;
					}
					CNFCondition currentCondition = new CNFCondition();
					if (i > 0) {
						currentCondition.addAtomicCondition(new PairTemporalOrderCondition(
																					sequence.get(i - 1),
																					negativeEventType));
					}
					if (i < sequence.size() - 1) {
						currentCondition.addAtomicCondition(new PairTemporalOrderCondition(
								negativeEventType,
								sequence.get(i + 1)));
					}
					if (i == sequence.size() - 1) {
						//this is a closing negative type
						if (!(negativeTypesAtSequenceEnd.contains(negativeEventType))) {
							negativeTypesAtSequenceEnd.add(negativeEventType);
						}
					}
					negativeTemporalConditions.put(negativeEventType, currentCondition);
				}
			}
		}
	}
	
	private void addContiguityConstraints() {
		switch (pattern.getType()) {
			case SEQ:
				addPairwiseContiguityConstraints();
				if (!pattern.isActuallyComposite()) {
					break;
				}
				//otherwise, continue to the AND case
			case AND_SEQ:
				addTotalContiguityConstraints();
				break;
			case ITER:
			case NEG:
			case NONE:
			case NOP:
			case OLD_AND:
			case OLD_SEQ:
			case OR:
			default:
				break;
		}
	}
	
	private void addPairwiseContiguityConstraints() {
		if (pattern.getType() != PatternOperatorTypes.SEQ || pattern.getEventTypes().size() < 2) {
			return;
		}
		CNFCondition mainCondition = (CNFCondition)pattern.getCondition();
		UnaryPattern prevPattern = null;
		for (Pattern pattern : pattern.getNestedPatterns()) {
			if (pattern.getType() == PatternOperatorTypes.NEG) {
				continue;
			}
			if (pattern.getType() == PatternOperatorTypes.ITER) {
				prevPattern = null;
				continue;
			}
			UnaryPattern unaryPattern = (UnaryPattern) pattern;
			if (prevPattern != null) {
				mainCondition.addAtomicCondition(new PairwiseContiguityCondition(prevPattern.getEventType(),
																				 unaryPattern.getEventType()));
			}
			prevPattern = unaryPattern;
		}
	}
	
	private void addTotalContiguityConstraints() {
		((CNFCondition) pattern.getCondition()).addAtomicCondition(new TotalContiguityCondition());
	}
	
	private boolean isSequenceOrderViolation(EventType candidateType, List<EventType> nextTypes) {
		for (List<EventType> sequence : sequences) {
			int i = 0;
			for (; i < sequence.size(); ++i) {
				if (sequence.get(i) == candidateType)
					break;
			}
			if (i == sequence.size())
				continue;
			for (--i; i >= 0; --i) {
				if (nextTypes.contains(sequence.get(i))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void addTransitionsForIterativeTypeState(NFAState previousState, NFAState state,
													 EventType iterativeEventType, 
													 CNFCondition conditionForCurrentType) {
		CNFCondition conditionForSelfLoop = new CNFCondition();
		CNFCondition conditionForIncomingTransition = new CNFCondition();
		for (AtomicCondition atomicCondition : conditionForCurrentType.getAtomicConditions()) {
			if (!(atomicCondition instanceof IteratedIncrementalCondition)) {
				conditionForIncomingTransition.addAtomicCondition(atomicCondition);
			}
			if (!(atomicCondition instanceof IterationTriggerCondition)) {
				conditionForSelfLoop.addAtomicCondition(atomicCondition);
			}
		}
		
		//add incoming transition
		addTransition(previousState, state, Transition.Action.TAKE, iterativeEventType, conditionForIncomingTransition);
		//add self loop
		addTransition(state, state, Transition.Action.TAKE, iterativeEventType, conditionForSelfLoop);
	}
	
	@Override
	protected void buildStatesSubTree(NFAState rootState, List<EventType> types, CNFCondition condition) {
		List<EventType> positiveTypes = new ArrayList<EventType>();
		for (EventType eventType : types) {
			if (!negativeTypes.contains(eventType)) {
				positiveTypes.add(eventType);
			}
		}
		CNFCondition positiveCondition = condition.getConditionExcludingTypes(pattern.getNegativeEventTypes());
		recursiveBuildStatesSubTree(rootState, positiveTypes, positiveCondition);
	}

	//this method contains some code duplication from the now-deprecated AND_NFA - unfortunately, no choice until I
	//remove the parent class completely
	@Override
	protected void recursiveBuildStatesSubTree(NFAState rootState, List<EventType> types, CNFCondition condition) {
		if (types.isEmpty())
			return;
		
		if (types.size() == 1) {
			EventType eventType = types.get(0);
			if (iterativeTypes.contains(eventType)) {
				NFAState stateBeforeFinal = new NFAState("State before Final");
				states.add(stateBeforeFinal);
				addTransitionsForIterativeTypeState(rootState, stateBeforeFinal, eventType, condition);
				addTransition(stateBeforeFinal, finalState, Transition.Action.TAKE, null, new TrivialCondition());
			}
			else {
				addTransition(rootState, finalState, Transition.Action.TAKE, eventType, condition);
			}
			return;
		}
		
		for (EventType eventType : types) {
			List<EventType> typesForSubTree = new ArrayList<EventType>(types);
			typesForSubTree.remove(eventType);
			if (isSequenceOrderViolation(eventType, typesForSubTree)) {
				continue;
			}
			
			NFAState stateForCurrentType = new NFAState(String.format("State for %s", eventType.getName()));
			states.add(stateForCurrentType);
			
			CNFCondition conditionForCurrentType = condition.getConditionExcludingTypes(typesForSubTree);
			if (iterativeTypes.contains(eventType)) {
				addTransitionsForIterativeTypeState(rootState, stateForCurrentType, eventType, conditionForCurrentType);
			}
			else {
				addTransition(rootState, stateForCurrentType, Transition.Action.TAKE, eventType, conditionForCurrentType);
			}
			
			CNFCondition nextCondition = condition.getConditionForTypes(typesForSubTree, false);
			recursiveBuildStatesSubTree(stateForCurrentType, typesForSubTree, nextCondition);
		}
	}
	
	@Override
	public List<Match> validateTimeWindow(long currentTime, Event event) {
		List<Match> matches = super.validateTimeWindow(currentTime, event);
		negatedEventsBuffer.refresh(currentTime);
		if (frozenInstances.isEmpty()) {
			return matches;
		}
		
		List<Instance> expiredInstances = new ArrayList<Instance>();
		for (Instance instance : frozenInstances) {
			if (instance.isExpired(currentTime)) {
				Match match = performMatchPostProcessing(instance);
				if (match != null) {
					if (matches == null) {
						matches = new ArrayList<Match>();
					}
					matches.add(match);
				}
				expiredInstances.add(instance);
			}
		}
		frozenInstances.removeAll(expiredInstances);
		Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.instanceDeletions, 
																	 					  expiredInstances.size());
		return matches;
	}
	
	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		if (!(negativeTypes.contains(event.getType()))) {
			return super.processNewEvent(event, canStartInstance);
		}
		negatedEventsBuffer.store(event);
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.bufferInsertions);
		return null;
	}
	
	private boolean verifySingleNegativeEventType(Match match, CNFCondition condition, EventType negativeEventType) {
		//copying the list since we're going to modify it by adding negative events
		List<Event> positiveEvents = new ArrayList<Event>(match.getPrimitiveEvents());
		List<EventType> listForNegativeEventType = new ArrayList<EventType>();
		listForNegativeEventType.add(negativeEventType);
		CNFCondition conditionToVerify = condition.getConditionForTypes(listForNegativeEventType, false);
		//add temporal constraints
		conditionToVerify.addAtomicConditions(negativeTemporalConditions.get(negativeEventType));
		for (Event negativeEvent : negatedEventsBuffer.getTypeBuffer(negativeEventType)) {
			positiveEvents.add(0, negativeEvent);
			if (conditionToVerify.verify(positiveEvents)) {
				//negative event found - we should invalidate this match
				return false;
			}
			positiveEvents.remove(0);
		}
		return true;
	}
	
	private boolean hasNegativeTypesAtSequenceEnd() {
		return !(negativeTypesAtSequenceEnd.isEmpty());
	}
	
	@Override
	protected Match performMatchPostProcessing(Instance instance) {
		Match initialMatch = instance.getMatch();
		if (initialMatch == null)
			return null;
		CNFCondition mainCondition = (CNFCondition)pattern.getCondition();
		for (EventType negativeEventType : negativeTypes) {
			if (!(verifySingleNegativeEventType(initialMatch, mainCondition, negativeEventType))) {
				return null;
			}
		}
		if (hasNegativeTypesAtSequenceEnd() && !frozenInstances.contains(instance)) {
			//in this rare extreme case, this instance should be frozen and the match shouldn't be reported yet
			frozenInstances.add(instance);
			return null;
		}
		return initialMatch;
	}
	
	@Override
	protected int getInstancesNumber() {
		return super.getInstancesNumber() + frozenInstances.size();
	}
	
	@Override
	public long size() {
		long result = super.size() + negatedEventsBuffer.size();
		if (!frozenInstances.isEmpty()) {
			result += frozenInstances.size() * frozenInstances.get(0).size();
		}
		return result;
	}
	
	@Override
	protected int getBufferedEventsNumber() {
		return negatedEventsBuffer.numberOfEvents();
	}
	
	@Override
	public List<Match> getLastMatches() {
		//force timeout on all instances
		long dummyExpirationTime = lastKnownGlobalTime + timeWindow;
		return validateTimeWindow(dummyExpirationTime, null);
	}
}
