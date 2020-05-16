package sase.config;

import sase.specification.SimulationSpecification;
import sase.specification.adaptation.AdaptationSpecification;
import sase.specification.adaptation.ConstantThresholdAdaptationSpecification;
import sase.specification.adaptation.TrivialAdaptationSpecification;
import sase.specification.algo.OrderAlgoUnitSpecification;
import sase.specification.algo.TreeAlgoUnitSpecification;
import sase.specification.creators.SpecificationCreatorTypes;
import sase.specification.creators.condition.ConditionSpecificationCreatorTypes;
import sase.specification.creators.condition.ConditionSpecificationSetCreatorTypes;
import sase.specification.evaluation.*;
import sase.specification.input.InputSpecification;
import sase.specification.input.ShuffleEventTypesInputSpecification;
import sase.specification.input.TrivialInputSpecification;
import sase.specification.workload.PatternReorderingSensitivityTypes;
import sase.specification.workload.PatternSpecification;
import sase.specification.workload.SinglePatternWorkloadSpecification;
import sase.specification.workload.WorkloadCreationSpecification;
import sase.user.stocks.StockEventTypesManager;
import sase.user.stocks.condition.StockFirstValueCmpCondition.ComparisonOperation;
import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;
import sase.adaptive.monitoring.invariant.compare.InvariantComparerType;
import sase.base.Event; //this dummy import is needed to avoid the annoying 'unused warning suppression' message
import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.LazyNFANegationTypes;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;
import sase.evaluation.tree.TopologyCreatorTypes;
import sase.evaluation.tree.TreeCostModelTypes;
import sase.multi.calculator.MPTCalculatorTypes;
import sase.multi.calculator.local.neighborhood.NeighborhoodTypes;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.workload.WorkloadManagerTypes;

@SuppressWarnings("unused")
public class SimulationConfig {

	//specification creator definition
	public static final SpecificationCreatorTypes specificationCreatorType =
																SpecificationCreatorTypes.CROSS_PRODUCT_SINGLE;
	public static final ConditionSpecificationCreatorTypes conditionCreatorType =
																ConditionSpecificationCreatorTypes.STOCK_DELTA;
	public static final ConditionSpecificationSetCreatorTypes conditionSetCreatorType =
																ConditionSpecificationSetCreatorTypes.COUNTERS;

	//for stock events
	public static final long timeWindows[] = {20};

	//for tram events
	//public static final long timeWindows[] = {3 * 60, 6 * 60, 9 * 60, 12 * 60, 15 * 60, 18 * 60};

	//for traffic events
	//public static final long timeWindows[] = {5, 10, 15, 20, 25, 30};

	//for fraud events
	//public static final long timeWindows[] = {3, 6, 9, 12, 15, 18};
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	/* Settings for pattern specification generation. */
	public static final int[] patternSizes = {3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22};
	public static final int patternsPerLength = 1;
	public static final Long timeWindowForPatternGenerator = 25L;
	public static final PatternOperatorTypes mainOperatorType = PatternOperatorTypes.AND_SEQ;
	
	public static final int negatedEventsNumber = 0;
	public static final double negatedConditionProbability = 1.0;
	
	public static final int iteratedEventsNumber = 0;
	public static final ComparisonOperation triggerComparisonOperator = ComparisonOperation.EQUALS;
	public static final ComparisonOperation iterativeComparisonOperator = ComparisonOperation.EQUALS;
	public static final ComparisonOperation terminatorComparisonOperator = ComparisonOperation.EQUALS;
	
	public static final int numberOfDisjunctions = 3;
	
	public static final int[] numbersOfConditions = {1,2,3,4,4};
	public static final Double conditionToEventRatio = 3.0 / 4.0;
	public static final double[] correlations = {0.5,0.6,0.7,0.8,0.9};
	
	
	private static final int statisticsMonitoringWindowToTimeWindowRatio = 2;
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final InputSpecification[] inputSpecifications = {
			//Experiments 1-8
			///////////////////////////////////////////////////////////////////////no input modification
			new TrivialInputSpecification(),
			/////////////////////////////////////////////////////////////////////////////////////////////////////			
			
			//Experiment 9
			///////////////////////////////////////////////////////////////////////input modification
//			new ShuffleEventTypesInputSpecification(100),
//			new ShuffleEventTypesInputSpecification(500),
//			new ShuffleEventTypesInputSpecification(1000),
//			new ShuffleEventTypesInputSpecification(5000),
//			new ShuffleEventTypesInputSpecification(10000),
			/////////////////////////////////////////////////////////////////////////////////////////////////////			
	};
	
	
	
