package sase.adaptive.estimation;

import sase.base.EventType;

public interface IEventArrivalRateEstimator {

	public void registerEventArrival(EventType type);
	public double getEventRateEstimate(EventType type);
}
