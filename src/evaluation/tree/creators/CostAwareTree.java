package evaluation.tree.creators;

import evaluation.tree.elements.Node;

public class CostAwareTree {
	public Node root;
	public Double cost;
	
	public CostAwareTree(Node root, Double cost) {
		this.root = root;
		this.cost = cost;
	}
}