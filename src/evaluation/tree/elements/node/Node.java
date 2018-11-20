package sase.evaluation.tree.elements.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import sase.base.EventType;
import sase.evaluation.common.State;
import sase.evaluation.tree.elements.TreeInstance;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;

public abstract class Node extends State {

	public static Node getCommonParent(Node firstNode, Node secondNode) {
		List<Node> intersection = new ArrayList<Node>(firstNode.getParents());
		intersection.retainAll(secondNode.getParents());
		if (intersection.size() > 1) {
			throw new RuntimeException("Corrupt tree: a pair of nodes can only have one common parent");
		}
		return intersection.isEmpty() ? null : intersection.get(0);
	}
	
	private List<Node> parents;
	private int level = 0;
	protected CNFCondition mainCondition;
	protected CNFCondition nodeCondition = null;
	private Long timeWindow = null;
	
	private boolean enableEventTypeCache = false;
	private List<EventType> eventTypeCache;
	private boolean visited = false;

	public Node(CNFCondition mainCondition) {
		this(null, mainCondition);
	}
	
	public Node(Node parent, CNFCondition mainCondition) {
		parents = new ArrayList<Node>();
		addParent(parent);
		this.mainCondition = mainCondition;
		eventTypeCache = new ArrayList<EventType>();
	}
	
	public Long getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(Long timeWindow) {
		this.timeWindow = timeWindow;
	}
	
	public void setMaxParentTimeWindow() {
		Long newTimeWindow = null;
		for (Node parent : parents) {
			if (newTimeWindow == null || parent.getTimeWindow() > newTimeWindow) {
				newTimeWindow = parent.getTimeWindow();
			}
		}
		setTimeWindow(newTimeWindow);
	}

	public void enableEventTypeCache(boolean enable) {
		enableEventTypeCache = enable;
		if (!enableEventTypeCache) {
			eventTypeCache.clear();
		}
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void markVisited(boolean visited, boolean recursive) {
		this.visited = visited;
	}
	
	public boolean isMultiTreeNode() {
		return (parents.size() > 1);
	}
	
	public List<Node> getParents() {
		return parents;
	}
	
	public void addParent(Node parent) {
		if (parent == null) {
			return;
		}
//		if (parents.contains(parent)) {
//			throw new RuntimeException("Parent was already added");
//		}
		parents.add(parent);
	}
	
	public boolean removeParent(Node parent) {
		return parents.remove(parent);
	}
	
	public CNFCondition getNodeCondition() {
		return nodeCondition;
	}
	
	public boolean isNodeConditionSatisfied(TreeInstance treeInstance) {
		if (nodeCondition == null) {
			return false;
		}
		return nodeCondition.verify(treeInstance.getEvents());
	}
	
	public List<Node> getPeers() {
		List<Node> result = new ArrayList<Node>();
		for (Node parent : parents) {
			result.add(((InternalNode)parent).getOtherChild(this));
		}
		return result;
	}
	
	protected void completeNodeCreation() {
		initializeNodeCondition();
	}
	
	public List<EventType> getEventTypes() {
		if (enableEventTypeCache && !eventTypeCache.isEmpty()) {
			return eventTypeCache;
		}
		List<EventType> eventTypes = actuallyGetEventTypes();
		if (enableEventTypeCache) {
			eventTypeCache = eventTypes;
		}
		return eventTypes;
	}
	
	public boolean isSuperNode(Node other) {
		List<EventType> myEventTypes = getEventTypes();
		List<EventType> otherEventTypes = other.getEventTypes();
		return myEventTypes.containsAll(otherEventTypes);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Node)) {
			return false;
		}
		Node otherNode = (Node)other;
		List<EventType> myEventTypes = getEventTypes();
		List<EventType> otherEventTypes = otherNode.getEventTypes();
		if (!(new HashSet<EventType>(myEventTypes).equals(new HashSet<EventType>(otherEventTypes)))) {
			return false;
		}
		return nodeCondition.equals(otherNode.getNodeCondition());
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(getEventTypes(), nodeCondition);
    }

	//////////////////////////////////////Single-tree-only methods
	public Node getParent() {
		if (isMultiTreeNode()) {
			throw new RuntimeException("Unexpected usage of getParent() in a multi-tree");
		}
		return parents.isEmpty() ? null : parents.get(0);
	}

	public void setParent(Node parent) {
		if (isMultiTreeNode()) {
			throw new RuntimeException("Unexpected usage of setParent() in a multi-tree");
		}
		parents.clear();
		parents.add(parent);
		this.level = parent == null ? 0 : parent.level + 1;
	}
	
	public Node getPeer() {
		if (isMultiTreeNode()) {
			throw new RuntimeException("Unexpected usage of getPeer() in a multi-tree");
		}
		if (parents.isEmpty()) {
			return null;
		}
		return getPeers().get(0);
	}
	
	public CNFCondition getMainCondition() {
		if (isMultiTreeNode()) {
			throw new RuntimeException("Unexpected usage of getMainCondition() in a multi-tree");
		}
		return mainCondition;
	}
	
	public void finalizeTree() {
		if (isMultiTreeNode()) {
			throw new RuntimeException("Unexpected usage of finalizeNode() in a multi-tree");
		}
		finalizeNode(null);
	}
	
	public void setMainCondition(CNFCondition mainCondition) {
		this.mainCondition = mainCondition;
		initializeNodeCondition();
	}
	
	@Override
	public String toString() {
		String indentation = new String(new char[level]).replace("\0", "\t");
		return printNode(indentation);
	}
	///////////////////////////////////////////////////////////////////////////
	
	public abstract List<LeafNode> getLeavesInSubTree();
	public abstract List<Node> getNodesInSubTree();
	public abstract void finalizeNode(Node parent);
	public abstract Node cloneNode();
	protected abstract List<EventType> actuallyGetEventTypes();
	protected abstract void initializeNodeCondition();
	protected abstract String printNode(String indentation);
	public abstract Node getNodeByAtomicCondition(AtomicCondition condition);
	public abstract double getNodeReachProbability();
	public abstract String getShortDescription();

}
