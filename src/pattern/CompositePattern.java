package sase.pattern;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.Condition;

public class CompositePattern extends Pattern {

	private final List<Pattern> nestedPatterns;

	private static List<EventType> createEventListFromPatternList(List<Pattern> patterns) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		for (Pattern pattern : patterns) {
			for (EventType eventType : pattern.getEventTypes()) {
				if (!(eventTypes.contains(eventType))) {
					eventTypes.add(eventType);
				}
			}
		}
		return eventTypes;
	}

	public CompositePattern(PatternOperatorType operatorType, List<Pattern> nestedPatterns, Condition condition,
			long timeWindow) {
		super(operatorType, createEventListFromPatternList(nestedPatterns), condition, timeWindow);
		this.nestedPatterns = nestedPatterns;
	}

	@Override
	public boolean isActuallyComposite() {
		for (Pattern pattern : nestedPatterns) {
			if (pattern.getType() != PatternOperatorType.NOP) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected PatternOperatorType[] getValidPatternTypes() {
		return new PatternOperatorType[] { PatternOperatorType.SEQ, PatternOperatorType.AND_SEQ,
				PatternOperatorType.OR };
	}

	@Override
	protected String getPatternCategoryName() {
		return "Composite Pattern";
	}

	public List<Pattern> getNestedPatterns() {
		return nestedPatterns;
	}

	public List<List<EventType>> extractSequences(boolean positiveElementsOnly) {
		List<List<EventType>> sequences = new ArrayList<List<EventType>>();
		switch (getType()) {
		case SEQ:
			if (positiveElementsOnly) {
				List<EventType> positiveList = new ArrayList<EventType>();
				for (Pattern nestedPattern : nestedPatterns) {
					if (nestedPattern.getType() != PatternOperatorType.NEG) {
						positiveList.add(((UnaryPattern) nestedPattern).getEventType());
					}
				}
				sequences.add(positiveList);
			} else {
				sequences.add(getEventTypes());
			}
			break;
		case AND_SEQ:
		case OR:
			for (Pattern nestedPattern : nestedPatterns) {
				if (nestedPattern.getType() != PatternOperatorType.SEQ) {
					continue;// unary pattern
				}
				sequences.addAll(((CompositePattern) nestedPattern).extractSequences(positiveElementsOnly));
			}
			break;
		default:
			throw new RuntimeException(String.format("Unsupported pattern type %s", getType()));
		}
		return sequences;
	}

	private List<EventType> getEventTypesForUnaryPatternType(PatternOperatorType patternType) {
		List<EventType> result = new ArrayList<EventType>();
		for (Pattern nestedPattern : nestedPatterns) {
			if (nestedPattern.getType() == patternType) {
				result.add(((UnaryPattern) nestedPattern).getEventType());
			} else if (nestedPattern.getType() == PatternOperatorType.SEQ) {
				result.addAll(((CompositePattern) nestedPattern).getNegativeEventTypes());
			}
		}
		return result;
	}

	public List<EventType> getNegativeEventTypes() {
		return getEventTypesForUnaryPatternType(PatternOperatorType.NEG);
	}

	public List<EventType> getIterativeEventTypes() {
		return getEventTypesForUnaryPatternType(PatternOperatorType.ITER);
	}
	
	public CompositePattern getSubPattern(List<EventType> eventTypesForSubPattern) {
		List<Pattern> nestedPatternsForSubPattern = new ArrayList<Pattern>();
		for (Pattern pattern : nestedPatterns) {
			if (pattern instanceof UnaryPattern) {
				if (eventTypesForSubPattern.contains(((UnaryPattern)pattern).getEventType())) {
					nestedPatternsForSubPattern.add(pattern);
				}
			}
			else if (pattern instanceof CompositePattern) {
				nestedPatternsForSubPattern.add(((CompositePattern)pattern).getSubPattern(eventTypesForSubPattern));
			}
			else {
				throw new RuntimeException("Unsupported pattern type detected.");
			}
		}
		return new CompositePattern(getType(), nestedPatternsForSubPattern, 
									getSubCondition(eventTypesForSubPattern), getTimeWindow());
	}

}
