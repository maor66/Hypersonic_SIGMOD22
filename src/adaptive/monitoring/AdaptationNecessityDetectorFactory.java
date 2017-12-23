package sase.adaptive.monitoring;

import sase.adaptive.monitoring.invariant.InvariantAdaptationNecessityDetector;
import sase.adaptive.monitoring.invariant.compare.InvariantComparerType;
import sase.pattern.Pattern;

public class AdaptationNecessityDetectorFactory {

	public AdaptationNecessityDetector createAdaptationNecessityDetector(AdaptationNecessityDetectorTypes type,
																		 Pattern pattern,
																		 InvariantComparerType comparerType,
																		 Double minimalDistance,
																		 Double minThroughputDifferenceRatio) {
		switch(type) {
			case TRIVIAL:
				return new TrivialAdaptationNecessityDetector();
			case INVARIANT:
				return new InvariantAdaptationNecessityDetector(comparerType, minimalDistance);
			case PERFORMANCE:
				return new PerformanceDeltaAdaptationNecessityDetector(minThroughputDifferenceRatio);
			case RECOMPUTE:
				return new PlanCreatorBasedAdaptationNecessityDetector(pattern);
			case NONE:
			default:
				return null;
		}
	}
}
