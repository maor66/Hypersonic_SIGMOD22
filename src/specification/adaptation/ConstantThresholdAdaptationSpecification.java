package sase.specification.adaptation;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;

public class ConstantThresholdAdaptationSpecification extends AdaptationSpecification {

	public Double threshold;
	
	public ConstantThresholdAdaptationSpecification(Integer statisticsMonitoringWindowToTimeWindowRatio, Double maxError,
													Double threshold) {
		super(AdaptationNecessityDetectorTypes.CONSTANT_THRESHOLD, statisticsMonitoringWindowToTimeWindowRatio, maxError);
		this.threshold = threshold;
	}

	@Override
	protected String getInternalShortDescription() {
		return String.format(";thrhd=%f", threshold);
	}

	@Override
	protected String getInternalLongDescription() {
		return String.format("statistic deviation threshold: %f", threshold);
	}

}
