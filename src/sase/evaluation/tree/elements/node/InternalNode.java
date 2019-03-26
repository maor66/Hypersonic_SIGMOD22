package sase.evaluation.tree.elements.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;

public abstract class InternalNode extends Node {

	protected Node leftChild;
	protected Node rightChild;
	protected Long acceptingPatternId = null;
	
	public InternalNode(CNFCondition condition, Node leftSon, Node rightSon) {
		super(condition);
		setLeftChild(leftSon);
		setRightChild(rightSon);
		completeNodeCreation();
	}
	
	public InternalNode(InternalNode other) {
		super(other.mainCondition);
		leftChild = other.leftChild.cloneNode();
		rightChild = other.rightChild.cloneNode();
		nodeCondition = other.nodeCondition;
		acceptingPatternId = other.acceptingPatternId;
	}
	
	protected InternalNode(Node leftChild, Node rightChild, CNFCondition nodeCondition, Long acceptingPatternId) {
		super(null);
		setLeftChild(leftChild, true);
		setRightChild(rightChild, true);
		this.nodeCondition = nodeCondition;
		this.acceptingPatternId = acceptingPatternId;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}
	
	public void setLeftChild(Node leftChild) {
		setLeftChild(leftChild, false);
	}
	
	private void setLeftChild(Node leftChild, boolean isMultiTreeContext) {
		this.leftChild = leftChild;
		setParentToChild(leftChild, isMultiTreeContext);
	}

	public void setRightChild(Node rightChild) {
		setRightChild(rightChild, false);
	}
	
	private void setRightChild(Node rightChild, boolean isMultiTreeContext) {
		this.rightChild = rightChild;
		setParentToChild(rightChild, isMultiTreeContext);
	}
	
	private void setParentToChild(Node child, boolean isMultiTreeContext) {
		if (isMultiTreeContext) {
			child.addParent(this);
		}
		else {
			child.setParent(this);
		}
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

	public Long getAcceptingPatternId() {
		return acceptingPatternId;
	}

	public void setAcceptingPatternId(Long acceptingPatternId) {
		this.acceptingPatternId = acceptingPatternId;
	}
	
	public void swapRandomEventTypes() {
		Random random = new Random();
		List<LeafNode> leaves = getLeavesInSubTree();
		int firstIndex = random.nextInt(leaves.size());
		int secondIndex = random.nextInt(leaves.size());
		while (firstIndex == secondIndex) {
			secondIndex = random.nextInt(leaves.size());
		}
		swapLeaves(leaves.get(firstIndex), leaves.get(secondIndex));
	}

	private void swapLeaves(LeafNode firstLeaf, LeafNode secondLeaf) {
		InternalNode firstParent = (InternalNode)firstLeaf.getParent();
		InternalNode secondParent = (InternalNode)secondLeaf.getParent();
		if (firstLeaf == firstParent.leftChild) {
			firstParent.setLeftChild(secondLeaf);
		}
		else {//right child
			firstParent.setRightChild(secondLeaf);
		}
		if (secondLeaf == secondParent.leftChild) {
			secondParent.setLeftChild(firstLeaf);
		}
		else {//right child
			secondParent.setRightChild(firstLeaf);
		}
		firstLeaf.setParent(secondParent);
		secondLeaf.setParent(firstParent);
	}
	
	@Override
	public void setMainCondition(CNFCondition mainCondition) {
		if (leftChild instanceof InternalNode) {
			((InternalNode)leftChild).setMainCondition(mainCondition);
		}
		if (rightChild instanceof InternalNode) {
			((InternalNode)rightChild).setMainCondition(mainCondition);
		}
		super.setMainCondition(mainCondition);
	}
	
	@Override
	public void enableEventTypeCache(boolean enable) {
		super.enableEventTypeCache(enable);
		leftChild.enableEventTypeCache(enable);
		rightChild.enableEventTypeCache(enable);
	}
	
	@Override
	public void markVisited(boolean visited, boolean recursive) {
		super.markVisited(visited, recursive);
		if (recursive) {
			leftChild.markVisited(visited, recursive);
			rightChild.markVisited(visited, recursive);
		}
	}
	
	@Override
	public void setMaxParentTimeWindow() {
		super.setMaxParentTimeWindow();
		leftChild.setMaxParentTimeWindow();
		rightChild.setMaxParentTimeWindow();
	}

	@Override
	public List<EventType> actuallyGetEventTypes() {
		List<EventType> result = new ArrayList<EventType>(leftChild.actuallyGetEventTypes());
		result.addAll(rightChild.actuallyGetEventTypes());
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

	@Override
	public String getShortDescription() {
		return String.format("(%s*%s)", leftChild.getShortDescription(), rightChild.getShortDescription());
	}
	
//	@Override
//	public boolean equals(Object other) {
//		if (!(other instanceof InternalNode)) {
//			return false;
//		}
//		InternalNode otherNode = (InternalNode)other;
//		if (leftChild == null && otherNode.leftChild != null) {
//			return false;
//		}
//		if (leftChild != null && otherNode.leftChild == null) {
//			return false;
//		}
//		if (rightChild == null && otherNode.rightChild != null) {
//			return false;
//		}
//		if (rightChild != null && otherNode.rightChild == null) {
//			return false;
//		}
//		if (nodeCondition == null && otherNode.nodeCondition != null) {
//			return false;
//		}
//		if (nodeCondition != null && otherNode.nodeCondition == null) {
//			return false;
//		}
//		return super.equals(other);
//	}
	
	public abstract InternalNode createEquivalentNode(Node newLeftChild, Node newRightChild);
	
}
