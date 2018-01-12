package evaluation.tree.elements.node;

import pattern.condition.base.CNFCondition;

public class ConjunctiveInternalNode extends InternalNode {

	public ConjunctiveInternalNode(CNFCondition condition, Node leftSon, Node rightSon) {
		super(condition, leftSon, rightSon);
	}

}
