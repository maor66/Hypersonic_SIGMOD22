package sase.specification.adaptation;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;
import sase.adaptive.monitoring.invariant.compare.InvariantComparerType;

public class InvariantAdaptationSpecification extends AdaptationSpecification {

	public InvariantComparerType comparerType;
	public Double minimalInvariantRelativeDistance;
	
	public InvariantAdaptationSpecification(Integer statisticsMonitoringWindowToTimeWindowRatio, Double maxError,
											InvariantComparerType comparerType, Double minimalInvariantRelativeDistance) {
		super(AdaptationNecessityDetectorTypes.INVARIANT, statisticsMonitoringWindowToTimeWindowRatio, maxError);
		this.comparerType = comparerType;
		this.minimalInvariantRelativeDistance = minimalInvariantRelativeDistance;
	}

	@Override
	protected String getInternalShortDescription() {
		return String.format(";cmp=%s;dist=%f", comparerType, minimalInvariantRelativeDistance);
	}

	@Override
	protected String getInternalLongDescription() {
		return String.format("; comparer type: %s; minimal distance: %f", comparerType, minimalInvariantRelativeDistance);
	}
}
