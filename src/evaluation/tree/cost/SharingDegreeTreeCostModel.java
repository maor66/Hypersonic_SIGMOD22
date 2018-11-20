package sase.evaluation.tree.cost;

import java.util.HashSet;
import java.util.Set;

import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.elements.node.InternalNode;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternMultiTree;

public class SharingDegreeTreeCostModel implements ITreeCostModel {
	
	@Override
	public Double getCost(Node root) {
		return (double)root.getNodesInSubTree().size();
	}
	
	@Override
	public Double getMPMTCost(MultiPatternMultiTree multiPatternMultiTree) {
		return (double)countMPMTNodes(multiPatternMultiTree);
	}

	private int countMPMTNodes(MultiPatternMultiTree multiPatternMultiTree) {
		Set<Node> seenNodes = new HashSet<Node>();
		for (Node node : multiPatternMultiTree.getAcceptingNodes().values()) {
			collectNodes(node, seenNodes);
		}
		return seenNodes.size();
	}
	
	private void collectNodes(Node currentNode, Set<Node> nodes) {
		nodes.add(currentNode);
		if (!(currentNode instanceof InternalNode)) {
			return;
		}
		InternalNode internalNode = (InternalNode)currentNode;
		collectNodes(internalNode.getLeftChild(), nodes);
		collectNodes(internalNode.getRightChild(), nodes);
	}
	
}
