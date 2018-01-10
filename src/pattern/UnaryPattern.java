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
	
	public static List<PatternOperatorType> getUnaryPatternTypes() {
		List<PatternOperatorType> result = new ArrayList<PatternOperatorType> ();
		result.add(PatternOperatorType.NOP);
		result.add(PatternOperatorType.NEG);
		result.add(PatternOperatorType.ITER);
		return result;
	}

	public UnaryPattern(PatternOperatorType operatorType, EventType eventType, Condition condition,
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
	protected PatternOperatorType[] getValidPatternTypes() {
		return getUnaryPatternTypes().toArray(new PatternOperatorType[0]);
	}

	@Override
	protected String getPatternCategoryName() {
		return "Unary Pattern";
	}
}