	public static final AdaptationSpecification[] adaptationSpecifications = {
			//Experiments 1-8
			///////////////////////////////////////////////////////////////////////no input modification
			new TrivialAdaptationSpecification(),
			/////////////////////////////////////////////////////////////////////////////////////////////////////			
			
			//Experiment 9
			///////////////////////////////////////////////////////////////////////input modification
//			new TrivialAdaptationSpecification(),
//			new ConstantThresholdAdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 0.1),
			/////////////////////////////////////////////////////////////////////////////////////////////////////
	};
	
	
	
	public static final WorkloadCreationSpecification[] workloadCreationSpecifications = {			

		//Experiments 1,6
		///////////////////////////////////////////////////////////////////////workload size
//			new WorkloadCreationSpecification(50, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(150, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(200, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(250, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(300, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////			

		//Experiment 2, 6b
		///////////////////////////////////////////////////////////////////////pattern length
//			new WorkloadCreationSpecification(100, 3, 5, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 5, 7, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 6, 8, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 7, 9, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
			
		//Experiment 3,3b
		///////////////////////////////////////////////////////////////////////graph density
			new WorkloadCreationSpecification(100, 4, 6, 20,
					  15, null, 2, 
					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
			new WorkloadCreationSpecification(100, 4, 6, 20,
					  30, null, 2, 
					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
			new WorkloadCreationSpecification(100, 4, 6, 20,
					  45, null, 2, 
					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
			new WorkloadCreationSpecification(100, 4, 6, 20,
					  60, null, 2, 
					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
			new WorkloadCreationSpecification(100, 4, 6, 20,
					  75, null, 2, 
					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
			new WorkloadCreationSpecification(100, 4, 6, 20,
			  		  90, null, 2, 
			  		  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
			  		  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
			
		//Experiment 4,4b
		///////////////////////////////////////////////////////////////////////reordering sensitivity
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.LOW, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.HIGH, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////

		//Experiment 5
		///////////////////////////////////////////////////////////////////////local search time limit
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////

		//Experiment 7
		///////////////////////////////////////////////////////////////////////SLA
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.1, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.2, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.3, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.4, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.5, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
			
		//Experiment 8
		///////////////////////////////////////////////////////////////////////workload modification
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.0),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_DYNAMIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_DYNAMIC, 0.5, 0.005),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_DYNAMIC, 0.5, 0.025),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_DYNAMIC, 0.5, 0.125),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
			
		//Experiment 9
		///////////////////////////////////////////////////////////////////////adaptivity
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					  30, null, 2, 
//					  PatternReorderingSensitivityTypes.HIGH, conditionCreatorType, 
//					  0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Experiments 10, 6c
		///////////////////////////////////////////////////////////////////////time windows
//			new WorkloadCreationSpecification(100, 4, 6, 5,
//					30, null, 2, 
//					PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 10,
//					30, null, 2, 
//					PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 15,
//					30, null, 2, 
//					PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 20,
//					30, null, 2, 
//					PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
//			new WorkloadCreationSpecification(100, 4, 6, 25,
//					30, null, 2, 
//					PatternReorderingSensitivityTypes.MEDIUM, conditionCreatorType, 
//					0.0, WorkloadManagerTypes.MULTI_STATIC, 0.5, 0.001),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
	};
	
	
	
	/* Individual specification sets for cross-combining. */
//	public static final EvaluationSpecification[] evaluationSpecifications = {
		
		//Experiment 1
		////////////////////////////////////////all algorithm/neighborhood combinations
//		new IterativeImprovementMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				(long)180, 0, NeighborhoodTypes.STATE_SWAP, 10000, 1000),
//		new IterativeImprovementMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				(long)180, 0, NeighborhoodTypes.PAIRWISE, 10000, 1000),
//		new IterativeImprovementMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				(long)180, 4, NeighborhoodTypes.MULTI_SET, 10000, 1000),
//		new IterativeImprovementMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				(long)180, 8, NeighborhoodTypes.MULTI_SET, 10000, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.STATE_SWAP, (long)180, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.MULTI_SET, (long)180, 4, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.MULTI_SET, (long)180, 8, 10000000, 0.99, 1000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.STATE_SWAP, (long)180, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.MULTI_SET, (long)180, 4, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.MULTI_SET, (long)180, 8, 10000000, 100, 10000),
		/////////////////////////////////////////////////////////////////////////////////////////////////////

		//Experiments 2-4, 7-8, 10
		////////////////////////////////////////selected algorithm/neighborhood combinations
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.MULTI_SET, (long)180, 8, 10000000, 0.99, 1000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.MULTI_SET, (long)180, 8, 10000000, 100, 10000),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Experiments 3b, 4b
		///////////////////////////////////////////////////////////////////////comparison to sharing/reordering only
//		new MultiPlanEvaluationSpecification(MPTCalculatorTypes.NO_SHARING, 
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT)),
//		new MultiPlanEvaluationSpecification(MPTCalculatorTypes.NO_REORDERING, 
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT)),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Experiment 5
		////////////////////////////////////////plan calculation time
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)60, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)120, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)300, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)450, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)600, 0, 10000000, 0.99, 1000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)60, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)120, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)300, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)450, 0, 10000000, 100, 10000),
//		new TabuSearchMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)600, 0, 10000000, 100, 10000),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Experiment 6, 6b, 6c
		///////////////////////////////////////////////////////////////////////comparison to state-of-the-art
