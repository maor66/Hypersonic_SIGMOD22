package sase.evaluation.plan;

import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.Pattern;

public class OrderEvaluationPlan extends EvaluationPlan {
	
	private final List<EventType> evaluationOrder;

	public OrderEvaluationPlan(List<EventType> evaluationOrder) {
		this(null, evaluationOrder, null);
	}
	
	public OrderEvaluationPlan(Pattern pattern, List<EventType> evaluationOrder, ICostModel orderCostModel) {
		this.evaluationOrder = evaluationOrder;
		this.cost = orderCostModel == null ? null : orderCostModel.getOrderCost(pattern, evaluationOrder);
	}

	@Override
	public List<EventType> getRepresentation() {
		return evaluationOrder;
	}
	
	@Override
	public String toString() {
		return evaluationOrder.toString();
	}

}
