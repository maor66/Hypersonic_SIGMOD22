package sase.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmFactory;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelFactory;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.evaluation.plan.DisjunctionEvaluationPlan;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.MultiPatternMultiTreeEvaluationPlan;
import sase.evaluation.plan.MultiPatternTreeEvaluationPlan;
import sase.evaluation.plan.OrderEvaluationPlan;
import sase.evaluation.plan.TreeEvaluationPlan;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.TopologyCreatorFactory;
import sase.evaluation.tree.TreeCostModelFactory;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.MultiPatternGraph;
import sase.multi.MultiPatternMultiTree;
import sase.multi.MultiPatternTree;
import sase.multi.MultiPlan;
import sase.multi.algo.OrderAlgoUnit;
import sase.multi.algo.TreeAlgoUnit;
import sase.multi.calculator.IMPTCalculator;
import sase.multi.calculator.MPTCalculatorFactory;
import sase.pattern.CompositePattern;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.base.CNFCondition;
import sase.specification.evaluation.CostBasedLazyNFAEvaluationSpecification;
import sase.specification.evaluation.EvaluationSpecification;
import sase.specification.evaluation.FixedLazyNFAEvaluationSpecification;
import sase.specification.evaluation.MultiPlanEvaluationSpecification;
import sase.specification.evaluation.TreeEvaluationSpecification;
import sase.specification.evaluation.ParallelEvaluationSpecification;

public class EvaluationPlanCreator {

	private final EvaluationSpecification specification;
	
	public EvaluationPlanCreator(EvaluationSpecification specification) {
		this.specification = specification;
	}
	
	public EvaluationPlan createEvaluationPlan(List<Pattern> patterns, IEvaluationMechanism currentEvaluationMechanism) {
		if (specification.type == EvaluationMechanismTypes.MULTI_PATTERN_TREE ||
			specification.type == EvaluationMechanismTypes.MULTI_PATTERN_MULTI_TREE) {
			return createMultiPatternPlan(patterns, specification.type, currentEvaluationMechanism);
		}
		if (patterns.size() != 1) {
			throw new RuntimeException("Illegal workload size for single-pattern setting");
		}
		return createEvaluationPlan(patterns.get(0));
	}

	public EvaluationPlan createEvaluationPlan(Pattern pattern) {
		switch (specification.type) {
			case LAZY_CHAIN:
			case LAZY_CHAIN_PARALLEL:
				return createOrderBasedPlan(pattern);
			case TREE:
				return createTreeBasedPlan(pattern);
			case HIRZEL_CHAIN_NFA:
			case RIP_CHAIN_NFA:
				switch (((ParallelEvaluationSpecification)specification).internalSpecification.type) {
					case TREE:
						return createTreeBasedPlan(pattern);
					default:
						return createOrderBasedPlan(pattern);
				}
			case MULTI_PATTERN_TREE:
			case MULTI_PATTERN_MULTI_TREE:
				throw new RuntimeException("Illegal evaluation structure for single-pattern setting");
			case EAGER:
			case LAZY_TREE:				
			default:
				return null;
		}
	}

	private HashMap<Pattern, EvaluationPlan> createNestedPlans(Pattern pattern) {
		CompositePattern compositePattern = (CompositePattern) pattern;
		List<Pattern> nestedPatterns = compositePattern.getNestedPatterns();
		HashMap<Pattern, EvaluationPlan> nestedPlans = new HashMap<Pattern, EvaluationPlan>();
		for (Pattern nestedPattern : nestedPatterns) {
			nestedPlans.put(nestedPattern, createEvaluationPlan(nestedPattern));
		}
		return nestedPlans;
	}
	
	private EvaluationPlan createOrderBasedPlan(Pattern pattern) {
		
		// Max: Fix for parallel specifications (Hirzel and RIP)
		EvaluationSpecification curr_specification = specification;
		if (specification instanceof ParallelEvaluationSpecification) {
			curr_specification = ((ParallelEvaluationSpecification)specification).internalSpecification;
		}
		
		// Maor: Here is where the order of the chain is determined
		if (curr_specification instanceof FixedLazyNFAEvaluationSpecification) {
			return createFixedOrderBasedPlan(pattern);
		}
		if (!(curr_specification instanceof CostBasedLazyNFAEvaluationSpecification)) {
			throw new RuntimeException("Unexpected curr_specification type");
		}
		CostBasedLazyNFAEvaluationSpecification costBasedSpecification = 
												(CostBasedLazyNFAEvaluationSpecification)curr_specification;
		IOrderingAlgorithm orderingAlgorithm = 
				OrderingAlgorithmFactory.createOrderingAlgorithm(costBasedSpecification.orderingAlgorithmType, null);
		ICostModel costModel = CostModelFactory.createCostModel(costBasedSpecification.costModelType, 
																new Object[] { pattern.getEventTypes(),
																costBasedSpecification.throughputToLatencyRatio});
		return actuallyCreateOrderBasedPlan(pattern, orderingAlgorithm, costModel);
	}
	