//		new MultiPlanEvaluationSpecification(MPTCalculatorTypes.NO_SHARING, 
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT)),
//		new MultiPlanEvaluationSpecification(MPTCalculatorTypes.NO_REORDERING, 
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT)),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.SHARING_DEGREE),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 0.99, 1000),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)180, 0, 10000000, 0.99, 1000),
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Experiment 9
		///////////////////////////////////////////////////////////////////////best-performing combination (real-time)
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new OrderAlgoUnitSpecification(OrderingAlgorithmTypes.GREEDY_COST, CostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)10, 0, 10000000, 0.99, 1000),
		/////////////////////////////////////////////////////////////////////////////////////////////////////

		
		//intentionally commented out multi-tree configurations - if needed, all experiments will be repeated on them
//		new MultiPlanEvaluationSpecification(MPTCalculatorTypes.EXHAUSTIVE,
//				new TreeAlgoUnitSpecification(TopologyCreatorTypes.SELINGER, TreeCostModelTypes.THROUGHPUT)),
//		new IterativeImprovementMPTEvaluationSpecification(
//				new TreeAlgoUnitSpecification(TopologyCreatorTypes.SELINGER, TreeCostModelTypes.THROUGHPUT),
//				(long)20, 5, NeighborhoodTypes.MULTI_SET, 10000000, 100),
//		new SimulatedAnnealingMPTEvaluationSpecification(
//				new TreeAlgoUnitSpecification(TopologyCreatorTypes.SELINGER, TreeCostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)20, 0, 10000000, 0.99, 1000),
//		new TabuSearchMPTEvaluationSpecification(
//				new TreeAlgoUnitSpecification(TopologyCreatorTypes.SELINGER, TreeCostModelTypes.THROUGHPUT),
//				NeighborhoodTypes.PAIRWISE, (long)20, 0, 10000000, 100, 10000),

//	};
			//Maor: what algorithms are going to be run (in my case, I need my own, RIP and Hirzel)
	public static final EvaluationSpecification[] evaluationSpecifications = {


//		new ParallelBasicEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0),
        new ParallelLazyNFAStateDynamicEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
                CostModelTypes.THROUGHPUT_LATENCY,
                0.0, 23, 0.5),
