package sase.evaluation.tree.elements.node;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;

public abstract class InternalNode extends Node {

	protected Node leftChild;
	protected Node rightChild;
	
	public InternalNode(CNFCondition condition, Node leftSon, Node rightSon) {
		super(condition);
		setLeftChild(leftSon);
		setRightChild(rightSon);
		completeNodeCreation();
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}
	
	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
		this.leftChild.setParent(this);
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
		this.rightChild.setParent(this);
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
	}

	@Override
	protected void initializeNodeCondition() {
		List<EventType> leftEventTypes = leftChild.getEventTypes();
		List<EventType> rightEventTypes = rightChild.getEventTypes();
		nodeCondition = mainCondition.getConditionBetweenTypeSets(leftEventTypes, rightEventTypes);
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
