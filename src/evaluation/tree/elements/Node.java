package evaluation.tree.elements;

import java.util.List;

import base.Event;
import base.EventType;
import evaluation.common.State;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;

public abstract class Node extends State {

	protected Node parent;
	protected final CNFCondition mainCondition;
	protected CNFCondition nodeCondition = null;
	protected final List<EventType> order;//note that this means that our tree only supports sequences as of now!
	protected int level;

	public Node(CNFCondition mainCondition, List<EventType> order) {
		this(null, mainCondition, order);
	}
	
	public Node(Node parent, CNFCondition mainCondition, List<EventType> order) {
		setParent(parent);
		this.mainCondition = mainCondition;
		this.order = order;
	}
	
	public List<EventType> getOrder() {
		return order;
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
	
	public boolean isNodeConditionSatisfied(List<Event> events) {
		if (nodeCondition == null) {
			return false;
		}
		return nodeCondition.verify(events);
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