//		new ParallelLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0, 23, 0.5),
//		new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0),
//		new ParallelLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0, 23, 0.5),
//		new RIPEvaluationSpecification(EvaluationMechanismTypes.RIP_CHAIN_NFA, new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0), 23, 0.80, 50873),
//		new RIPEvaluationSpecification(EvaluationMechanismTypes.RIP_CHAIN_NFA, new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0), 23, 0.80, 50873),
//		new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
//				CostModelTypes.THROUGHPUT_LATENCY,
//				0.0),

//			new HirzelEvaluationSpecification(EvaluationMechanismTypes.HIRZEL_CHAIN_NFA, new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
//					CostModelTypes.THROUGHPUT_LATENCY,
//					0.0), Runtime.getRuntime().availableProcessors(), StockEventTypesManager.labelAttributeName),
//			new RIPEvaluationSpecification(EvaluationMechanismTypes.RIP_CHAIN_NFA, new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
//					CostModelTypes.THROUGHPUT_LATENCY,
//					0.0), 23, 0.80, 50873),

////			new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.GREEDY_COST,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						0.0),
//			new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.II_RANDOM,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						0.0),
//			new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.II_GREEDY_COST,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						0.0),
//			new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.DYNAMIC,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						0.0),
//			new TreeEvaluationSpecification(TopologyCreatorTypes.SELINGER,
//											TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0),
//			new TreeEvaluationSpecification(TopologyCreatorTypes.ZSTREAM, 
//											TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0),
//			new TreeEvaluationSpecification(TopologyCreatorTypes.ORDERED_ZSTREAM, 
//											TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0),
//			new EvaluationSpecification(EvaluationMechanismTypes.EAGER),
//			new EvaluationSpecification(OrderingAlgorithmTypes.GREEDY_COST,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						100.0),
//			new EvaluationSpecification(OrderingAlgorithmTypes.II_RANDOM,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						100.0),
//			new EvaluationSpecification(OrderingAlgorithmTypes.II_GREEDY_COST,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						100.0),
//			new EvaluationSpecification(OrderingAlgorithmTypes.DYNAMIC,
//						CostModelTypes.THROUGHPUT_LATENCY,
//						100.0),
//			new EvaluationSpecification(TopologyCreatorTypes.SELINGER,
//										TreeCostModelTypes.THROUGHPUT_LATENCY, 100.0),
//			new EvaluationSpecification(TopologyCreatorTypes.ZSTREAM, 
//										TreeCostModelTypes.THROUGHPUT_LATENCY, 100.0),
//			new EvaluationSpecification(TopologyCreatorTypes.ORDERED_ZSTREAM, 
//										TreeCostModelTypes.THROUGHPUT_LATENCY, 100.0),
//			new EvaluationSpecification(OrderingAlgorithmTypes.GREEDY_ADAPTIVE, CostModelTypes.NONE, 0.0),
//			new EvaluationSpecification(TopologyCreatorTypes.ADAPTIVE_ZSTREAM, TreeCostModelTypes.NONE, 0.0),
	};
		// Maor: what patterns are going to be matched, should start with a simple one
	public static final PatternSpecification[] patternSpecifications = PatternConfig.stockByCompanyPatternSpecifications;
//	public static final AdaptationSpecification[] adaptationSpecifications = {
//		new TrivialAdaptationSpecification(),
//		new ConstantThresholdAdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 0.1),
		
		
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.TRIVIAL, 
//								    InvariantComparerType.NONE, null, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.RECOMPUTE, 
//			    					InvariantComparerType.NONE, null, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//			    					InvariantComparerType.NONE, null, 0.1),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//									InvariantComparerType.NONE, null, 0.25),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//									InvariantComparerType.NONE, null, 0.4),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//									InvariantComparerType.NONE, null, 0.55),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//									InvariantComparerType.NONE, null, 0.7),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//									InvariantComparerType.NONE, null, 0.85),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.PERFORMANCE, 
//									InvariantComparerType.NONE, null, 1.0),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.INVARIANT, 
//			    					InvariantComparerType.TRIVIAL, null, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.INVARIANT, 
//			    					InvariantComparerType.MINIMAL_DISTANCE, 0.1, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.INVARIANT, 
//									InvariantComparerType.MINIMAL_DISTANCE, 0.2, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.INVARIANT, 
//									InvariantComparerType.MINIMAL_DISTANCE, 0.3, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.INVARIANT, 
//									InvariantComparerType.MINIMAL_DISTANCE, 0.4, null),
//		new AdaptationSpecification(statisticsMonitoringWindowToTimeWindowRatio, 0.0001, 
//									AdaptationNecessityDetectorTypes.INVARIANT, 
//									InvariantComparerType.MINIMAL_DISTANCE, 0.5, null),

	private static final long numberOfSyntheticEvents = 100000;
	private static final int numberOfSyntheticEventTypes = 4;

