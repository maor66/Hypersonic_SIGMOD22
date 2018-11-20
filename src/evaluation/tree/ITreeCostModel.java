package sase.evaluation.tree;

import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternMultiTree;

public interface ITreeCostModel {

	public Double getCost(Node root);
	public Double getMPMTCost(MultiPatternMultiTree multiPatternMultiTree);
}
