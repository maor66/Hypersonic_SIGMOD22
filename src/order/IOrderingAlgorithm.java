package order;

import java.util.List;

import base.EventType;
import order.cost.ICostModel;
import pattern.Pattern;

public interface IOrderingAlgorithm {

	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel);
	
}