//	public static final InputSpecification[] inputSpecifications = {
			
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				1.0, 1.0,//arrival rates range 
//				0.1, 0.1,//selectivities range
//				0,
//				0.25, 0.0,//arrival rates change range
//				0.025, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				0),//no input modification
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.75, 1.5,//arrival rates range 
//				0.075, 0.15,//selectivities range
//				0,
//				0.25, 0.0,//arrival rates change range
//				0.025, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.5, 2.0,//arrival rates range 
//				0.05, 0.2,//selectivities range
//				0,
//				0.25, 0.0,//arrival rates change range
//				0.025, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.33, 3.0,//arrival rates range 
//				0.033, 0.3,//selectivities range
//				0,
//				0.25, 0.0,//arrival rates change range
//				0.025, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.25, 4.0,//arrival rates range 
//				0.025, 0.5,//selectivities range
//				0,
//				0.25, 0.0,//arrival rates change range
//				0.025, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				0.25, 0.0,//arrival rates change range
//				0.025, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.0, 0.0,//arrival rates change range
//				0.1, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.75, 0.0,//arrival rates change range
//				0.175, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				2.5, 0.0,//arrival rates change range
//				0.25, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				3.25, 0.0,//arrival rates change range
//				0.325, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				4.0, 0.0,//arrival rates change range
//				0.4, 0.0,//selectivities change range 
//				1000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.0, 0.0,//arrival rates change range
//				0.1, 0.0,//selectivities change range 
//				50, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.0, 0.0,//arrival rates change range
//				0.1, 0.0,//selectivities change range 
//				100, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.0, 0.0,//arrival rates change range
//				0.1, 0.0,//selectivities change range 
//				500, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.0, 0.0,//arrival rates change range
//				0.1, 0.0,//selectivities change range 
//				5000, 0,//time between input changes
//				1),
//		new InputSpecification(numberOfSyntheticEvents, numberOfSyntheticEventTypes,
//				0.2, 5.0,//arrival rates range 
//				0.02, 0.5,//selectivities range
//				0,
//				1.0, 0.0,//arrival rates change range
//				0.1, 0.0,//selectivities change range 
//				10000, 0,//time between input changes
//				1),
//	};
	
	/* Settings for performing complex simulations, comparing between several running modes. */
	public static final SimulationSpecification[] specifications = {
			
		/* Testing. */
//		new SimulationSpecification(PatternConfig.testSequenceOfFive,
//			    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    					 		LazyNFANegationTypes.NONE,
//			    					 		OrderingAlgorithmTypes.DYNAMIC,
//			    					 		CostModelTypes.THROUGHPUT_LATENCY,
//			    					 		0.0)),

		new SimulationSpecification(new SinglePatternWorkloadSpecification(PatternConfig.testSequence),
				new ParallelLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.DYNAMIC,
															CostModelTypes.THROUGHPUT_LATENCY,
															0.0, Runtime.getRuntime().availableProcessors(), 0.5)),
		new SimulationSpecification(new SinglePatternWorkloadSpecification(PatternConfig.testSequence),
									new TreeEvaluationSpecification(TopologyCreatorTypes.SELINGER, 
																	TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0)),

			
		/* Sequence */
