package sase.pattern;

import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.Condition;

public class SimplePattern extends Pattern {

	public SimplePattern(PatternOperatorType operatorType, List<EventType> eventTypes, Condition condition,
			long timeWindow) {
		super(operatorType, eventTypes, condition, timeWindow);
	}
	
	@Override
	public boolean isActuallyComposite() {
		return false;
	}

	@Override
	protected PatternOperatorType[] getValidPatternTypes() {
		return new PatternOperatorType[] {
				PatternOperatorType.OLD_AND,
				PatternOperatorType.OLD_SEQ
		};
	}

	@Override
	protected String getPatternCategoryName() {
		return "Simple Pattern";
	}

}
