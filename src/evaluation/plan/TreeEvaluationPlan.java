package sase.evaluation.plan;

import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.elements.node.Node;

public class TreeEvaluationPlan extends EvaluationPlan {
	
	private final Node evaluationTree;

	public TreeEvaluationPlan(Node evaluationTree) {
		this.evaluationTree = evaluationTree;
	}
	
	public TreeEvaluationPlan(Node evaluationTree, ITreeCostModel treeCostModel) {
		this(evaluationTree);
		this.cost = treeCostModel.getCost(evaluationTree);
	}

	@Override
	public Node getRepresentation() {
		return evaluationTree;
	}
	
	@Override
	public String toString() {
		return evaluationTree.toString();
	}

}
