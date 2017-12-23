package adaptive.monitoring;

import adaptive.monitoring.invariant.InvariantAdaptationNecessityDetector;
import adaptive.monitoring.invariant.compare.InvariantComparerType;
import pattern.Pattern;

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