	private EvaluationPlan createFixedOrderBasedPlan(Pattern pattern) {
		FixedLazyNFAEvaluationSpecification fixedOrderSpecification = (FixedLazyNFAEvaluationSpecification)specification;
		List<EventType> evaluationOrder = 
							EventTypesManager.getInstance().convertNamesToTypes(fixedOrderSpecification.evaluationOrder);
		if (pattern.getEventTypes().size() != evaluationOrder.size()) {
			throw new RuntimeException(
					String.format("Invalid evaluation order %s specified for pattern containing events %s", 
								  evaluationOrder.toString(), pattern.getEventTypes().toString()));
		}
		IOrderingAlgorithm orderingAlgorithm = 
				OrderingAlgorithmFactory.createOrderingAlgorithm(OrderingAlgorithmTypes.FIXED, 
																 new Object[] { evaluationOrder });
		return actuallyCreateOrderBasedPlan(pattern, orderingAlgorithm, null);
	}
	
	private EvaluationPlan actuallyCreateOrderBasedPlan(Pattern pattern,
														IOrderingAlgorithm orderingAlgorithm, ICostModel costModel) {
		if (pattern.getType() == PatternOperatorTypes.OR) {
			return new DisjunctionEvaluationPlan(createNestedPlans(pattern));
		}
		return new OrderEvaluationPlan(pattern, createEvaluationOrder(pattern, orderingAlgorithm, costModel), costModel);
	}
	
	private List<EventType> createEvaluationOrder(Pattern pattern,
												  IOrderingAlgorithm orderingAlgorithm, ICostModel costModel) {
		if (!(pattern instanceof CompositePattern)) {
			return orderingAlgorithm.calculateEvaluationOrder(pattern, costModel);
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		List<EventType> iterativeTypes = compositePattern.getIterativeEventTypes();
		List<EventType> negativeTypes = compositePattern.getNegativeEventTypes();
		List<EventType> eventTypesToExclude = new ArrayList<EventType>(iterativeTypes);
		eventTypesToExclude.addAll(negativeTypes);
		CompositePattern simplifiedPattern = compositePattern.getFilteredSubPattern(eventTypesToExclude);
		List<EventType> result = orderingAlgorithm.calculateEvaluationOrder(simplifiedPattern, costModel);
		result.addAll(iterativeTypes);
		result.addAll(negativeTypes);
		return result;
	}
	
	private EvaluationPlan createTreeBasedPlan(Pattern pattern) {
		if (!(specification instanceof TreeEvaluationSpecification)) {
			throw new RuntimeException("Unexpected specification type");
		}
		TreeEvaluationSpecification treeSpecification = (TreeEvaluationSpecification)specification;
		ITreeTopologyCreator topologyCreator = 
				TopologyCreatorFactory.createTopologyCreator(treeSpecification.topologyCreatorType);
		ITreeCostModel costModel = TreeCostModelFactory.createTreeCostModel(
				treeSpecification.treeCostModelType, new Object[] { pattern.getEventTypes(),
																	treeSpecification.throughputToLatencyRatio});
		return actuallyCreateTreeBasedPlan(pattern, topologyCreator, costModel);
	}

	private EvaluationPlan actuallyCreateTreeBasedPlan(Pattern pattern,
													   ITreeTopologyCreator topologyCreator, ITreeCostModel costModel) {
		if (pattern.getType() == PatternOperatorTypes.OR) {
			return new DisjunctionEvaluationPlan(createNestedPlans(pattern));
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		CompositePattern positivePattern = compositePattern.getFilteredSubPattern(compositePattern.getNegativeEventTypes());
		Node root = topologyCreator.createTreeTopology(positivePattern, (CNFCondition) pattern.getCondition(), costModel);
		root.finalizeTree();
		return new TreeEvaluationPlan(root, costModel);
	}
	
	private EvaluationPlan createMultiPatternPlan(List<Pattern> patterns, 
												  EvaluationMechanismTypes multiPatternMechanismType,
												  IEvaluationMechanism currentEvaluationMechanism) {
		if (!(specification instanceof MultiPlanEvaluationSpecification)) {
			throw new RuntimeException("Unexpected specification type");
		}
		MultiPlanEvaluationSpecification mptSpecification = (MultiPlanEvaluationSpecification)specification;
		IMPTCalculator mptCalculator = MPTCalculatorFactory.createMPTCalculator(mptSpecification);
		MultiPlan multiPlan = (currentEvaluationMechanism == null) ?
				mptCalculator.calculateMultiPlan(new MultiPatternGraph(patterns)) :
				mptCalculator.improveMultiPlan(((IMultiPatternEvaluationMechanism)currentEvaluationMechanism).getMultiPlan());
		multiPlan.setAlgoUnit(mptCalculator.getAlgoUnit());
		switch (multiPatternMechanismType) {
			case MULTI_PATTERN_TREE:
				MultiPatternTree multiPatternTree = (MultiPatternTree)multiPlan;
				OrderAlgoUnit orderAlgoUnit = (OrderAlgoUnit)mptCalculator.getAlgoUnit();
				return new MultiPatternTreeEvaluationPlan(multiPatternTree, orderAlgoUnit.getCostModel());
			case MULTI_PATTERN_MULTI_TREE:
				MultiPatternMultiTree multiPatternMultiTree = (MultiPatternMultiTree)multiPlan;
				TreeAlgoUnit treeAlgoUnit = (TreeAlgoUnit)mptCalculator.getAlgoUnit();
				return new MultiPatternMultiTreeEvaluationPlan(multiPatternMultiTree, treeAlgoUnit.getCostModel());
			default:
				throw new RuntimeException(String.format("%s is not a valid multi-pattern evaluation mechanism", 
														 multiPatternMechanismType));
		}
	}

	public EvaluationSpecification getSpecification() {
		return specification;
	}
}