//		new SimulationSpecification(PatternConfig.sequenceOfThree,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfThree,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 		EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeNAEUCA,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeNAEUCA,
//					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 EvaluationOrderConfig.orderOfThreeNAEUCA)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeEUASCA,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeEUASCA,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfThreeEUASCA)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourLazy,
//					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 EvaluationOrderConfig.orderOfFour)),
//		new SimulationSpecification(PatternConfig.sequenceOfFiveEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfFiveLazy,
//					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 EvaluationOrderConfig.orderOfFive)),
////		new SimulationSpecification(PatternConfig.sequenceOfSixEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.sequenceOfSixLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfSix)),
////		new SimulationSpecification(PatternConfig.sequenceOfSevenEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.sequenceOfSevenLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfSeven)),
////		new SimulationSpecification(PatternConfig.sequenceOfEightEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.sequenceOfEightLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfEight)),
//		
//		/* Negation */
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThree,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThree,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 LazyNFANegationTypes.POST_PROCESSING,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThree,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 LazyNFANegationTypes.FIRST_CHANCE,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThreeEUFirst,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThreeEUFirst,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 LazyNFANegationTypes.POST_PROCESSING,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThreeEUFirst,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 LazyNFANegationTypes.FIRST_CHANCE,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThreeEULast,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThreeEULast,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//		    					 			 			 LazyNFANegationTypes.POST_PROCESSING,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfThreeEULast,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								 			 			 LazyNFANegationTypes.FIRST_CHANCE,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeConjunctionOfThree,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.negativeConjunctionOfThree,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								 			 			 LazyNFANegationTypes.POST_PROCESSING,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeConjunctionOfThree,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								 			 			 LazyNFANegationTypes.FIRST_CHANCE,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfFiveEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfFiveLazy,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								 			 			 LazyNFANegationTypes.POST_PROCESSING,
//								    					 EvaluationOrderConfig.orderOfFiveForNegation)),
//		new SimulationSpecification(PatternConfig.negativeSequenceOfFiveLazy,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								 			 			 LazyNFANegationTypes.FIRST_CHANCE,
//								    					 EvaluationOrderConfig.orderOfFiveForNegation)),
////		new SimulationSpecification(PatternConfig.negativeSequenceOfEightEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.negativeSequenceOfEightLazy,
////								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////								 			 			 LazyNFANegationTypes.POST_PROCESSING,
////								    					 EvaluationOrderConfig.orderOfEight)),
////		new SimulationSpecification(PatternConfig.negativeSequenceOfEightLazy,
////								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////								 			 			 LazyNFANegationTypes.FIRST_CHANCE,
////								    					 EvaluationOrderConfig.orderOfEight)),
//
//		/* Disjunction */
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfTwo,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfTwo,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfFour)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfTwoNACAEUSA,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfTwoNACAEUSA,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfFourNACAEUSA)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfTwoEUASCAME,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfTwoEUASCAME,
//						    		new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//						    					 		 EvaluationOrderConfig.orderOfFourEUASCAME)),
//		new SimulationSpecification(PatternConfig.disjunctionOfThreeSequencesOfTwo,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfThreeSequencesOfTwo,
//					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 EvaluationOrderConfig.orderOfSix)),
//		new SimulationSpecification(PatternConfig.disjunctionOfFourSequencesOfTwo,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfFourSequencesOfTwo,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfEight)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfThreeEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfThreeLazy,
//					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 EvaluationOrderConfig.orderOfSix)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfFourEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.disjunctionOfTwoSequencesOfFourLazy,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfEight)),
//			
//		/* Conjunction */
//		new SimulationSpecification(PatternConfig.conjunctionOfTwo,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.conjunctionOfTwo,
//					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//					    					 			 EvaluationOrderConfig.orderOfTwo)),
//		new SimulationSpecification(PatternConfig.conjunctionOfThree,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.conjunctionOfThree,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfThree)),
//		new SimulationSpecification(PatternConfig.conjunctionOfThreeEUSAAFR,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.conjunctionOfThreeEUSAAFR,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfThreeEUSAAFR)),
//		new SimulationSpecification(PatternConfig.conjunctionOfThreeCASAAU,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.conjunctionOfThreeCASAAU,
//			    					new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    					 		 			 EvaluationOrderConfig.orderOfThreeCASAAU)),
////		new SimulationSpecification(PatternConfig.conjunctionOfFourEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfFourLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfFour)),
////		new SimulationSpecification(PatternConfig.conjunctionOfFiveEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfFiveLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfFive)),
////		new SimulationSpecification(PatternConfig.conjunctionOfSixEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfSixLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfSix)),
////		new SimulationSpecification(PatternConfig.conjunctionOfSevenEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfSevenLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfSeven)),
////		new SimulationSpecification(PatternConfig.conjunctionOfEightEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfEightLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfEight)),
//
//		/* Conjunction-Sequence */
////		new SimulationSpecification(PatternConfig.conjunctionOfTwoSequencesEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfTwoSequencesLazy,
////								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////								    					 EvaluationOrderConfig.orderOfFour)),
////		new SimulationSpecification(PatternConfig.conjunctionOfFourSequencesEager,
////									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
////		new SimulationSpecification(PatternConfig.conjunctionOfFourSequencesLazy,
////					    			new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
////					    					 			 EvaluationOrderConfig.orderOfEight)),
//		
//		/* Iteration */
//		new SimulationSpecification(PatternConfig.sequenceOfThreeWithIteration,
//								    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//								    					 EvaluationOrderConfig.orderOfThreeForIteration)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeWithIteration,
//				new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		
//		
//		/* Equality patterns for SASE comparison. */
//		new SimulationSpecification(PatternConfig.sequenceOfThreeWithEquation,
//			    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    					 EvaluationOrderConfig.orderOfThreeForEqualitySequence)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourWithNegation,
//			    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			 			 			 LazyNFANegationTypes.FIRST_CHANCE,
//			    					 EvaluationOrderConfig.orderOfFourForEqualityNegation)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeWithEquationIteration,
//			    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    					 EvaluationOrderConfig.orderOfThreeForEqualityIteration)),

			
			
			/* Tram congestion patterns. */
