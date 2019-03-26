package sase.evaluation.tree.multi;

import java.util.Collection;
import java.util.List;

import sase.evaluation.IMultiPatternEvaluationMechanism;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.MultiPatternMultiTreeEvaluationPlan;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.evaluation.tree.elements.TreeInstance;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternMultiTree;
import sase.multi.MultiPlan;
import sase.pattern.Pattern;

public class MultiTreeEvaluationMechanism extends TreeEvaluationMechanism implements IMultiPatternEvaluationMechanism {

	MultiPatternMultiTree multiTree;
	
	public MultiTreeEvaluationMechanism(EvaluationPlan evaluationPlan) {
		super(null, evaluationPlan);
	}
	
	public Collection<Node> getAcceptingNodes() {
		return multiTree.getAcceptingNodes().values();
	}
	
	@Override
	protected void initTreeStructure(Pattern pattern, EvaluationPlan evaluationPlan) {
		multiTree = (MultiPatternMultiTree)((MultiPatternMultiTreeEvaluationPlan)evaluationPlan).getRepresentation();
	}
	
	@Override
	protected void initEventTypeToLeafMap() {
		eventTypeToLeafMap = multiTree.getLeaves();
	}
	
	@Override
	protected void initStorage() {
		storage = new MultiTreeInstanceStorage(multiTree);
	}
	
	@Override
	protected TreeInstance createLeafInstance(LeafNode leaf) {
		return new MultiTreeInstance(this, leaf);
	}
	
	@Override
	protected List<Node> getPeers(TreeInstance currentInstance) {
		return currentInstance.getCurrentNode().getPeers();
	}

	@Override
	public MultiPlan getMultiPlan() {
		return multiTree;
	}

	@Override
	public String getStructureSummary() {
		return multiTree.toString();
	}
	
	@Override
	protected boolean isIterativeInstance(TreeInstance treeInstance) {
		return false; //unsupported as of now
	}

}
