package sase.multi.calculator;

import sase.multi.algo.AlgoUnitFactory;
import sase.multi.algo.IAlgoUnit;
import sase.multi.calculator.local.IterativeImprovementMPTCalculator;
import sase.multi.calculator.local.SimulatedAnnealingMPTCalculator;
import sase.multi.calculator.local.TabuSearchMPTCalculator;
import sase.specification.evaluation.IterativeImprovementMPTEvaluationSpecification;
import sase.specification.evaluation.MultiPlanEvaluationSpecification;
import sase.specification.evaluation.SimulatedAnnealingMPTEvaluationSpecification;
import sase.specification.evaluation.TabuSearchMPTEvaluationSpecification;

public class MPTCalculatorFactory {

	public static IMPTCalculator createMPTCalculator(MultiPlanEvaluationSpecification specification) {
		IAlgoUnit algoUnit = AlgoUnitFactory.createAlgoUnit(specification.algoUnitSpecification);
		switch (specification.mptCalculatorType) {
			case ITERATIVE_IMPROVEMENT:
				IterativeImprovementMPTEvaluationSpecification iterativeImprovementSpecification = 
										(IterativeImprovementMPTEvaluationSpecification)specification;
				return new IterativeImprovementMPTCalculator(iterativeImprovementSpecification.neighborhoodType,
															 algoUnit,
															 iterativeImprovementSpecification.timeLimitInSeconds,
															 iterativeImprovementSpecification.multiSetParameter,
															 iterativeImprovementSpecification.maxSteps,
															 iterativeImprovementSpecification.maxNeighbors);
			case NO_REORDERING:
				return new NoReorderingMPTCalculator(algoUnit);
			case NO_SHARING:
				return new NoSharingMPTCalculator(algoUnit);
			case EXHAUSTIVE:
				return new ExhaustiveMPTCalculator(algoUnit);
			case SIMULATED_ANNEALING:
				SimulatedAnnealingMPTEvaluationSpecification simulatedAnnealingSpecification = 
					(SimulatedAnnealingMPTEvaluationSpecification)specification;
				return new SimulatedAnnealingMPTCalculator(simulatedAnnealingSpecification.neighborhoodType, 
														   algoUnit, 
														   simulatedAnnealingSpecification.timeLimitInSeconds,
														   simulatedAnnealingSpecification.multiSetParameter,
														   simulatedAnnealingSpecification.maxStepsSinceImprovement, 
														   simulatedAnnealingSpecification.alpha, 
														   simulatedAnnealingSpecification.numOfNeighborsForTempInit);
			case TABU_SEARCH:
				TabuSearchMPTEvaluationSpecification tabuSearchSpecification =
						(TabuSearchMPTEvaluationSpecification)specification;
				return new TabuSearchMPTCalculator(tabuSearchSpecification.neighborhoodType, 
												   algoUnit, 
												   tabuSearchSpecification.timeLimitInSeconds, 
												   tabuSearchSpecification.multiSetParameter,
												   tabuSearchSpecification.maxStepsSinceImprovement, 
												   tabuSearchSpecification.neighborsPerStep, 
												   tabuSearchSpecification.memorySize);
			default:
				throw new RuntimeException("Unexpected MPT calculator type");
		}
	}

}
