package sase.pattern.condition.contiguity;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.pattern.condition.base.AtomicCondition;

public class TotalContiguityCondition extends AtomicCondition {

	private static final double conditionSelectivity = 1.0;
	
	public TotalContiguityCondition() {
		super(conditionSelectivity);
	}

	@Override
	protected boolean actuallyVerify(List<Event> events) {
		if (events.size() < 2) {
			return true;
		}
		List<Event> orderedEvents = new ArrayList<Event>(events);
		orderedEvents.sort(null);
		for (int i = 0; i < orderedEvents.size() - 1; ++i) {
			long firstSequenceNumber = orderedEvents.get(i).getSequenceNumber();
			long secondSequenceNumber = orderedEvents.get(i+1).getSequenceNumber();
			if (firstSequenceNumber != secondSequenceNumber - 1) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public String toString() {
		return "Total contiguity on all types";
	}

}
