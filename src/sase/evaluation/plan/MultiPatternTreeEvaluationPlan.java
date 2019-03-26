package sase.evaluation.plan;

import java.util.Objects;

import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.multi.MultiPatternTree;

public class MultiPatternTreeEvaluationPlan extends EvaluationPlan {

	private MultiPatternTree multiPatternTree;
	
	public MultiPatternTreeEvaluationPlan(MultiPatternTree multiPatternTree, ICostModel orderCostModel) {
		this.multiPatternTree = multiPatternTree;
		this.cost = orderCostModel.getMPTCost(multiPatternTree);
	}

	@Override
	public Object getRepresentation() {
		return multiPatternTree;
	}
	
	@Override
	public String toString() {
		return multiPatternTree.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MultiPatternTreeEvaluationPlan)) {
			return false;
		}
		MultiPatternTreeEvaluationPlan multiPatternTreeEvaluationPlan = (MultiPatternTreeEvaluationPlan)other;
		return getRepresentation().equals(multiPatternTreeEvaluationPlan.getRepresentation());
	}

	@Override
    public int hashCode() {
        return Objects.hash(multiPatternTree);
    }

}
