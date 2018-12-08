package sase.evaluation.tree.elements.node;

import sase.pattern.condition.base.CNFCondition;

public class ConjunctiveInternalNode extends InternalNode {

	public ConjunctiveInternalNode(CNFCondition condition, Node leftSon, Node rightSon) {
		super(condition, leftSon, rightSon);
	}
	
	public ConjunctiveInternalNode(ConjunctiveInternalNode other) {
		super(other);
	}
	
	protected ConjunctiveInternalNode(Node leftChild, Node rightChild, CNFCondition nodeCondition, Long acceptingPatternId) {
		super(leftChild, rightChild, nodeCondition, acceptingPatternId);
	}

	@Override
	public Node cloneNode() {
		return new ConjunctiveInternalNode(this);
	}

	@Override
	public InternalNode createEquivalentNode(Node newLeftChild, Node newRightChild) {
		return new ConjunctiveInternalNode(newLeftChild, newRightChild, nodeCondition, acceptingPatternId);
	}

}
