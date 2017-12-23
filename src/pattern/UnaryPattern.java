package pattern;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.condition.Condition;

public class UnaryPattern extends Pattern {
	
	private static List<EventType> createListFromSingleEventType(EventType eventType) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		eventTypes.add(eventType);
		return eventTypes;
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
		return new PatternOperatorType[] {
				PatternOperatorType.NOP,
				PatternOperatorType.NEG,
				PatternOperatorType.ITER
		};
	}

	@Override
	protected String getPatternCategoryName() {
		return "Unary Pattern";
	}
}
