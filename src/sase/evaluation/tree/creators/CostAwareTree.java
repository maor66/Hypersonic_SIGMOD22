package sase.evaluation.tree.creators;

import sase.evaluation.tree.elements.node.Node;

public class CostAwareTree {
	public Node root;
	public Double cost;
	
	public CostAwareTree(Node root, Double cost) {
		this.root = root;
		this.cost = cost;
	}
}