package sase.evaluation.tree.elements;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.config.MainConfig;
import sase.input.EventTypesConverterTypes;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.time.PairTemporalOrderCondition;

public class InternalNode extends Node {

	private final Node leftChild;
	private final Node rightChild;
	
	public InternalNode(CNFCondition condition, List<EventType> order, Node leftSon, Node rightSon) {
		super(condition, order);
		this.leftChild = leftSon;
		this.rightChild = rightSon;
		this.leftChild.setParent(this);
		this.rightChild.setParent(this);
		completeNodeCreation();
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}
	
	public Node getOtherChild(Node son) {
		if (son == leftChild) {
			return rightChild;
		}
		if (son == rightChild) {
			return leftChild;
		}
		return null;
	}

	@Override
	public List<EventType> getEventTypes() {
		List<EventType> result = new ArrayList<EventType>(leftChild.getEventTypes());
		result.addAll(rightChild.getEventTypes());
		return result;
	}

	@Override
	public List<Node> getNodesInSubTree() {
		List<Node> result = new ArrayList<Node>(leftChild.getNodesInSubTree());
		result.addAll(rightChild.getNodesInSubTree());
		result.add(this);
		return result;
	}

	@Override
	public List<LeafNode> getLeavesInSubTree() {
		List<LeafNode> result = new ArrayList<LeafNode>(leftChild.getLeavesInSubTree());
		result.addAll(rightChild.getLeavesInSubTree());
		return result;
	}

	@Override
	public void finalizeNode(Node parent) {
		setParent(parent);
		leftChild.finalizeNode(this);
		rightChild.finalizeNode(this);
		//XXX: ATTENTION - TEMPORARY HACK!!! Remove this ASAP.
		if (MainConfig.eventTypesConverterType != EventTypesConverterTypes.TRAFFIC_SPEED_VEHICLES_NUMBER) {
			calculateTemporalCondition();
		}
	}

	@Override
	protected void initializeNodeCondition() {
		List<EventType> leftEventTypes = leftChild.getEventTypes();
		List<EventType> rightEventTypes = rightChild.getEventTypes();
		nodeCondition = mainCondition.getConditionBetweenTypeSets(leftEventTypes, rightEventTypes);
	}
	
	private void calculateTemporalCondition() {
		List<EventType> leftEventTypes = leftChild.getEventTypes();
		List<EventType> rightEventTypes = rightChild.getEventTypes();
		CNFCondition temporalCondition = new CNFCondition();
		for (EventType eventType : rightEventTypes) {
			for (int i = order.indexOf(eventType) - 1; i >= 0; --i) {
				EventType currEventType = order.get(i);
				if (rightEventTypes.contains(currEventType)) {
					break;//already tightly bound
				}
				if (leftEventTypes.contains(currEventType)) {
					//tightest bound found
					PairTemporalOrderCondition currentTemporalCondition = 
												new PairTemporalOrderCondition(currEventType, eventType);
					currentTemporalCondition.setSelectivity(1.0);
					temporalCondition.addAtomicCondition(currentTemporalCondition);
					break;
				}
			}
			for (int i = order.indexOf(eventType) + 1; i < order.size(); ++i) {
				EventType currEventType = order.get(i);
				if (rightEventTypes.contains(currEventType)) {
					break;//already tightly bound
				}
				if (leftEventTypes.contains(currEventType)) {
					//tightest bound found
					PairTemporalOrderCondition currentTemporalCondition = 
												new PairTemporalOrderCondition(eventType, currEventType);
					currentTemporalCondition.setSelectivity(1.0);
					temporalCondition.addAtomicCondition(currentTemporalCondition);
					break;
				}
			}
		}
		nodeCondition.addAtomicConditions(temporalCondition);
	}
	
	@Override
	protected String printNode(String indentation) {
		String leftString = leftChild.toString();
		String rightString = rightChild.toString();
		return String.format("%sNode[\n%s\n%s\n%s](%s)", indentation, leftString, rightString, indentation, nodeCondition);
	}

	@Override
	public Node getNodeByAtomicCondition(AtomicCondition condition) {
		if (nodeCondition.getAtomicConditions().contains(condition)) {
			return this;
		}
		Node nodeFromLeftSubtree = leftChild.getNodeByAtomicCondition(condition);
		if (nodeFromLeftSubtree != null) {
			return nodeFromLeftSubtree;
		}
		return rightChild.getNodeByAtomicCondition(condition);
	}

	@Override
	public double getNodeReachProbability() {
		return nodeCondition.getSelectivity() * leftChild.getNodeReachProbability() * rightChild.getNodeReachProbability();
	}
	
}
