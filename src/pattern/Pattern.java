package sase.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.CNFCondition;

/**
 * This class represents a single pattern whose instances the user is willing to detect.
 */
public abstract class Pattern {

	public enum PatternOperatorTypes {
		NONE,
		
		OLD_SEQ, 
		OLD_AND,
		
		NOP,
		NEG,
		ITER,

		SEQ,
		AND_SEQ,
		OR
	}
	
	private static Long patternIdCounter = (long) 0;
	
	private final Long patternId;
	private final PatternOperatorTypes type;
	private final Condition condition;
	private final long timeWindow;
	
	private List<EventType> eventTypes;//NOTE: for sequence patterns this also defines the desired order
	
	public Pattern(PatternOperatorTypes operatorType, 
				   List<EventType> eventTypes, 
				   Condition condition, 
				   long timeWindow) {
		this.patternId = patternIdCounter++;
		this.type = operatorType;
		this.condition = condition;
		this.timeWindow = timeWindow;
		
		this.eventTypes = eventTypes;
		validatePatternType();
	}

	public Long getPatternId() {
		return patternId;
	}

	public PatternOperatorTypes getType() {
		return type;
	}

	public List<EventType> getEventTypes() {
		return eventTypes;
	}

	public Condition getCondition() {
		return condition;
	}
	
	public long getTimeWindow() {
		return timeWindow;
	}
	
	private void validatePatternType()
	{
		for (PatternOperatorTypes patternOperatorType : getValidPatternTypes()) {
			if (patternOperatorType == getType()) {
				return;
			}
		}
		throw new RuntimeException(String.format("Illegal operator for pattern of type %s: %s", 
												 getPatternCategoryName(), getType()));
	}
	
	protected Condition getSubCondition(Collection<EventType> subSetOfEvents) {
		List<EventType> excludedEvents = new ArrayList<EventType>();
		for (EventType eventType : eventTypes) {
			if (!subSetOfEvents.contains(eventType)) {
				excludedEvents.add(eventType);
			}
		}
		CNFCondition mainCondition = (CNFCondition)condition;
		return mainCondition.getConditionExcludingTypes(excludedEvents);
	}
	
	public abstract boolean isActuallyComposite();
	protected abstract PatternOperatorTypes[] getValidPatternTypes();
	protected abstract String getPatternCategoryName();
}