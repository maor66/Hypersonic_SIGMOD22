package order.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.EventType;
import order.IOrderingAlgorithm;
import order.cost.ICostModel;
import pattern.Pattern;

public class RandomOrderingAlgorithm implements IOrderingAlgorithm {

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		List<EventType> result = new ArrayList<EventType>(pattern.getEventTypes());
		Collections.shuffle(result);
		return result;
	}

}
