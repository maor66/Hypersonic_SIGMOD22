package sase.evaluation.nfa.lazy.order.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sase.base.EventType;
import sase.config.EventRateConfig;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.Pattern;

public class EventFrequencyOrderingAlgorithm implements IOrderingAlgorithm {
	
	private class EventTypeComparator implements Comparator<EventType> {
//Maor: This is the frequency comparator
		@Override
		public int compare(EventType firstEventType, EventType secondEventType) {
			Double firstFrequency = EventRateConfig.eventRate.get(firstEventType.getName());
			Double secondFrequency = EventRateConfig.eventRate.get(secondEventType.getName());
			if (firstFrequency - secondFrequency > 0) {
				return 1;
			}
			else if (firstFrequency - secondFrequency < 0) {
				return -1;
			}
			return 0;
		}
		
	}

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		List<EventType> eventTypes = new ArrayList<EventType>(pattern.getEventTypes());
		Collections.sort(eventTypes, new EventTypeComparator());
		return eventTypes;
	}

}
