package adaptive.estimation;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import config.EventRateConfig;
import config.MainConfig;
import pattern.EventTypesManager;

public class SlidingWindowEventArrivalRateEstimator {

	private class EventRateEstimator {
		public EventType type;
		public ExponentialHistogramsCounter estimator;
		
		public EventRateEstimator(EventType type, ExponentialHistogramsCounter estimator) {
			this.type = type;
			this.estimator = estimator;
		}
	}

	private static final int defaultEventTypeRate = 1;
	
	private List<EventRateEstimator> estimators;
	private final long recordedEventsLowerBound;

	public SlidingWindowEventArrivalRateEstimator(long windowSize, double maxError, int numberOfEventTypes) {
		this.recordedEventsLowerBound = windowSize * numberOfEventTypes;
		
		estimators = new ArrayList<EventRateEstimator>();
		for (EventType eventType : EventTypesManager.getInstance().getKnownEventTypes()) {
			EventRateEstimator estimator = new EventRateEstimator(eventType, 
																  new ExponentialHistogramsCounter(windowSize, maxError));
			estimators.add(estimator);
		}
	}
	
	public void registerEventArrival(EventType type) {
		for (EventRateEstimator eventRateEstimator : estimators) {
			eventRateEstimator.estimator.addElement(eventRateEstimator.type == type);
		}
	}
	
	public int getEventRateEstimate(EventType type) {
		if (!MainConfig.isArrivalRateMonitoringAllowed) {
			return EventRateConfig.eventRate.get(type);
		}
		for (EventRateEstimator eventRateEstimator : estimators) {
			if (eventRateEstimator.type == type) {
				if (eventRateEstimator.estimator.getNumberOfRecordedElements() < (long)recordedEventsLowerBound) {
					return EventRateConfig.eventRate.getOrDefault(type, defaultEventTypeRate);
				}
				return eventRateEstimator.estimator.getSumEstimate();
			}
		}
		throw new RuntimeException(String.format("Event type %s was not found", type));
	}
}
