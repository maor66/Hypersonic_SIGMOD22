package sase.specification.adaptation;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;

public class PlanCreatorBasedAdaptationSpecification extends AdaptationSpecification {

	public PlanCreatorBasedAdaptationSpecification(Integer statisticsMonitoringWindowToTimeWindowRatio, Double maxError) {
		super(AdaptationNecessityDetectorTypes.RECOMPUTE, statisticsMonitoringWindowToTimeWindowRatio, maxError);
	}

	@Override
	protected String getInternalShortDescription() {
		return "";
	}

	@Override
	protected String getInternalLongDescription() {
		return "";
	}
}
