package sase.order.algorithm;

import java.util.List;

import sase.base.EventType;
import sase.order.IOrderingAlgorithm;
import sase.order.cost.ICostModel;
import sase.pattern.Pattern;

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
