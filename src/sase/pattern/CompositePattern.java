package sase.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.CNFCondition;

public class CompositePattern extends Pattern {

	private final List<Pattern> nestedPatterns;

	private static List<EventType> createEventListFromPatternList(List<Pattern> patterns) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		for (Pattern pattern : patterns) {
			for (EventType eventType : pattern.getEventTypes()) {
				//NOTE: as of now, it means that we do not support the appearance of an event type more than once in a sase.pattern
				if (!(eventTypes.contains(eventType))) {
					eventTypes.add(eventType);
				}
			}
		}
		return eventTypes;
	}

	public CompositePattern(PatternOperatorTypes operatorType, List<Pattern> nestedPatterns, Condition condition,
			long timeWindow) {
		super(operatorType, createEventListFromPatternList(nestedPatterns), condition, timeWindow);
		this.nestedPatterns = nestedPatterns;
	}

	@Override
	public boolean isActuallyComposite() {
		for (Pattern pattern : nestedPatterns) {
			if (pattern.getType() != PatternOperatorTypes.NOP) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected PatternOperatorTypes[] getValidPatternTypes() {
		return new PatternOperatorTypes[] { PatternOperatorTypes.SEQ, PatternOperatorTypes.AND_SEQ,
				PatternOperatorTypes.OR };
	}

	@Override
	protected String getPatternCategoryName() {
		return "Composite Pattern";
	}

	public List<Pattern> getNestedPatterns() {
		return nestedPatterns;
	}
	
	public CNFCondition getCNFCondition() {
		if (!(getCondition() instanceof CNFCondition)) {
			throw new RuntimeException("Illegal condition");
		}
		return (CNFCondition)getCondition();
	}

	public List<List<EventType>> extractSequences(boolean positiveElementsOnly) {
		List<List<EventType>> sequences = new ArrayList<List<EventType>>();
		switch (getType()) {
		case SEQ:
			if (positiveElementsOnly) {
				List<EventType> positiveList = new ArrayList<EventType>();
				for (Pattern nestedPattern : nestedPatterns) {
					if (nestedPattern.getType() != PatternOperatorTypes.NEG) {
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
				if (nestedPattern.getType() != PatternOperatorTypes.SEQ) {
					continue;// unary sase.pattern
				}
				sequences.addAll(((CompositePattern) nestedPattern).extractSequences(positiveElementsOnly));
			}
			break;
		default:
			throw new RuntimeException(String.format("Unsupported sase.pattern type %s", getType()));
		}
		return sequences;
	}

	private boolean shouldIncludePattern(PatternOperatorTypes requiredPatternType, PatternOperatorTypes actualPatternType) {
		if (requiredPatternType == actualPatternType) {
			return true;
		}
		return (requiredPatternType == PatternOperatorTypes.NONE &&
				UnaryPattern.getUnaryPatternTypes().contains(actualPatternType));
	}
	
	private List<EventType> getEventTypesForPatternType(PatternOperatorTypes patternType) {
		List<EventType> result = new ArrayList<EventType>();
		for (Pattern nestedPattern : nestedPatterns) {
			if (shouldIncludePattern(patternType, nestedPattern.getType())) {
				result.add(((UnaryPattern) nestedPattern).getEventType());
			} else if (nestedPattern.getType() == PatternOperatorTypes.SEQ) {
				result.addAll(((CompositePattern) nestedPattern).getNegativeEventTypes());
			}
		}
		return result;
	}

	public List<EventType> getNegativeEventTypes() {
		return getEventTypesForPatternType(PatternOperatorTypes.NEG);
	}

	public List<EventType> getIterativeEventTypes() {
		return getEventTypesForPatternType(PatternOperatorTypes.ITER);
	}
	
	public CompositePattern getSubPattern(Collection<EventType> eventTypesForSubPattern) {
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
				throw new RuntimeException("Unsupported sase.pattern type detected.");
			}
		}
		return new CompositePattern(getType(), nestedPatternsForSubPattern, 
									getSubCondition(eventTypesForSubPattern), getTimeWindow());
	}
	
	public CompositePattern getFilteredSubPattern(List<EventType> eventTypesToExclude) {
		if (eventTypesToExclude.isEmpty()) {
			return this;
		}
		List<EventType> allEventTypes = getEventTypes();
		List<EventType> eventTypesForSubPattern = new ArrayList<EventType>();
		for (EventType eventType : allEventTypes) {
			if (!eventTypesToExclude.contains(eventType)) {
				eventTypesForSubPattern.add(eventType);
			}
		}
		return getSubPattern(eventTypesForSubPattern);
	}
	
	@Override
	public String toString() {
		return String.format("Pattern %d over %s", getPatternId(), getEventTypes());
	}

}
