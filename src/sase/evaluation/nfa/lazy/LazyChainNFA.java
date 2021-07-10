package sase.evaluation.nfa.lazy;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventSelectionStrategies;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.EvaluationOrder;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.OrderEvaluationPlan;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.contiguity.TotalContiguityCondition;
import sase.pattern.condition.iteration.eager.IteratedIncrementalCondition;
import sase.pattern.condition.iteration.lazy.IteratedTotalFromIncrementalCondition;
import sase.pattern.condition.time.EventTemporalPositionCondition;
import sase.pattern.condition.time.GlobalTemporalOrderCondition;


public class LazyChainNFA extends LazyNFA {

	protected final LazyNFANegationTypes negationType;
	protected final EvaluationOrder evaluationOrder;
	protected final CNFCondition fullCondition;
	private final GlobalTemporalOrderCondition globalTemporalOrderCondition;
	
	public LazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
		super(pattern);
		this.negationType = negationType;
		evaluationOrder = new EvaluationOrder(pattern, ((OrderEvaluationPlan)evaluationPlan).getRepresentation());
		fullCondition = createFullCondition(pattern);
		CompositePattern compositePattern = (CompositePattern) pattern;
		globalTemporalOrderCondition = new GlobalTemporalOrderCondition(compositePattern.extractSequences(false));
		if (MainConfig.selectionStrategy == EventSelectionStrategies.CONTUGUITY && 
			pattern.getType() == PatternOperatorTypes.AND_SEQ) {
				fullCondition.addAtomicCondition(new TotalContiguityCondition());
		}
	}
	
	private CNFCondition createFullCondition(Pattern pattern) {
		CNFCondition result = new CNFCondition();
		CNFCondition conditionFromPattern = (CNFCondition)pattern.getCondition();
		for (AtomicCondition atomicCondition : conditionFromPattern.getAtomicConditions()) {
			AtomicCondition processedAtomicCondition = atomicCondition;
			if (atomicCondition instanceof IteratedIncrementalCondition) {
				IteratedIncrementalCondition incrementalCondition = (IteratedIncrementalCondition)atomicCondition;
				processedAtomicCondition = new IteratedTotalFromIncrementalCondition(incrementalCondition);
			}
			result.addAtomicCondition(processedAtomicCondition);
		}
		return result;
	}

	@Override
	protected void initNFAStructure() {
		initialState = new TypedNFAState(evaluationOrder.getFullEvaluationOrder().get(0),"Initial State", true, false, false);
		finalState = new TypedNFAState(evaluationOrder.getFullEvaluationOrder().get(evaluationOrder.getFullEvaluationOrder().size()-1),"Final State", false, true, true);
		rejectingState = new NFAState("Rejecting State", false, true, false);
		switch(negationType) {
			case NONE:
				buildStatesChain();
				break;
			case POST_PROCESSING:
				buildStatesChainWithPostProcessingNegation();
				break;
			case FIRST_CHANCE:
				buildStatesChainWithFirstChanceNegation();
				break;
			default:
				break;
		}
		if (!states.contains(rejectingState)) {
			states.add(rejectingState);
		}
	}
	
	private void buildStatesChainWithFirstChanceNegation() {
		List<EventType> positiveOrder = evaluationOrder.getPositiveEvaluationOrder();
		List<EventType> negativeOrder = evaluationOrder.getInverseNegativeEvaluationOrder();
		CNFCondition fullPositiveCondition = fullCondition.getConditionExcludingTypes(negativeOrder);
		List<EventType> unboundedNegativeTypes = new ArrayList<EventType>();
		List<EventType> unusedNegativeTypes = new ArrayList<EventType>();
		
		buildStatesChain(fullPositiveCondition, positiveOrder, initialState, finalState, false, null);
		
		for (EventType negativeEventType : negativeOrder) {
			EventTemporalPositionCondition temporalCondition = 
					globalTemporalOrderCondition.getPositionConstraintsForType(negativeEventType);
			List<EventType> precedingEvents = getPositivePrecedingEvents(temporalCondition);
			List<EventType> succeedingEvents = getPositiveSucceedingEvents(temporalCondition);
			List<EventType> dependingEvents = getPositiveDependingEvents(negativeEventType);
			CNFCondition condition = fullCondition.getConditionForType(negativeEventType, false);
			
			boolean isUnbounded = (succeedingEvents.isEmpty());
			if (isUnbounded) {
				unboundedNegativeTypes.add(negativeEventType);
			}
			
			Transition outgoingTakeTransition = null;
			boolean areConditionsSatisfied = false;
			boolean prevAreConditionsSatisfied = false;
			boolean addedEdgeToRejectingState = false;
			for (NFAState state = initialState; state != finalState; state = outgoingTakeTransition.getDestination()) {
				areConditionsSatisfied = (precedingEvents.isEmpty() && 
										  dependingEvents.isEmpty() && 
										  succeedingEvents.isEmpty());
				if (!areConditionsSatisfied) {
					addStoreTransition(state, negativeEventType);
				}
				if ((isUnbounded && addedEdgeToRejectingState) || areConditionsSatisfied != prevAreConditionsSatisfied) {
					addTakeTransition(state, rejectingState, negativeEventType, condition, temporalCondition);
					makeLastTransitionHighPriority(state);
					addedEdgeToRejectingState = true;
				}
				prevAreConditionsSatisfied = areConditionsSatisfied;
				
				outgoingTakeTransition = getOutgoingPositiveMatchTransition(state, positiveOrder);
				if (outgoingTakeTransition == null) {
					throw new RuntimeException(String.format("Invalid state detected: %s", state));
				}
				EventType currentEventType = outgoingTakeTransition.getEventType();
				
				//attempt to remove the event from lists - unchanged if not present
				precedingEvents.remove(currentEventType);
				succeedingEvents.remove(currentEventType);
				dependingEvents.remove(currentEventType);
			}
			if (!areConditionsSatisfied) {
				//in this case no rejecting edge could be added - we will fall back to post-processing negation later
				unusedNegativeTypes.add(negativeEventType);
			}
			else if (isUnbounded) {
				//temporarily added to final, to be fixed later
				addTakeTransition(finalState, rejectingState, negativeEventType, condition, temporalCondition);
			}
		}
		
		if (!unusedNegativeTypes.isEmpty()) {
			CNFCondition fullNegativeCondition = fullCondition.getConditionForTypes(unusedNegativeTypes, false);
			NFAState mediatorState = new NFAState("Mediator State");
			replaceState(finalState, mediatorState);
			buildStatesChain(fullNegativeCondition, unusedNegativeTypes, mediatorState, finalState, true, null);
		}
		
		else if (!unboundedNegativeTypes.isEmpty()) {		
			//handle the special case of negative event(s) at the end
			addTimeoutStateBeforeFinalState();
		}
	}
	
	private void buildStatesChainWithPostProcessingNegation() {
		List<EventType> positiveOrder = evaluationOrder.getPositiveEvaluationOrder();
		List<EventType> negativeOrder = evaluationOrder.getInverseNegativeEvaluationOrder();
		CNFCondition fullPositiveCondition = fullCondition.getConditionExcludingTypes(negativeOrder);
		CNFCondition fullNegativeCondition = fullCondition.getConditionForTypes(negativeOrder, false);
		NFAState mediatorState = new NFAState("Mediator State");
		buildStatesChain(fullPositiveCondition, positiveOrder, initialState, mediatorState, false, negativeOrder);
		buildStatesChain(fullNegativeCondition, negativeOrder, mediatorState, finalState, true, null);
	}
	
	protected void buildStatesChain() {
		buildStatesChain(fullCondition, evaluationOrder.getFullEvaluationOrder(), 
						 initialState, finalState, false, null);
	}
	
	protected void buildStatesChain(CNFCondition fullCondition, List<EventType> evaluationOrderForChain,
									NFAState firstState, NFAState lastState, boolean isNegativeChain,
									List<EventType> additionalEventsToStore) {
		List<CNFCondition> transitionConditions = 
								fullCondition.getSubConditionsByOrder(evaluationOrderForChain, isNegativeChain);
		List<EventTemporalPositionCondition> temporalConstraints = 
								globalTemporalOrderCondition.getPositionConstraintsByOrder(evaluationOrderForChain,
																						   isNegativeChain);
		NFAState prevState = null;
		EventType prevType = null;
		CNFCondition prevCondition = null;
		EventTemporalPositionCondition prevTemporalConstraint = null;
		for (int i = 0; i < evaluationOrderForChain.size(); ++i) {
			EventType currentType = evaluationOrderForChain.get(i);
			NFAState currentState = (i == 0 || prevState == null) ? firstState :
										  new TypedNFAState(currentType, String.format("State for %s", prevType.getName()));
			if (!states.contains(currentState)) {
				states.add(currentState);
			}
			
			//add 'per-chain-state' transitions - self-loops and edges to the rejecting state
			for (int j = i+1; j < evaluationOrderForChain.size(); ++j) {
				addStoreTransition(currentState, evaluationOrderForChain.get(j));
			}
			if (isNegativeChain) {
				EventTemporalPositionCondition currentTemporalConstraint = temporalConstraints.get(i); 
				addTakeTransition(currentState, rejectingState, currentType,
								  transitionConditions.get(i), currentTemporalConstraint);
			}
			else { //positive chain
				if (additionalEventsToStore != null) {
					for (EventType eventType : additionalEventsToStore) {
						addStoreTransition(currentState, eventType);
					}
				}
				if (i > 0) {
					addTimeoutTransition(currentState, rejectingState);
				}
			}
			
			if (i > 0) {
				addTransitionBetweenChainStates(prevState, currentState, isNegativeChain, 
												prevType, prevCondition, prevTemporalConstraint);
			}
			
			prevState = currentState;
			prevType = currentType;
			prevCondition = transitionConditions.get(i);
			prevTemporalConstraint = temporalConstraints.get(i);
		}
		if (!states.contains(lastState)) {
			states.add(lastState);
		}
		addTransitionBetweenChainStates(prevState, lastState, isNegativeChain, 
										prevType, prevCondition, prevTemporalConstraint);
	}
	
	private void addTransitionBetweenChainStates(NFAState sourceState, NFAState destinationState, boolean isNegativeChain,
												 EventType eventType, Condition condition,
												 EventTemporalPositionCondition temporalCondition) {
		if (isNegativeChain) {
			if (canReceiveNegativeEventFromInputStream(temporalCondition)) {
				addTimeoutTransition(sourceState, destinationState);
			}
			else {
				addSearchFailedTransition(sourceState, destinationState);
			}
			return;
		}
		
		//positive chain
		if (iterativeTypes.contains(eventType)) {
			addIterateTransition(sourceState, destinationState, eventType, condition, temporalCondition);
		}
		else {
			addTakeTransition(sourceState, destinationState, eventType, condition, temporalCondition);
		}
	}
	
	private boolean canReceiveNegativeEventFromInputStream(EventTemporalPositionCondition negativeTypeCondition) {
		List<EventType> succeedingEventTypes = negativeTypeCondition.getSucceedingEventTypes();
		if (succeedingEventTypes.isEmpty()) {
			return true;
		}
		List<EventType> positiveEvents = evaluationOrder.getPositiveEvaluationOrder();
		for (EventType eventType : succeedingEventTypes) {
			if (positiveEvents.contains(eventType)) {
				return false;
			}
		}
		List<EventType> negativeEvents = evaluationOrder.getInverseNegativeEvaluationOrder();
		int targetEventIndex = negativeEvents.indexOf(negativeTypeCondition.getTargetEventType());
		for (int i = 0; i < targetEventIndex; ++i) {
			if (succeedingEventTypes.contains(negativeEvents.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	private List<EventType> getPositivePrecedingEvents(EventTemporalPositionCondition temporalCondition) {
		List<EventType> result = new ArrayList<EventType>();
		List<EventType> negativeEvents = evaluationOrder.getInverseNegativeEvaluationOrder();
		for (EventType eventType : temporalCondition.getPrecedingEventTypes()) {
			if (!negativeEvents.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}
	
	private List<EventType> getPositiveSucceedingEvents(EventTemporalPositionCondition temporalCondition) {
		List<EventType> result = new ArrayList<EventType>();
		List<EventType> negativeEvents = evaluationOrder.getInverseNegativeEvaluationOrder();
		for (EventType eventType : temporalCondition.getSucceedingEventTypes()) {
			if (!negativeEvents.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}

	private List<EventType> getPositiveDependingEvents(EventType eventType) {
		List<EventType> result = new ArrayList<EventType>();
		List<EventType> negativeEvents = evaluationOrder.getInverseNegativeEvaluationOrder();
		CNFCondition conditionForType = fullCondition.getConditionForType(eventType, false);
		for (AtomicCondition atomicCondition : conditionForType.getAtomicConditions()) {
			for (EventType currentEventType : atomicCondition.getEventTypes()) {
				if (currentEventType != eventType && !negativeEvents.contains(currentEventType)) {
					result.add(currentEventType);
				}
			}
		}
		return result;
	}
	
	private Transition getOutgoingPositiveMatchTransition(NFAState state, List<EventType> positiveEvents) {
		for (Transition transition : state.getOutgoingTransitions()) {
			if (transition.isMatchTransition() && positiveEvents.contains(transition.getEventType())) {
				return transition; //for now, we assume there is only one such transition
			}
		}
		return null;
	}
	
	private void addTimeoutStateBeforeFinalState() {
		NFAState timeoutState = new NFAState("Timeout State");
		replaceState(finalState, timeoutState);
		addTimeoutTransition(timeoutState, finalState);
	}
	
	private void makeLastTransitionHighPriority(NFAState state) {
		List<Transition> stateTransitions = state.getOutgoingTransitions();
		if (stateTransitions.isEmpty()) {
			return;
		}
		Transition transition = stateTransitions.remove(stateTransitions.size() - 1);
		stateTransitions.add(0, transition);
	}
	
	@Override
	protected void addTimeoutTransition(NFAState sourceState, NFAState destinationState) {
		super.addTimeoutTransition(sourceState, destinationState);
		makeLastTransitionHighPriority(sourceState);//timeout should always be highest priority
	}

	@Override
	public String getStructureSummary() {
		return evaluationOrder.toString();
	}
}
