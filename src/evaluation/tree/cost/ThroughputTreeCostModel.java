package sase.evaluation.tree.cost;

import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.elements.node.InternalNode;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.simulator.Environment;

public class ThroughputTreeCostModel implements ITreeCostModel {

	@Override
	public Double getCost(Node root) {
		return internalGetCost(root);
	}
	
	private Double internalGetCost(Node root) {
		if (root instanceof LeafNode) {
			return getLeafNodeCost((LeafNode)root);
		}
		if (root instanceof InternalNode) {
			return getInternalNodeCost((InternalNode)root);
		}
		return null;
		
	}
	
	protected Double getCardinality(Node root) {
		if (root instanceof LeafNode) {
			return getLeafNodeCardinality((LeafNode)root);
		}
		if (root instanceof InternalNode) {
			return getInternalNodeCardinality((InternalNode)root);
		}
		return null;
	}
	
	private Double getInternalNodeCost(InternalNode node) {
		Double leftSonCost = internalGetCost(node.getLeftChild());
		Double rightSonCost = internalGetCost(node.getRightChild());
		return leftSonCost + rightSonCost + getInternalNodeCardinality(node);
	}
	
	private Double getLeafNodeCost(LeafNode node) {
		return getLeafNodeCardinality(node);
	}

	private Double getInternalNodeCardinality(InternalNode node) {
		Double leftSonCardinality = getCardinality(node.getLeftChild());
		Double rightSonCardinality = getCardinality(node.getRightChild());
		return leftSonCardinality * rightSonCardinality * node.getNodeCondition().getSelectivity();
	}

	private Double getLeafNodeCardinality(LeafNode node) {
		int eventRate = Environment.getEnvironment().getEventRateEstimator().getEventRateEstimate(node.getEventType());
		if (node.isIterated()) {
			eventRate = new Double(Math.pow(2, eventRate)).intValue();
		}
		Double result = eventRate * node.getNodeCondition().getSelectivity();
		return (result == null) ? 0 : result;
	}

}
