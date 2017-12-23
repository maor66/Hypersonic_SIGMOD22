package sase.evaluation.nfa.lazy.elements;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;

public class EvaluationOrder {
	private final List<EventType> fullEvaluationOrder;
	private final List<EventType> negativeTypes;
	private final List<EventType> iterativeTypes;
	
	public EvaluationOrder(Pattern pattern, List<EventType> evaluationOrder) {
		fullEvaluationOrder = new ArrayList<EventType>(evaluationOrder);
		CompositePattern compositePattern = (CompositePattern)pattern;
		negativeTypes = compositePattern.getNegativeEventTypes();
		iterativeTypes = compositePattern.getIterativeEventTypes();
		moveIterativeTypesToEnd();
	}
	
	private void moveIterativeTypesToEnd() {
		List<EventType> iterativeTypesByOrder = new ArrayList<EventType>();
		for (EventType eventType : fullEvaluationOrder) {
			if (iterativeTypes.contains(eventType)) {
				iterativeTypesByOrder.add(eventType);
			}
		}
		for (EventType eventType : iterativeTypesByOrder) {
			fullEvaluationOrder.remove(eventType);
			fullEvaluationOrder.add(eventType);
		}
	}
	
	public List<EventType> getFullEvaluationOrder() {
		return fullEvaluationOrder;
	}
	
	public List<EventType> getPositiveEvaluationOrder() {
		List<EventType> result = new ArrayList<EventType>();
		for (EventType eventType : fullEvaluationOrder) {
			if (!negativeTypes.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}
	
	public List<EventType> getInverseNegativeEvaluationOrder() {
		List<EventType> result = new ArrayList<EventType>();
		for (EventType eventType : fullEvaluationOrder) {
			if (negativeTypes.contains(eventType)) {
				//add each element at the beginning to get inverse order
				result.add(0, eventType);
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return fullEvaluationOrder.toString();
	}
}