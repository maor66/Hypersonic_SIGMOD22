package evaluation.tree;

import evaluation.tree.elements.node.Node;
import pattern.Pattern;
import pattern.condition.base.CNFCondition;

public interface ITreeTopologyCreator {

	//at least for now, we assume the list of events to also specify the desired temporal sequence order
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel);
}
