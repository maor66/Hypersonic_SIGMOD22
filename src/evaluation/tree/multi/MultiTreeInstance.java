package sase.evaluation.tree.multi;

import sase.evaluation.common.EventBuffer;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.evaluation.tree.elements.TreeInstance;
import sase.evaluation.tree.elements.node.Node;

public class MultiTreeInstance extends TreeInstance {
	
	public MultiTreeInstance(MultiTreeEvaluationMechanism tree, Node currentNode) {
		super(tree, currentNode);
	}
	
	public MultiTreeInstance(MultiTreeEvaluationMechanism tree, Node currentNode, EventBuffer matchBuffer) {
		super(tree, currentNode, matchBuffer);
	}

	@Override
	public boolean hasMatch() {
		return (((MultiTreeEvaluationMechanism)tree).getAcceptingNodes().contains(currentNode));
	}
	
	@Override
	protected Long getInstanceTimeWindow() {
		return currentNode.getTimeWindow();
	}
	
	@Override
	protected TreeInstance createInstanceForNodeAndBuffer(TreeEvaluationMechanism tree, Node node, EventBuffer buffer) {
		return new MultiTreeInstance((MultiTreeEvaluationMechanism)tree, node, buffer);
	}
}
