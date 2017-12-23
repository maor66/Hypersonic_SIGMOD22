package sase.order.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sase.base.EventType;
import sase.config.EventRateConfig;
import sase.order.IOrderingAlgorithm;
import sase.order.cost.ICostModel;
import sase.pattern.Pattern;

public class EventFrequencyOrderingAlgorithm implements IOrderingAlgorithm {
	
	private class EventTypeComparator implements Comparator<EventType> {

		@Override
		public int compare(EventType firstEventType, EventType secondEventType) {
			Integer firstFrequency = EventRateConfig.eventRate.get(firstEventType.getName());
			Integer secondFrequency = EventRateConfig.eventRate.get(secondEventType.getName());
			return firstFrequency - secondFrequency;
		}
		
	}

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		List<EventType> eventTypes = new ArrayList<EventType>(pattern.getEventTypes());
		Collections.sort(eventTypes, new EventTypeComparator());
		return eventTypes;
	}

}
