package sase.evaluation.plan;

import java.util.Objects;

import sase.evaluation.tree.ITreeCostModel;
import sase.multi.MultiPatternMultiTree;

public class MultiPatternMultiTreeEvaluationPlan extends EvaluationPlan {

	private MultiPatternMultiTree multiPatternMultiTree;
	
	public MultiPatternMultiTreeEvaluationPlan(MultiPatternMultiTree multiPatternMultiTree, ITreeCostModel treeCostModel) {
		this.multiPatternMultiTree = multiPatternMultiTree;
		this.cost = treeCostModel.getMPMTCost(multiPatternMultiTree);
	}

	@Override
	public Object getRepresentation() {
		return multiPatternMultiTree;
	}
	
	@Override
	public String toString() {
		return multiPatternMultiTree.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MultiPatternMultiTreeEvaluationPlan)) {
			return false;
		}
		MultiPatternMultiTreeEvaluationPlan multiPatternMultiTreeEvaluationPlan = (MultiPatternMultiTreeEvaluationPlan)other;
		return getRepresentation().equals(multiPatternMultiTreeEvaluationPlan.getRepresentation());
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(multiPatternMultiTree);
    }

}
