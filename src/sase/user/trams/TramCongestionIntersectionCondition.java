package sase.user.trams;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * Represents a condition on a correlation rate between appearances of two given primitive events.
 */
public class TramCongestionIntersectionCondition extends DoubleEventCondition {
	
	public TramCongestionIntersectionCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	private HashSet<String> getEventLineNumbers(Event event) {
		String lineNumbersStr = (String)event.getAttributeValue(TramEventTypesManager.lineNumbersAttributeName);
		List<String> lineNumbersList = Arrays.asList(lineNumbersStr.split(TramEventTypesManager.lineNumbersSeparator));
		return new HashSet<String>(lineNumbersList);
	}
	
	@Override
    public boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		HashSet<String> firstEventLineNumbers = getEventLineNumbers(firstEvent);
		HashSet<String> secondEventLineNumbers = getEventLineNumbers(secondEvent);
		firstEventLineNumbers.retainAll(secondEventLineNumbers);
		return firstEventLineNumbers.size() > 0;
	}
	
	@Override
	public String toString() {
		return String.format("Congestion intersection between %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
