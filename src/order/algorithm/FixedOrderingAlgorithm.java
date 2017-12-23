package order.algorithm;

import java.util.List;

import base.EventType;
import order.IOrderingAlgorithm;
import order.cost.ICostModel;
import pattern.Pattern;

public class FixedOrderingAlgorithm implements IOrderingAlgorithm {

	private final List<EventType> evaluationOrder;
	
	public FixedOrderingAlgorithm(List<EventType> evaluationOrder) {
		this.evaluationOrder = evaluationOrder;
	}

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		return evaluationOrder;
	}

}
