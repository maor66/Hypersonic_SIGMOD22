package sase.evaluation.tree.cost;

import java.util.HashMap;

import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.elements.node.InternalNode;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternMultiTree;

public class ThroughputTreeCostModel implements ITreeCostModel {

	private HashMap<Node, Double> cache = new HashMap<Node, Double>();
	private boolean enableCache = false;
	
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
		Double cardinality = null;
		if (enableCache) {
			cardinality = cache.get(root);
			if (cardinality != null) {
				return cardinality;
			}
		}
		if (root instanceof LeafNode) {
			cardinality = getLeafNodeCardinality((LeafNode)root);
		}
		if (root instanceof InternalNode) {
			cardinality = getInternalNodeCardinality((InternalNode)root);
		}
		if (enableCache) {
			cache.put(root, cardinality);
		}
		return cardinality;
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
		double eventRate = node.getEventType().getRate();
		if (node.isIterated()) {
			eventRate = new Double(Math.pow(2, eventRate)).intValue();
		}
		Double result = eventRate * node.getNodeCondition().getSelectivity();
		return (result == null) ? 0 : result;
	}

	@Override
	public Double getMPMTCost(MultiPatternMultiTree multiPatternMultiTree) {
		if (multiPatternMultiTree.isSharingEnabled()) {
			return getSharedMPMTCost(multiPatternMultiTree);
		}
		HashMap<Long, Node> singlePatternTrees = multiPatternMultiTree.getSinglePatternTrees();
		Double cost = 0.0;
		for (Node node : singlePatternTrees.values()) {
			cost += getCost(node);
		}
		return cost;
	}

	private Double getSharedMPMTCost(MultiPatternMultiTree multiPatternMultiTree) {
		enableCache = true;
		for (Node node : multiPatternMultiTree.getAcceptingNodes().values()) {
			internalGetCost(node);//trigger cardinality calculation for all nodes
		}
		Double cost = 0.0;
		for (Double nodeCost : cache.values()) {
			cost += nodeCost;
		}
		cache.clear();
		enableCache = false;
		return cost;
	}

}
