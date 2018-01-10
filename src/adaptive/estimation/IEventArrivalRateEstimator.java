package sase.adaptive.estimation;

import sase.base.EventType;

public interface IEventArrivalRateEstimator {

	public void registerEventArrival(EventType type);
	public int getEventRateEstimate(EventType type);
}
