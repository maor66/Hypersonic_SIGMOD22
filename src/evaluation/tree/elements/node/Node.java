package evaluation.tree.elements.node;

import java.util.List;

import base.EventType;
import evaluation.common.State;
import evaluation.tree.elements.TreeInstance;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;

public abstract class Node extends State {

	protected Node parent;
	protected final CNFCondition mainCondition;
	protected CNFCondition nodeCondition = null;
	protected int level;

	public Node(CNFCondition mainCondition) {
		this(null, mainCondition);
	}
	
	public Node(Node parent, CNFCondition mainCondition) {
		setParent(parent);
		this.mainCondition = mainCondition;
	}

	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
		this.level = (parent == null) ? 0 : parent.level + 1;
	}

	public CNFCondition getMainCondition() {
		return mainCondition;
	}
	
	public CNFCondition getNodeCondition() {
		return nodeCondition;
	}
	
	public void finalizeTree() {
		finalizeNode(null);
	}
	
	public boolean isNodeConditionSatisfied(TreeInstance treeInstance) {
		if (nodeCondition == null) {
			return false;
		}
		return nodeCondition.verify(treeInstance.getEvents());
	}
	
	public Node getPeer() {
		if (parent == null) {
			return null;
		}
		return ((InternalNode)parent).getOtherChild(this);
	}
	
	protected void completeNodeCreation() {
		initializeNodeCondition();
	}
	
	@Override
	public String toString() {
		String indentation = new String(new char[level]).replace("\0", "\t");
		return printNode(indentation);
	}
	
	public abstract List<EventType> getEventTypes();
	public abstract List<LeafNode> getLeavesInSubTree();
	public abstract List<Node> getNodesInSubTree();
	public abstract void finalizeNode(Node parent);
	protected abstract void initializeNodeCondition();
	protected abstract String printNode(String indentation);
	public abstract Node getNodeByAtomicCondition(AtomicCondition condition);
	public abstract double getNodeReachProbability();

}
