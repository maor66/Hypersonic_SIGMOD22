package sase.pattern;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.Condition;

public class UnaryPattern extends Pattern {
	
	private static List<EventType> createListFromSingleEventType(EventType eventType) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		eventTypes.add(eventType);
		return eventTypes;
	}
	
	public static List<PatternOperatorTypes> getUnaryPatternTypes() {
		List<PatternOperatorTypes> result = new ArrayList<PatternOperatorTypes> ();
		result.add(PatternOperatorTypes.NOP);
		result.add(PatternOperatorTypes.NEG);
		result.add(PatternOperatorTypes.ITER);
		return result;
	}

	public UnaryPattern(PatternOperatorTypes operatorType, EventType eventType, Condition condition,
			long timeWindow) {
		super(operatorType, createListFromSingleEventType(eventType), condition, timeWindow);
	}
	
	public EventType getEventType() {
		return getEventTypes().get(0);
	}
	
	@Override
	public boolean isActuallyComposite() {
		return false;
	}

	@Override
	protected PatternOperatorTypes[] getValidPatternTypes() {
		return getUnaryPatternTypes().toArray(new PatternOperatorTypes[0]);
	}

	@Override
	protected String getPatternCategoryName() {
		return "Unary Pattern";
	}
}
