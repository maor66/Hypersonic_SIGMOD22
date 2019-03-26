package sase.specification.adaptation;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;

public class PerformanceDeltaAdaptationSpecification extends AdaptationSpecification {

	public Double minThroughputDifferenceRatio;
	
	public PerformanceDeltaAdaptationSpecification(Integer statisticsMonitoringWindowToTimeWindowRatio, Double maxError,
												   Double minThroughputDifferenceRatio) {
		super(AdaptationNecessityDetectorTypes.PERFORMANCE, statisticsMonitoringWindowToTimeWindowRatio, maxError);
		this.minThroughputDifferenceRatio = minThroughputDifferenceRatio;
	}

	@Override
	protected String getInternalShortDescription() {
		return String.format(";thrpt=%f", minThroughputDifferenceRatio);
	}

	@Override
	protected String getInternalLongDescription() {
		return String.format("minimal throughput diff: %f", minThroughputDifferenceRatio);
	}
}
