package sase.evaluation.tree.elements.node;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;

public class LeafNode extends Node {
	
	private final EventType eventType;
	private final boolean isIterated;

	public LeafNode(EventType eventType, CNFCondition condition, boolean isIterated) {
		super(condition);
		this.eventType = eventType;
		this.isIterated = isIterated;
		completeNodeCreation();
	}
	
	public LeafNode(LeafNode other) {
		this(other.eventType, other.mainCondition, other.isIterated);
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public boolean isIterated() {
		return isIterated;
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
