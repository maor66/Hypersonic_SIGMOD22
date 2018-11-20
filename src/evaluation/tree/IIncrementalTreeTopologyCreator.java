package sase.evaluation.tree;

import java.util.List;

import sase.evaluation.tree.elements.node.Node;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public interface IIncrementalTreeTopologyCreator extends ITreeTopologyCreator {

	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel, List<Node> subtrees);
}
