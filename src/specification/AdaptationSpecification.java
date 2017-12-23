package sase.specification;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;
import sase.adaptive.monitoring.invariant.compare.InvariantComparerType;

public class AdaptationSpecification {
	
	public Integer statisticsMonitoringWindowToTimeWindowRatio;
	public Double maxError;
	public AdaptationNecessityDetectorTypes adaptationDetectorType;
	public InvariantComparerType comparerType;
	public Double minimalInvariantRelativeDistance;
	public Double minThroughputDifferenceRatio;

	public AdaptationSpecification(Integer statisticsMonitoringWindowToTimeWindowRatio, Double maxError,
								   AdaptationNecessityDetectorTypes adaptationDetectorType,
								   InvariantComparerType comparerType,
								   Double minimalInvariantRelativeDistance,
								   Double minThroughputDifferenceRatio) {
		this.statisticsMonitoringWindowToTimeWindowRatio = statisticsMonitoringWindowToTimeWindowRatio;
		this.maxError = maxError;
		this.adaptationDetectorType = adaptationDetectorType;
		this.comparerType = comparerType;
		this.minimalInvariantRelativeDistance = minimalInvariantRelativeDistance;
		this.minThroughputDifferenceRatio = minThroughputDifferenceRatio;
	}
	
	public AdaptationSpecification() {
		this(null, null, AdaptationNecessityDetectorTypes.NONE, InvariantComparerType.NONE, null, null);
	}

	public String getShortDescription() {
		String monitoringDescriptionStr = String.format("(det=%s;cmp=%s;dist=%f;thrpt=%f)",
														adaptationDetectorType, comparerType,
														minimalInvariantRelativeDistance, minThroughputDifferenceRatio);
		if (statisticsMonitoringWindowToTimeWindowRatio == null) {
			return monitoringDescriptionStr;
		}
		return String.format("(W=%d;e=%f)%s", statisticsMonitoringWindowToTimeWindowRatio, maxError, monitoringDescriptionStr);
	}
	
	public String getLongDescription() {
		return String.format("adaptation window (in pattern time windows): %d; maximal error: %f; " +
							 "adaptation detector: %s; comparer type: %s; minimal distance: %f; minimal throughput diff: %f",
							 statisticsMonitoringWindowToTimeWindowRatio, maxError,
							 adaptationDetectorType, comparerType, 
							 minimalInvariantRelativeDistance, minThroughputDifferenceRatio);
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}
}
