package sase.adaptive.monitoring;

import java.util.List;

import sase.adaptive.monitoring.invariant.InvariantAdaptationNecessityDetector;
import sase.pattern.Pattern;
import sase.specification.adaptation.AdaptationSpecification;
import sase.specification.adaptation.ConstantThresholdAdaptationSpecification;
import sase.specification.adaptation.InvariantAdaptationSpecification;
import sase.specification.adaptation.PerformanceDeltaAdaptationSpecification;

public class AdaptationNecessityDetectorFactory {

	public static IAdaptationNecessityDetector createAdaptationNecessityDetector(AdaptationSpecification specification,
																		 		List<Pattern> workload) {
		switch(specification.adaptationDetectorType) {
			case TRIVIAL:
				return new TrivialAdaptationNecessityDetector();
			case INVARIANT:
				InvariantAdaptationSpecification invariantSpecification = (InvariantAdaptationSpecification)specification;
				return new InvariantAdaptationNecessityDetector(invariantSpecification.comparerType, 
																invariantSpecification.minimalInvariantRelativeDistance);
			case PERFORMANCE:
				PerformanceDeltaAdaptationSpecification performanceSpecification = 
																(PerformanceDeltaAdaptationSpecification)specification;
				return new PerformanceDeltaAdaptationNecessityDetector(performanceSpecification.minThroughputDifferenceRatio);
			case RECOMPUTE:
				if (workload.size() != 1) {
					throw new RuntimeException("The selected adaptation mechanism cannot be applied in sase.multi-sase.pattern mode");
				}
				return new PlanCreatorBasedAdaptationNecessityDetector(workload.get(0));
			case CONSTANT_THRESHOLD:
				ConstantThresholdAdaptationSpecification constantThresholdSpecification = 
																(ConstantThresholdAdaptationSpecification)specification;
				return new ConstantThresholdAdaptationNecessityDetector(workload, constantThresholdSpecification.threshold);
			case NONE:
			default:
				return null;
		}
	}
}
