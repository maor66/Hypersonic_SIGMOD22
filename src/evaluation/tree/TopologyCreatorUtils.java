package evaluation.tree;

import java.util.List;

import base.EventType;
import evaluation.tree.elements.node.ConjunctiveInternalNode;
import evaluation.tree.elements.node.InternalNode;
import evaluation.tree.elements.node.Node;
import evaluation.tree.elements.node.SeqInternalNode;
import pattern.Pattern;
import pattern.condition.base.CNFCondition;

public class TopologyCreatorUtils {

	public static InternalNode createNodeByPatternType(Pattern pattern, CNFCondition condition, List<EventType> order,
													   Node leftChild, Node rightChild) {
		switch (pattern.getType()) {
			case AND_SEQ:
				return new ConjunctiveInternalNode(condition, leftChild, rightChild);
			case SEQ:
				return new SeqInternalNode(condition, order, leftChild, rightChild);
			case ITER:
			case NEG:
			case NONE:
			case NOP:
			case OLD_AND:
			case OLD_SEQ:
			case OR:
			default:
				throw new RuntimeException("Invalid pattern type for the topology creator");
		}
	}

}
