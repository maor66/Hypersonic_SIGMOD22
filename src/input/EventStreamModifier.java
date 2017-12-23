package input;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pattern.EventTypesManager;
import base.Event;
import base.EventType;
import config.MainConfig;

public class EventStreamModifier {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");

	private int eventCounter;
	private int rarestEventCounter;
	private final int rarestEventTypeFilteringFactor;
	private final int rarestEventTypeDuplicatingFactor;
	
	//these variables are only used when dynamic input modification is enabled
	private List<EventType> initialReplaceMap;
	private List<EventType> currReplaceMap;

	//TODO: enable filtering and duplicating of all event types, rather than the rarest one only
	public EventStreamModifier(int rarestEventTypeFilteringFactor,
							   int rarestEventTypeDuplicatingFactor) throws Exception {
		eventCounter = 0;
		rarestEventCounter = 0;
		this.rarestEventTypeFilteringFactor = rarestEventTypeFilteringFactor;
		this.rarestEventTypeDuplicatingFactor = rarestEventTypeDuplicatingFactor;
		if (this.rarestEventTypeFilteringFactor > 1 && this.rarestEventTypeDuplicatingFactor > 1)
			throw new Exception("Inconsistent Filtering & Duplicating parameters provided");
		initialReplaceMap = new ArrayList<EventType>();
		for (String eventTypeName : MainConfig.recognizedEventTypeNames) {
			//TODO: getTypeByName may return null if data domain other than stocks is used!
			initialReplaceMap.add(EventTypesManager.getInstance().getTypeByName(eventTypeName));
		}
		currReplaceMap = new ArrayList<EventType>(initialReplaceMap);
	}

	private static long createTimestamp(String originalTimestamp) throws ParseException {
		return formatter.parse(originalTimestamp).getTime() / 1000 / 60;
	}
	
	private void advanceToNextPhase() {
		EventType newMostFrequentEventType = currReplaceMap.remove(currReplaceMap.size() - 1);
		currReplaceMap.add(0, newMostFrequentEventType);
	}
	
	private EventType getEffectiveEventType(EventType originalEventType) {
		if (!(MainConfig.enableFullDynamicMode))
			return originalEventType;
		int eventIndex = initialReplaceMap.indexOf(originalEventType);
		if (eventIndex < 0)
			return originalEventType;
		if (eventCounter % MainConfig.eventRatesChangeFrequency == 0) {
			advanceToNextPhase();
		}
		return currReplaceMap.get(eventIndex);
	}
	
	private boolean shouldFilterEvent(Event initialEvent, EventType eventType) {
		if (MainConfig.rarestEventTypeName != null)
			return false;
		if (eventType != EventTypesManager.getInstance().getTypeByName(MainConfig.rarestEventTypeName))
			return false;
		return (rarestEventCounter % rarestEventTypeFilteringFactor > 0);
	}
	
	private List<Event> createAndDuplicateActualEvent(Event initialEvent, EventType eventType) {
		Object[] newEventPayload = initialEvent.getAttributes();
		try {
			newEventPayload[1] = Long.toString(createTimestamp(((Long)newEventPayload[1]).toString()));
		} catch (ParseException e) {
			return null;
		}
		List<Event> newEvents = new ArrayList<Event>();
		for (int i = 0; i < rarestEventTypeDuplicatingFactor; ++i) {
			newEvents.add(new Event(eventType, newEventPayload));
			if (MainConfig.rarestEventTypeName == null)
				break;
			if (eventType != EventTypesManager.getInstance().getTypeByName(MainConfig.rarestEventTypeName))
				break;
		}
		return newEvents;
	}
	
	public List<Event> produceModifiedEvents(Event initialEvent) {
		eventCounter++;
		EventType effectiveEventType = getEffectiveEventType(initialEvent.getType());	
		if (MainConfig.rarestEventTypeName != null && 
			effectiveEventType == EventTypesManager.getInstance().getTypeByName(MainConfig.rarestEventTypeName)) {
			rarestEventCounter++;
		}
		if (shouldFilterEvent(initialEvent, effectiveEventType))
			return null;
		return createAndDuplicateActualEvent(initialEvent, effectiveEventType);
	}
}
