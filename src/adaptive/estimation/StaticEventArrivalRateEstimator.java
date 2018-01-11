package sase.adaptive.estimation;

import sase.base.EventType;
import sase.config.EventRateConfig;

public class StaticEventArrivalRateEstimator implements IEventArrivalRateEstimator {

	@Override
	public void registerEventArrival(EventType type) {
	}

	@Override
	public int getEventRateEstimate(EventType type) {
		return EventRateConfig.eventRate.get(type.getName());
	}

}
