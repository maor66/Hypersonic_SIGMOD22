package sase.evaluation.tree.creators;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.TopologyCreatorUtils;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class TrivialTreeTopologyCreator implements ITreeTopologyCreator {

	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		List<LeafNode> leaves = new ArrayList<LeafNode>(); 
		for (EventType eventType : pattern.getEventTypes()) {
			boolean isIterated = ((CompositePattern)pattern).getIterativeEventTypes().contains(eventType);
			leaves.add(new LeafNode(eventType, mainCondition, isIterated));
		}
		Node currentNode = leaves.get(0);
		for (int i = 1; i < leaves.size(); ++i) {
			currentNode = TopologyCreatorUtils.createNodeByPatternType(pattern, mainCondition, 
																	   pattern.getEventTypes(), 
																	   currentNode, leaves.get(i));
		}
		return currentNode;
	}

}
