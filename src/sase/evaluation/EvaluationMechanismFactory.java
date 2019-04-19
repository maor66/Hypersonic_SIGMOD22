package sase.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.config.MainConfig;
import sase.evaluation.data_parallel.HirzelEvaluationMechanism;
import sase.evaluation.data_parallel.ParallelDummyEvaluationMechanism;
import sase.evaluation.data_parallel.RIPEvaluationMechanism;
import sase.evaluation.nfa.eager.AND_NFA;
import sase.evaluation.nfa.eager.AND_SEQ_NFA;
import sase.evaluation.nfa.eager.OR_AND_SEQ_NFA;
import sase.evaluation.nfa.eager.SEQ_NFA;
import sase.evaluation.nfa.lazy.LazyChainNFA;
import sase.evaluation.nfa.lazy.LazyMultiChainNFA;
import sase.evaluation.nfa.lazy.LazyMultiPatternTreeNFA;
import sase.evaluation.nfa.lazy.ParallelLazyChainNFA;
import sase.evaluation.plan.DisjunctionEvaluationPlan;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.MultiPatternMultiTreeEvaluationPlan;
import sase.evaluation.plan.TreeEvaluationPlan;
import sase.evaluation.tree.DisjunctionTreeEvaluationMechanism;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.evaluation.tree.elements.node.Node;
import sase.evaluation.tree.multi.MultiTreeEvaluationMechanism;
import sase.multi.MultiPatternMultiTree;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.base.TrivialCondition;
import sase.simulator.Environment;
import sase.specification.evaluation.EvaluationSpecification;
import sase.specification.evaluation.HirzelEvaluationSpecification;
import sase.specification.evaluation.LazyNFAEvaluationSpecification;
import sase.specification.evaluation.ParallelDummyEvaluationSpecification;
import sase.specification.evaluation.ParallelEvaluationSpecification;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;
import sase.specification.evaluation.RIPEvaluationSpecification;
import sase.statistics.Statistics;

public class EvaluationMechanismFactory {

