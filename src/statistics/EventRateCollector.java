package sase.statistics;

import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;

public class EventRateCollector {

	private HashMap<EventType, Integer> counters;
	private long timeWindow;
	private Long timeWindowStart = null;
	private int timeWindowsCounter = 0;
	
	public EventRateCollector(List<EventType> types, long timeWindow) {
		counters = new HashMap<EventType, Integer>();
		for (EventType eventType : types) {
			counters.put(eventType, 0);
		}
		this.timeWindow = timeWindow;
	}
	
	public void recordEvent(Event event) {
		if (!counters.containsKey(event.getType())) {
			return;
		}
		if (timeWindowStart == null || event.getTimestamp() - timeWindowStart > timeWindow) {
			++timeWindowsCounter;
			timeWindowStart = event.getTimestamp();
		}
		counters.put(event.getType(), counters.get(event.getType()) + 1);
	}
	
	public HashMap<EventType, Integer> getEventRates() {
		++timeWindowsCounter; //last time window
		HashMap<EventType, Integer> rates = new HashMap<EventType, Integer>();
		for (EventType eventType : counters.keySet()) {
			rates.put(eventType, counters.get(eventType) / timeWindowsCounter);
		}
		return rates;
	}

}
