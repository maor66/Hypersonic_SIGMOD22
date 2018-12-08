package sase.input.modifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sase.base.Event;
import sase.base.EventType;
import sase.input.IEventStreamModifier;
import sase.pattern.EventTypesManager;

public class TypeShuffleEventStreamModifier implements IEventStreamModifier {

	private Map<EventType, EventType> eventTypeTranslationMap;
	private final Integer reShufflingPeriod;
	
	public TypeShuffleEventStreamModifier(Integer reShufflingPeriod) {
		eventTypeTranslationMap = new HashMap<EventType, EventType>();
		for (EventType eventType : EventTypesManager.getInstance().getKnownEventTypes()) {
			eventTypeTranslationMap.put(eventType, eventType);
		}
		this.reShufflingPeriod = reShufflingPeriod;
	}
	
	private void reShuffleMap() {
		List<EventType> eventTypes = new ArrayList<EventType>(eventTypeTranslationMap.keySet());
		List<EventType> shuffledEventTypes = new ArrayList<EventType>(eventTypes);
		Collections.shuffle(shuffledEventTypes);
		for (int i = 0; i < eventTypes.size(); ++i) {
			eventTypeTranslationMap.put(eventTypes.get(i), shuffledEventTypes.get(i));
		}
	}
	
	@Override
	public List<Event> produceModifiedEvents(Event initialEvent) {
		if (initialEvent.getSequenceNumber() % reShufflingPeriod == 0) {
			reShuffleMap();
		}
		initialEvent.setType(eventTypeTranslationMap.get(initialEvent.getType()));
		return Arrays.asList(initialEvent);
	}

}
