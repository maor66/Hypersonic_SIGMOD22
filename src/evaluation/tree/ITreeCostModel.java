package sase.evaluation.tree;

import sase.evaluation.tree.elements.Node;

public interface ITreeCostModel {

	public Double getCost(Node root);
}
