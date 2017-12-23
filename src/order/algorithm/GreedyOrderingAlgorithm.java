package sase.order.algorithm;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.order.IOrderingAlgorithm;
import sase.order.cost.ICostModel;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorType;

public abstract class GreedyOrderingAlgorithm implements IOrderingAlgorithm {

	protected abstract EventType selectNextEventType(CompositePattern compositePattern, ICostModel costModel,
										  List<EventType> prefix, List<EventType> remainingTypes);
	
	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		if (pattern.getType() != PatternOperatorType.SEQ && pattern.getType() != PatternOperatorType.AND_SEQ) {
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

}
