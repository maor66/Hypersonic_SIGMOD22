package pattern.creation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import base.EventType;
import pattern.CompositePattern;
import pattern.EventTypesManager;
import pattern.Pattern;
import pattern.UnaryPattern;
import pattern.Pattern.PatternOperatorType;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;
import specification.ConditionSpecification;

public class CompositePatternCreator extends PatternCreator {

	private final String[][][] patternSpecification;
	private final ConditionSpecification[] correlations;
	private final List<String> negatedEvents;
	private final List<String> iteratedEvents;
	private final long timeWindow;
	private final CNFCondition mainCondition;
	
	public CompositePatternCreator(String[][][] patternSpecification,
												   ConditionSpecification[] correlations,
												   String[] negatedEvents,
												   String[] iteratedEvents,
												   long timeWindow) {
		this.patternSpecification = patternSpecification;
		this.correlations = correlations;
		this.negatedEvents = Arrays.asList(negatedEvents);
		this.iteratedEvents = Arrays.asList(iteratedEvents);
		this.timeWindow = timeWindow;
		this.mainCondition = createMainCondition();
	}
	
	private boolean isEventNegated(String eventName) {
		return negatedEvents.contains(eventName);
	}
	
	private boolean isEventIterated(String eventName) {
		return iteratedEvents.contains(eventName);
	}
	
	private CNFCondition createMainCondition() {
    	List<AtomicCondition> atomicConditions = new ArrayList<AtomicCondition>();
    	for (ConditionSpecification specification : correlations) {
    		atomicConditions.addAll(specification.createConditions());
		}
    	return new CNFCondition(atomicConditions);
    }
	
	private CNFCondition getSubConditionForPatterns(List<Pattern> subPatterns) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		for (Pattern pattern : subPatterns) {
			eventTypes.addAll(pattern.getEventTypes());
		}
		return mainCondition.getConditionForTypes(eventTypes, true);
	}
	
	private Pattern createPrimitivePattern(String eventName) {
		PatternOperatorType patternType;
		if (isEventNegated(eventName)) {
			patternType = PatternOperatorType.NEG;
		}
		else if (isEventIterated(eventName)) {
			patternType = PatternOperatorType.ITER;
		}
		else {
			patternType = PatternOperatorType.NOP;
		}
		EventType eventType = EventTypesManager.getInstance().getTypeByName(eventName);
		return new UnaryPattern(patternType, eventType, mainCondition.getConditionForType(eventType, true), timeWindow);
	}
	
	private Pattern createPrimitiveOrSequencePattern(String[] subPatternSpecification) {
		if (subPatternSpecification.length == 1) {
			//primitive pattern
			return createPrimitivePattern(subPatternSpecification[0]);
		}
		//sequence pattern
		List<Pattern> primitivePatterns = new ArrayList<Pattern>();
		for (String eventName : subPatternSpecification) {
			primitivePatterns.add(createPrimitivePattern(eventName));
		}
		return new CompositePattern(PatternOperatorType.SEQ, primitivePatterns, 
									getSubConditionForPatterns(primitivePatterns), timeWindow);
	}
	
	private Pattern createSequenceOrConjunctionPattern(String[][] subPatternSpecification) {
		if (subPatternSpecification.length == 1) {
			//primitive or sequence pattern
			return createPrimitiveOrSequencePattern(subPatternSpecification[0]);
		}
		//conjunction pattern
		List<Pattern> primitiveOrSequencePatterns = new ArrayList<Pattern>();
		for (String[] sequenceSpecification : subPatternSpecification) {
			primitiveOrSequencePatterns.add(createPrimitiveOrSequencePattern(sequenceSpecification));
		}
		return new CompositePattern(PatternOperatorType.AND_SEQ, primitiveOrSequencePatterns, 
									getSubConditionForPatterns(primitiveOrSequencePatterns), timeWindow);
	}
	
	@Override
	public Pattern createPattern() {
		if (patternSpecification.length == 1) {
			//single conjunction pattern
			return createSequenceOrConjunctionPattern(patternSpecification[0]);
		}
		//disjunction pattern
		List<Pattern> subPatterns = new ArrayList<Pattern>();
		for (String[][] andSpecification : patternSpecification) {
			subPatterns.add(createSequenceOrConjunctionPattern(andSpecification));
		}
		return new CompositePattern(PatternOperatorType.OR, subPatterns, mainCondition, timeWindow);
	}
}
