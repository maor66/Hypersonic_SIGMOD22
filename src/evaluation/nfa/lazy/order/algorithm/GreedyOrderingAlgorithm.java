package sase.evaluation.nfa.lazy.order.algorithm;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IIncrementalOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;

public abstract class GreedyOrderingAlgorithm implements IIncrementalOrderingAlgorithm {

	protected abstract EventType selectNextEventType(CompositePattern compositePattern, ICostModel costModel,
										  List<EventType> prefix, List<EventType> remainingTypes);
	
	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		if (pattern.getType() != PatternOperatorTypes.SEQ && pattern.getType() != PatternOperatorTypes.AND_SEQ) {
			throw new RuntimeException("Only sequence/conjunction patterns order calculation is currently supported.");
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		List<EventType> availableTypes = new ArrayList<EventType>(pattern.getEventTypes());
		List<EventType> result = new ArrayList<EventType>();
		while (!availableTypes.isEmpty()) {
			EventType nextType = selectNextEventType(compositePattern, costModel, result, availableTypes);
			availableTypes.remove(nextType);
			result.add(nextType);
		}
		return result;
	}

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel, List<EventType> prefix) {
		if (pattern.getType() != PatternOperatorTypes.SEQ && pattern.getType() != PatternOperatorTypes.AND_SEQ) {
			throw new RuntimeException("Only sequence/conjunction patterns order calculation is currently supported.");
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		List<EventType> availableTypes = new ArrayList<EventType>(pattern.getEventTypes());
		if (prefix != null) {
			availableTypes.removeAll(prefix);
		}
		List<EventType> result = prefix == null ? new ArrayList<EventType>() : new ArrayList<EventType>(prefix);
		while (!availableTypes.isEmpty()) {
			EventType nextType = selectNextEventType(compositePattern, costModel, result, availableTypes);
			availableTypes.remove(nextType);
			result.add(nextType);
		}
		return result;
	}

}
