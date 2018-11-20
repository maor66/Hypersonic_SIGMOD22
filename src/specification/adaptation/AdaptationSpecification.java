package sase.specification.adaptation;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;

public abstract class AdaptationSpecification {
	
	public Integer statisticsMonitoringWindowToTimeWindowRatio;
	public Double maxError;
	public AdaptationNecessityDetectorTypes adaptationDetectorType;

	public AdaptationSpecification(AdaptationNecessityDetectorTypes adaptationDetectorType,
								   Integer statisticsMonitoringWindowToTimeWindowRatio, Double maxError) {
		this.statisticsMonitoringWindowToTimeWindowRatio = statisticsMonitoringWindowToTimeWindowRatio;
		this.maxError = maxError;
		this.adaptationDetectorType = adaptationDetectorType;
	}

	public String getShortDescription() {
		String monitoringDescriptionStr = 
				String.format("det=%s;", adaptationDetectorType) + getInternalShortDescription();
		if (statisticsMonitoringWindowToTimeWindowRatio == null) {
			return monitoringDescriptionStr;
		}
		return String.format("(W=%d;e=%f)%s", statisticsMonitoringWindowToTimeWindowRatio, maxError, monitoringDescriptionStr);
	}
	
	public String getLongDescription() {
		return String.format("adaptation window (in pattern time windows): %d; maximal error: %f; adaptation detector: %s",
							 statisticsMonitoringWindowToTimeWindowRatio, maxError, adaptationDetectorType) + 
				getInternalLongDescription();
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}
	
	protected abstract String getInternalShortDescription();
	protected abstract String getInternalLongDescription();
}
