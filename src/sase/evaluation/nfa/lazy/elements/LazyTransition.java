package sase.evaluation.nfa.lazy.elements;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.lazy.optimizations.BufferFilter;
import sase.evaluation.nfa.lazy.optimizations.BufferGrouper;
import sase.evaluation.nfa.lazy.optimizations.BufferPreprocessor;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.iteration.eager.IterationTriggerCondition;
import sase.pattern.condition.time.EventTemporalPositionCondition;

public class LazyTransition extends Transition {
	
	private final EventTemporalPositionCondition temporalCondition;
	private final LazyTransitionType type;
	private final List<BufferPreprocessor> preprocessors;
	private IterationTriggerCondition triggerCondition;

	public LazyTransition(LazyTransitionType type, NFAState source, NFAState destination, Transition.Action action,
						  EventType eventType, Condition condition, EventTemporalPositionCondition temporalCondition) {
		super(source, destination, action, eventType, condition, false);
		this.temporalCondition = temporalCondition;
		this.type = type;
		preprocessors = new ArrayList<BufferPreprocessor>();
		if (MainConfig.debugMode) {
			System.out.println(String.format("Created %s", this));
		}
		
		if (action != Action.ITERATE) {
			return;
		}
		
		BufferFilter filter = new BufferFilter(condition);
		if (!filter.isTrivial()) {
			preprocessors.add(filter);
		}
		BufferGrouper grouper = new BufferGrouper(condition);
		if (!grouper.isTrivial()) {
			preprocessors.add(grouper);
		}
		CNFCondition cnfCondition = (CNFCondition)condition;
		triggerCondition = null;
		for (AtomicCondition atomicCondition : cnfCondition.getAtomicConditions()) {
			if (atomicCondition instanceof IterationTriggerCondition) {
				triggerCondition = (IterationTriggerCondition)atomicCondition;
			}
		}
	}
	
	public Event getActualPrecedingEvent(List<Event> events) {
		return temporalCondition == null ? null : temporalCondition.getActualPrecedingEvent(events);
	}
	
	public Event getActualSucceedingEvent(List<Event> events) {
		return temporalCondition == null ? null : temporalCondition.getActualSucceedingEvent(events);
	}

	public LazyTransitionType getType() {
		return type;
	}

	public List<BufferPreprocessor> getBufferPreprocessors() {
		return preprocessors;
	}

	public boolean shouldCloneInstance() {
		return (isMatchTransition() && !getDestination().isRejecting());
	}
	
	public boolean canProceedToBufferEvaluation() {
		return (getAction() != Transition.Action.STORE);
	}
	
	public void enableCache() {
		if (triggerCondition != null) {
			triggerCondition.enableCache();
		}
	}

	public void disableCache() {
		if (triggerCondition != null) {
			triggerCondition.disableCache();
		}
	}
	
	@Override
	public boolean isMatchTransition() {
		return (type == LazyTransitionType.REGULAR && super.isMatchTransition());
	}
	
	@Override
	public boolean verifyCondition(List<Event> events) {
		return super.verifyCondition(events) && (temporalCondition == null || temporalCondition.verify(events));
	}
	public boolean verifyConditionWithTemporalConditionFirst(List<Event> events)
	{
		return (temporalCondition == null || temporalCondition.verify(events)) &&  super.verifyCondition(events);
	}
	
	@Override
	public String toString() {
		String transitionAsString = super.toString();
		if (type == LazyTransitionType.REGULAR) {
			return transitionAsString;
		}
		return String.format("%s %s", type, transitionAsString);
	}

}
