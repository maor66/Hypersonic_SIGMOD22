package sase.evaluation;

import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.tree.elements.node.Node;
import sase.pattern.Pattern;

public class EvaluationPlan {

	private List<EventType> evaluationOrder = null;
	private Node evaluationTree = null;
	private HashMap<Pattern, EvaluationPlan> nestedPlans = null;
	
	public EvaluationPlan(List<EventType> evaluationOrder) {
		this(EvaluationMechanismTypes.LAZY_CHAIN, null, evaluationOrder, null);
	}
	
	public EvaluationPlan(Node evaluationTree) {
		this(EvaluationMechanismTypes.TREE, null, null, evaluationTree);
	}
	
	public EvaluationPlan(EvaluationMechanismTypes type, HashMap<Pattern, EvaluationPlan> nestedPlans) {
		this(type, nestedPlans, null, null);
	}
	
	public EvaluationPlan(EvaluationMechanismTypes type, HashMap<Pattern, EvaluationPlan> nestedPlans,
						  List<EventType> evaluationOrder, Node evaluationTree) {
		if (nestedPlans != null) {
			this.nestedPlans = nestedPlans;
			return;
		}
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
			throw new RuntimeException("Order representation requested from a tree-based or nested plan.");
		}
		return evaluationOrder;
	}

	public Node getTreeRepresentation() {
		if (evaluationTree == null) {
			throw new RuntimeException("Tree representation requested from an order-based or nested plan.");
		}
		return evaluationTree;
	}
	
	public HashMap<Pattern, EvaluationPlan> getNestedPlans() {
		if (nestedPlans == null) {
			throw new RuntimeException("Nested plans requested from a flat plan.");
		}
		return nestedPlans;
	}
	
	private String nestedPlansToString() {
		String result = "List of nested plans:\n";
		for (Pattern pattern : nestedPlans.keySet()) {
			result += String.format("Pattern: %s\nPlan: %s\n\n", pattern, nestedPlans.get(pattern));
		}
		return result;
	}
	
	@Override
	public String toString() {
		if (evaluationOrder != null) {
			return evaluationOrder.toString();
		}
		if (evaluationTree != null) {
			return evaluationTree.toString();
		}
		return nestedPlansToString();
	}
}
