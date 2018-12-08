package sase.evaluation.nfa.lazy.order.cost;

import java.util.List;

import sase.base.EventType;
import sase.multi.MultiPatternTree;
import sase.multi.MultiPatternTreeNode;
import sase.pattern.Pattern;

public class SharingDegreeCostModel implements ICostModel {

	@Override
	public Double getOrderCost(Pattern pattern, List<EventType> order) {
		return (double)(order.size() + 1);
	}

	@Override
	public Double getMPTCost(MultiPatternTree mpt) {
		return (double)recursiveCountNodes(mpt.getRoot());
	}

	private int recursiveCountNodes(MultiPatternTreeNode node) {
		Integer count = 1;
		for (MultiPatternTreeNode child : node.getAllChildren()) {
			count += recursiveCountNodes(child);
		}
		return count;
	}

}
