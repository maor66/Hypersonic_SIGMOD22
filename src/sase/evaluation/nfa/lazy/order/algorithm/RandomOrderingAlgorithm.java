package sase.evaluation.nfa.lazy.order.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.Pattern;

public class RandomOrderingAlgorithm implements IOrderingAlgorithm {

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		List<EventType> result = new ArrayList<EventType>(pattern.getEventTypes());
		Collections.shuffle(result);
		return result;
	}

}
