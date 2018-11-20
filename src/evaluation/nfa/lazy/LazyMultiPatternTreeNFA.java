package sase.evaluation.nfa.lazy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sase.base.EventType;
import sase.evaluation.IMultiPatternEvaluationMechanism;
import sase.evaluation.nfa.eager.elements.Instance;
import sase.evaluation.nfa.lazy.elements.multi.LazyMultiInstance;
import sase.evaluation.nfa.lazy.elements.multi.LazyMultiState;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.MultiPatternTreeEvaluationPlan;
import sase.multi.MultiPatternTree;
import sase.multi.MultiPatternTreeNode;
import sase.multi.MultiPlan;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.base.TrivialCondition;
import sase.pattern.condition.time.EventTemporalPositionCondition;

//TODO: as of now, negation and iteration are not supported
public class LazyMultiPatternTreeNFA extends LazyNFA implements IMultiPatternEvaluationMechanism {
	
	private final MultiPatternTree multiPatternTree;
	
	public LazyMultiPatternTreeNFA(List<Pattern> patterns, EvaluationPlan evaluationPlan) {
		super(new CompositePattern(PatternOperatorTypes.OR, patterns, new TrivialCondition(), patterns.get(0).getTimeWindow()));
		multiPatternTree = (MultiPatternTree)((MultiPatternTreeEvaluationPlan)evaluationPlan).getRepresentation();
	}

	@Override
	public String getStructureSummary() {
		return multiPatternTree.getRoot().toString();
	}

	@Override
	protected void initNFAStructure() {
		initialState = new LazyMultiState("Initial State", true, false, false, null);
		rejectingState = new LazyMultiState("Rejecting State", false, true, false, null);
		finalState = new LazyMultiState("Unused", null);
		recursiveBuildNFAStructure(multiPatternTree.getRoot(), (LazyMultiState)initialState, null);
		if (!states.contains(rejectingState)) {
			states.add(rejectingState);
		}
	}
	
	private LazyMultiState recursiveBuildNFAStructure(MultiPatternTreeNode currentNode, 
													  LazyMultiState existingState,
													  Set<EventType> usedEventTypes) {
		boolean isAccepting = (currentNode.getAcceptingPatternId() != null);
		LazyMultiState currentState = existingState != null ?
										existingState :
										new LazyMultiState(null, false, isAccepting, isAccepting, currentNode.getTimeWindow());
		if (!states.contains(currentState)) {
			states.add(currentState);
		}
		if (usedEventTypes == null) {
			usedEventTypes = new HashSet<EventType>();
		}
		if (currentState != initialState && (!currentState.isAccepting() || currentNode.hasChildren())) {
			addTimeoutTransition(currentState, rejectingState);
		}
		Set<EventType> eventTypesAcceptedByNextStates = new HashSet<EventType>();
		for (MultiPatternTreeNode childNode : currentNode.getAllChildren()) {
			LazyMultiState nextState = recursiveBuildNFAStructure(childNode, null, eventTypesAcceptedByNextStates);
			CNFCondition currentTakeCondition = new CNFCondition(childNode.getCondition());
			EventTemporalPositionCondition currentTakeTemporalCondition = removeTemporalCondition(currentTakeCondition);
			if (currentTakeTemporalCondition == null) {
				throw new RuntimeException("Fatal error: a node lacks a temporal condition");
			}
			addTakeTransition(currentState, nextState, childNode.getEventType(), 
							  currentTakeCondition, currentTakeTemporalCondition);
			usedEventTypes.add(childNode.getEventType());
		}
		for (EventType eventType : eventTypesAcceptedByNextStates) {
			addStoreTransition(currentState, eventType);
		}
		usedEventTypes.addAll(eventTypesAcceptedByNextStates);
		return currentState;
	}
	
	private EventTemporalPositionCondition removeTemporalCondition(CNFCondition condition) {
		EventTemporalPositionCondition result = null;
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			if (atomicCondition instanceof EventTemporalPositionCondition) {
				//we assume there is always exactly one such condition
				result = (EventTemporalPositionCondition)atomicCondition;
				break;
			}
		}
		if (result != null) {
			condition.removeAtomicCondition(result);
		}
		return result;
	}

	@Override
	protected Instance createInstance() {
		return new LazyMultiInstance(this, (LazyMultiState)initialState);
	}

	@Override
	public MultiPlan getMultiPlan() {
		return multiPatternTree;
	}

}
