package sase.evaluation.tree.multi;

import java.util.ArrayList;
import java.util.List;

import sase.evaluation.common.Match;
import sase.evaluation.tree.elements.TreeInstance;
import sase.evaluation.tree.elements.TreeInstanceStorage;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternMultiTree;

public class MultiTreeInstanceStorage extends TreeInstanceStorage {

	private List<Node> acceptingNodes;
	
	public MultiTreeInstanceStorage(MultiPatternMultiTree multiTree) {
		super();
		acceptingNodes = new ArrayList<Node>(multiTree.getAcceptingNodes().values());
		for (Node rootNode : acceptingNodes) {
			registerNodesInSubtree(rootNode);
		}
	}
	
	@Override
	public List<Match> getMatches() {
		List<Match> matches = new ArrayList<Match>();
		for (Node rootNode : acceptingNodes) {
			for (TreeInstance instance : instances.get(rootNode)) {
				matches.add(instance.getMatch());
			}
			instances.get(rootNode).clear();
		}
		return matches;
	}
	
	@Override
	public boolean hasMatches() {
		for (Node rootNode : acceptingNodes) {
			if (!instances.get(rootNode).isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
