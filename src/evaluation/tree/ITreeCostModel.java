package sase.evaluation.tree;

import sase.evaluation.tree.elements.node.Node;

public interface ITreeCostModel {

	public Double getCost(Node root);
}
