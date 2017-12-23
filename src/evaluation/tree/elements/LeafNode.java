package evaluation.tree.elements;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;

public class LeafNode extends Node {
	
	private final EventType eventType;

	public LeafNode(EventType eventType, CNFCondition condition) {
		super(condition, null);
		this.eventType = eventType;
		completeNodeCreation();
	}
	
	public LeafNode(LeafNode other) {
		this(other.eventType, other.mainCondition);
	}
	
	public EventType getEventType() {
		return eventType;
	}

	@Override
	public List<EventType> getEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(eventType);
		return result;
	}

	@Override
	public List<Node> getNodesInSubTree() {
		List<Node> result = new ArrayList<Node>();
		result.add(this);
		return result;
	}

	@Override
	public List<LeafNode> getLeavesInSubTree() {
		List<LeafNode> result = new ArrayList<LeafNode>();
		result.add(this);
		return result;
	}

	@Override
	public void finalizeNode(Node parent) {
		setParent(parent);
	}

	@Override
	protected void initializeNodeCondition() {
		nodeCondition = mainCondition.getFiltersForType(eventType);
	}
	
	@Override
	protected String printNode(String indentation) {
		return String.format("%sLeaf[%s, %s]", indentation, eventType, nodeCondition);
	}

	@Override
	public Node getNodeByAtomicCondition(AtomicCondition condition) {
		return null;
	}

	@Override
	public double getNodeReachProbability() {
		return 1.0;
	}

}
