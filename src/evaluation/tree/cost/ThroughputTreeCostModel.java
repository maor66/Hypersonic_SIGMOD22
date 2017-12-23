package evaluation.tree.cost;

import evaluation.tree.ITreeCostModel;
import evaluation.tree.elements.InternalNode;
import evaluation.tree.elements.LeafNode;
import evaluation.tree.elements.Node;
import simulator.Environment;

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
		Double result = eventRate * node.getNodeCondition().getSelectivity();
		return (result == null) ? 0 : result;
	}

}
