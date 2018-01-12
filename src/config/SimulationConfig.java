package config;

import specification.AdaptationSpecification;
import specification.EvaluationSpecification;
import specification.InputSpecification;
import specification.PatternSpecification;
import specification.SimulationSpecification;
import specification.creators.SpecificationCreatorTypes;
import specification.creators.condition.ConditionSpecificationCreatorTypes;
import specification.creators.condition.ConditionSpecificationSetCreatorTypes;
import user.stocks.condition.StockFirstValueCmpCondition.ComparisonOperation;
import adaptive.monitoring.AdaptationNecessityDetectorTypes;
import adaptive.monitoring.invariant.compare.InvariantComparerType;
import base.Event; //this dummy import is needed to avoid the annoying 'unused warning suppression' message
import evaluation.EvaluationMechanismTypes;
import evaluation.nfa.lazy.LazyNFANegationTypes;
import evaluation.tree.TopologyCreatorTypes;
import evaluation.tree.TreeCostModelTypes;
import order.OrderingAlgorithmTypes;
import order.cost.CostModelTypes;
import pattern.Pattern.PatternOperatorType;

@SuppressWarnings("unused")
public class SimulationConfig {
	
	//specification creator definition
	public static final SpecificationCreatorTypes specificationCreatorType = 
																SpecificationCreatorTypes.NONE;
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
	public static final int[] patternSizes = {7};
	public static final int patternsPerLength = 5;
	public static final Long timeWindowForPatternGenerator = 25L;
	public static final PatternOperatorType mainOperatorType = PatternOperatorType.AND_SEQ;
	
	public static final int negatedEventsNumber = 0;
	public static final double negatedConditionProbability = 1.0;
	
	public static final int iteratedEventsNumber = 0;
	public static final ComparisonOperation triggerComparisonOperator = ComparisonOperation.EQUALS;
	public static final ComparisonOperation iterativeComparisonOperator = ComparisonOperation.EQUALS;
	public static final ComparisonOperation terminatorComparisonOperator = ComparisonOperation.EQUALS;
	
	public static final int numberOfDisjunctions = 3;
	
	public static final int[] numbersOfConditions = {1,2,3,4,4};
	public static final double[] correlations = {0.5,0.6,0.7,0.8,0.9};
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Individual specification sets for cross-combining. */
	public static final EvaluationSpecification[] evaluationSpecifications = {
			new EvaluationSpecification(OrderingAlgorithmTypes.EVENT_FREQUENCY,
						CostModelTypes.THROUGHPUT_LATENCY,
						0.0),
			new EvaluationSpecification(OrderingAlgorithmTypes.GREEDY_COST,
						CostModelTypes.THROUGHPUT_LATENCY,
						0.0),
			new EvaluationSpecification(OrderingAlgorithmTypes.II_RANDOM,
						CostModelTypes.THROUGHPUT_LATENCY,
						0.0),
			new EvaluationSpecification(OrderingAlgorithmTypes.II_GREEDY_COST,
						CostModelTypes.THROUGHPUT_LATENCY,
						0.0),
			new EvaluationSpecification(OrderingAlgorithmTypes.DYNAMIC,
						CostModelTypes.THROUGHPUT_LATENCY,
						0.0),
			new EvaluationSpecification(TopologyCreatorTypes.SELINGER,
										TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0),
			new EvaluationSpecification(TopologyCreatorTypes.ZSTREAM, 
										TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0),
			new EvaluationSpecification(TopologyCreatorTypes.ORDERED_ZSTREAM, 
										TreeCostModelTypes.THROUGHPUT_LATENCY, 0.0),
			new EvaluationSpecification(EvaluationMechanismTypes.EAGER),
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
	public static final PatternSpecification[] patternSpecifications = PatternConfig.stockByCompanyPatternSpecifications;
	private static final int statisticsMonitoringWindowToTimeWindowRatio = 2;
	public static final AdaptationSpecification[] adaptationSpecifications = {
		new AdaptationSpecification(),
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
	};
	private static final long numberOfSyntheticEvents = 100000;
	private static final int numberOfSyntheticEventTypes = 4;
	public static final InputSpecification[] inputSpecifications = {
		new InputSpecification(),
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
	};
	
	/* Settings for performing complex simulations, comparing between several running modes. */
	public static final SimulationSpecification[] specifications = {
			
		/* Testing. */
//		new SimulationSpecification(PatternConfig.testSequenceOfFive,
//			    new EvaluationSpecification(EvaluationMechanismTypes.LAZY_CHAIN,
//			    					 		LazyNFANegationTypes.NONE,
//			    					 		OrderingAlgorithmTypes.DYNAMIC,
//			    					 		CostModelTypes.THROUGHPUT_LATENCY,
//			    					 		0.0)),

		new SimulationSpecification(PatternConfig.testSequence,
				new EvaluationSpecification(OrderingAlgorithmTypes.DYNAMIC,
											CostModelTypes.THROUGHPUT_LATENCY,
											0.0)),
		new SimulationSpecification(PatternConfig.testSequence,
									new EvaluationSpecification(TopologyCreatorTypes.SELINGER, 
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
//		/* Equality patterns for comparison. */
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
