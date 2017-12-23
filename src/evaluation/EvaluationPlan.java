package evaluation;

import java.util.List;

import base.EventType;
import evaluation.tree.elements.Node;

public class EvaluationPlan {

	private List<EventType> evaluationOrder = null;
	private Node evaluationTree = null;
	
	public EvaluationPlan(EvaluationMechanismTypes type, List<EventType> evaluationOrder) {
		this(type, evaluationOrder, null);
	}
	
	public EvaluationPlan(EvaluationMechanismTypes type, Node evaluationTree) {
		this(type, null, evaluationTree);
	}
	
	public EvaluationPlan(EvaluationMechanismTypes type, List<EventType> evaluationOrder, Node evaluationTree) {
		switch (type) {
			case LAZY_CHAIN:
				this.evaluationOrder = evaluationOrder;
				break;
			case TREE:
				this.evaluationTree = evaluationTree;
				break;
			case EAGER:
			case LAZY_TREE:
			default:
				break;
		}
	}

	public List<EventType> getOrderRepresentation() {
		if (evaluationOrder == null) {
			throw new RuntimeException("Order representation requested from a tree-based plan.");
		}
		return evaluationOrder;
	}

	public Node getTreeRepresentation() {
		if (evaluationTree == null) {
			throw new RuntimeException("Tree representation requested from an order-based plan.");
		}
		return evaluationTree;
	}
	
	@Override
	public String toString() {
		if (evaluationOrder != null) {
			return evaluationOrder.toString();
		}
		return evaluationTree.toString();
	}
}