//			new SimulationSpecification(PatternConfig.sequenceOfThreeCongestions,
//										new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//			new SimulationSpecification(PatternConfig.sequenceOfThreeCongestions,
//				    					new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//				    										 EvaluationOrderConfig.orderOfThreeCongestions)),
//			new SimulationSpecification(PatternConfig.sequenceOfFourCongestions,
//										new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//			new SimulationSpecification(PatternConfig.sequenceOfFourCongestions,
//										new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//												 			 EvaluationOrderConfig.orderOfFourCongestions)),
//			new SimulationSpecification(PatternConfig.sequenceOfFiveCongestions,
//										new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//			new SimulationSpecification(PatternConfig.sequenceOfFiveCongestions,
//										new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//												 			 EvaluationOrderConfig.orderOfFiveCongestions)),
		
		/* Traffic speed patterns. */
//		new SimulationSpecification(PatternConfig.sequenceOfThreeSpeedMeasurementsEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeSpeedMeasurementsLazy,
//			    					new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    										 EvaluationOrderConfig.orderOfThreeSpeedMeasurements)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourSpeedMeasurementsEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourSpeedMeasurementsLazy,
//									new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//											 			 EvaluationOrderConfig.orderOfFourSpeedMeasurements)),
//		new SimulationSpecification(PatternConfig.sequenceOfFiveSpeedMeasurementsEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfFiveSpeedMeasurementsLazy,
//									new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//											 			 EvaluationOrderConfig.orderOfFiveSpeedMeasurements)),

		
		/* Credit card fraud patterns. */
//		new SimulationSpecification(PatternConfig.sequenceOfThreeTransactionsEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfThreeTransactionsLazy,
//			    					new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    										 EvaluationOrderConfig.orderOfThreeTransactions)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourTransactionsEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfFourTransactionsLazy,
//									new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//											 			 EvaluationOrderConfig.orderOfFourTransactions)),
//		new SimulationSpecification(PatternConfig.sequenceOfFiveTransactionsEager,
//									new EvaluationSpecification(EvaluationMechanismTypes.EAGER)),
//		new SimulationSpecification(PatternConfig.sequenceOfFiveTransactionsLazy,
//									new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//											 			 EvaluationOrderConfig.orderOfFiveTransactions)),

	};

}
