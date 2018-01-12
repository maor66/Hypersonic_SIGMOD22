package evaluation.tree;

import evaluation.tree.elements.node.Node;

public interface ITreeCostModel {

	public Double getCost(Node root);
}
