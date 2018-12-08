package sase.multi.algo;

import java.util.Arrays;
import java.util.List;

import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.TreeEvaluationPlan;
import sase.evaluation.tree.IIncrementalTreeTopologyCreator;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.TopologyCreatorFactory;
import sase.evaluation.tree.TopologyCreatorTypes;
import sase.evaluation.tree.elements.node.InternalNode;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternMultiTree;
import sase.multi.MultiPlan;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class TreeAlgoUnit implements IAlgoUnit {

	private final IIncrementalTreeTopologyCreator treeCreator;
	private final ITreeCostModel costModel;
	private final ITreeTopologyCreator trivialTreeCreator;
	
	public TreeAlgoUnit(IIncrementalTreeTopologyCreator treeCreator, ITreeCostModel costModel) {
		this.treeCreator = treeCreator;
		this.costModel = costModel;
		this.trivialTreeCreator = TopologyCreatorFactory.createTopologyCreator(TopologyCreatorTypes.TRIVIAL);
	}
	
	public ITreeCostModel getCostModel() {
		return costModel;
	}

	@Override
	public EvaluationPlan calculateEvaluationPlan(Pattern pattern) {
		return new TreeEvaluationPlan(treeCreator.createTreeTopology(pattern, (CNFCondition)pattern.getCondition(), costModel));
	}

	@Override
	public EvaluationPlan calculateFullEvaluationPlan(Pattern pattern, EvaluationPlan subPlan,
													  MultiPlan currentMultiPlan) {
		Node subTree = subPlan == null ? null : ((TreeEvaluationPlan)subPlan).getRepresentation();
		if (currentMultiPlan == null) {
			return new TreeEvaluationPlan(treeCreator.createTreeTopology(pattern, 
																		 (CNFCondition)pattern.getCondition(), 
					 													 costModel, 
					 													 subTree == null ? null : Arrays.asList(subTree)));
		}
		MultiPatternMultiTree multiPatternMultiTree = (MultiPatternMultiTree)currentMultiPlan;
		List<Node> subtreesToKeep = multiPatternMultiTree.getFilteredSharedSubtrees((CompositePattern)pattern, subTree);
		if (subtreesToKeep != null && subTree != null) {
			subtreesToKeep.add(subTree);
		}
		return new TreeEvaluationPlan(treeCreator.createTreeTopology(pattern, (CNFCondition)pattern.getCondition(), 
																	 costModel, subtreesToKeep));
	}

	@Override
	public Double getPlanCost(Pattern pattern, EvaluationPlan plan) {
		return costModel.getCost(((TreeEvaluationPlan)plan).getRepresentation());
	}

	@Override
	public Double getMultiPlanCost(MultiPlan multiPlan) {
		return costModel.getMPMTCost((MultiPatternMultiTree)multiPlan);
	}

	@Override
	public MultiPlan instantiateMultiPlan() {
		return new MultiPatternMultiTree();
	}

	@Override
	public MultiPlan instantiateMultiPlan(boolean enableSharing) {
		return new MultiPatternMultiTree(enableSharing);
	}

	@Override
	public MultiPlan instantiateMultiPlan(MultiPlan source) {
		return new MultiPatternMultiTree((MultiPatternMultiTree)source);
	}

	@Override
	public void swapRandomEventTypes(CompositePattern pattern, MultiPlan multiPlan) {
		MultiPatternMultiTree multiPatternMultiTree = (MultiPatternMultiTree)multiPlan;
		Node tree = ((TreeEvaluationPlan)multiPatternMultiTree.getPlanForPattern(pattern)).getRepresentation().cloneNode();
		if (!(tree instanceof InternalNode)) {
			return;
		}
		((InternalNode)tree).swapRandomEventTypes();
		tree.setMainCondition(pattern.getCNFCondition());
		multiPlan.replacePatternPlan(pattern, new TreeEvaluationPlan(tree));
	}

	@Override
	public EvaluationPlan getTrivialEvaluationPlan(Pattern pattern) {
		return new TreeEvaluationPlan(trivialTreeCreator.createTreeTopology(pattern,
																			(CNFCondition)pattern.getCondition(),
																			costModel));
	}

}
