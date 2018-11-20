package sase.pattern;

import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.Condition;

public class SimplePattern extends Pattern {

	public SimplePattern(PatternOperatorTypes operatorType, List<EventType> eventTypes, Condition condition,
			long timeWindow) {
		super(operatorType, eventTypes, condition, timeWindow);
	}
	
	@Override
	public boolean isActuallyComposite() {
		return false;
	}

	@Override
	protected PatternOperatorTypes[] getValidPatternTypes() {
		return new PatternOperatorTypes[] {
				PatternOperatorTypes.OLD_AND,
				PatternOperatorTypes.OLD_SEQ
		};
	}

	@Override
	protected String getPatternCategoryName() {
		return "Simple Pattern";
	}

}