	public static IEvaluationMechanism createEvaluationMechanism(List<Pattern> patterns, 
																 IEvaluationMechanism currentEvaluationMechanism) {
		EvaluationPlanCreator evaluationPlanCreator = Environment.getEnvironment().getEvaluationPlanCreator();
		EvaluationPlan evaluationPlan = evaluationPlanCreator.createEvaluationPlan(patterns, currentEvaluationMechanism);
		if (!MainConfig.conditionSelectivityMeasurementMode) {
			Environment.getEnvironment().getStatisticsManager().replaceFractionalStatistic(Statistics.evaluationPlanCost,
					   																	   evaluationPlan.getCost());
		}
		EvaluationSpecification specification = evaluationPlanCreator.getSpecification();
		if (specification.type == EvaluationMechanismTypes.MULTI_PATTERN_TREE) {
			return new LazyMultiPatternTreeNFA(patterns, evaluationPlan);
		}
		else if (specification.type == EvaluationMechanismTypes.MULTI_PATTERN_MULTI_TREE) {
			return createMultiTreeEvaluationMechanism(evaluationPlan);
		}
		if (patterns.size() != 1) {
			throw new RuntimeException("Illegal workload size for single-pattern setting");
		}
		//single-pattern setting
		Pattern pattern = patterns.get(0);
		switch (pattern.getType()) {
			case SEQ:
				switch(specification.type) {
					case EAGER:
						return new AND_SEQ_NFA(pattern);
					case LAZY_CHAIN:
						return new LazyChainNFA(pattern, evaluationPlan, 
												((LazyNFAEvaluationSpecification)specification).negationType);
					case LAZY_CHAIN_PARALLEL:
						return new ParallelLazyChainNFA(pattern, evaluationPlan, (ParallelLazyNFAEvaluationSpecification)specification);
					case LAZY_CHAIN_PARALLEL_DUMMY:
						return new ParallelDummyEvaluationMechanism(pattern, evaluationPlan, (ParallelDummyEvaluationSpecification)specification);
					case HIRZEL_CHAIN_NFA:
						return new HirzelEvaluationMechanism(pattern, (HirzelEvaluationSpecification)specification, evaluationPlan);
					case RIP_CHAIN_NFA:
						return new RIPEvaluationMechanism(pattern, (RIPEvaluationSpecification)specification, evaluationPlan);
					case LAZY_TREE:
						return null;
					case TREE:
						return new TreeEvaluationMechanism(pattern, evaluationPlan);
					case MULTI_PATTERN_TREE:
					case MULTI_PATTERN_MULTI_TREE:
					default:
						return null;
			}
			case AND_SEQ:
				switch(specification.type) {
					case EAGER:
						return new AND_SEQ_NFA(pattern);
					case LAZY_CHAIN:
						return new LazyChainNFA(pattern, evaluationPlan, 
												((LazyNFAEvaluationSpecification)specification).negationType);
					case LAZY_TREE:
						return null;
					case TREE:
						return new TreeEvaluationMechanism(pattern, evaluationPlan);
					case MULTI_PATTERN_TREE:
					case MULTI_PATTERN_MULTI_TREE:
					default:
						return null;
			}
			case OR:
				switch(specification.type) {
					case EAGER:
						return new OR_AND_SEQ_NFA(pattern);
					case LAZY_CHAIN:
						return new LazyMultiChainNFA(pattern, evaluationPlan, 
													 ((LazyNFAEvaluationSpecification)specification).negationType);
					case LAZY_TREE:
						return null;
					case TREE:
						return new DisjunctionTreeEvaluationMechanism(pattern, evaluationPlan);
					case MULTI_PATTERN_TREE:
					case MULTI_PATTERN_MULTI_TREE:
					default:
						return null;
			}
			case OLD_SEQ:
				switch(specification.type) {
					case EAGER:
						return new SEQ_NFA(pattern);
					case LAZY_CHAIN:
					case LAZY_TREE:
					case TREE:
					default:
						return null;
				}
			case OLD_AND:
				switch(specification.type) {
					case EAGER:
						return new AND_NFA(pattern);
					case LAZY_CHAIN:
					case LAZY_TREE:
					case TREE:
					default:
						return null;
				}
			case ITER:
			case NEG:
			case NOP:
			default:
				throw new RuntimeException(String.format("Cannot create an evaluation mechanism for main pattern type %s", 
										   pattern.getType()));
			}
	}

	private static IEvaluationMechanism createMultiTreeEvaluationMechanism(EvaluationPlan evaluationPlan) {
		MultiPatternMultiTree multiTree =
				(MultiPatternMultiTree)((MultiPatternMultiTreeEvaluationPlan)evaluationPlan).getRepresentation();
		if (multiTree.isSharingEnabled()) {
			return new MultiTreeEvaluationMechanism(evaluationPlan);
		}
		CompositePattern disjunctionPattern = new CompositePattern(PatternOperatorTypes.OR, 
																   new ArrayList<Pattern>(multiTree.getPatterns()), 
																   new TrivialCondition(), 
																   multiTree.getPatterns().iterator().next().getTimeWindow());
		HashMap<Pattern, EvaluationPlan> nestedPlans = new HashMap<Pattern, EvaluationPlan>();
		HashMap<Long, Node> singlePatternTrees = multiTree.getSinglePatternTrees();
		for (CompositePattern pattern : multiTree.getPatterns()) {
			EvaluationPlan newPlan = new TreeEvaluationPlan(singlePatternTrees.get(pattern.getPatternId()));
			newPlan.setCost(multiTree.getAlgoUnit().getPlanCost(pattern, newPlan));
			nestedPlans.put(pattern, newPlan);
		}
		DisjunctionEvaluationPlan disjunctionEvaluationPlan = new DisjunctionEvaluationPlan(nestedPlans);
		return new DisjunctionTreeEvaluationMechanism(disjunctionPattern, disjunctionEvaluationPlan);
	}
}
