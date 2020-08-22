package sase.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;

/**
 * This class contains information regarding all types of primitive events used by the framework.
 *
 */
public abstract class EventTypesManager { //TODO: this class requires a HEAVY re-design! Adding a new type of data is a mess!!!

	private static EventTypesManager instance = null;

	private boolean typesInitialized = false;
	private HashMap<String, EventType> eventNameToEventType = null;
	private HashMap<String, String> eventLongNameToShortName = null;

	public static EventTypesManager getInstance() {
		if (instance == null)
			throw new RuntimeException("Event Types Manager is not yet initialized");
		return instance;
	}

	public static void setInstance(EventTypesManager eventTypesManager) {
		if (instance != null)
			throw new RuntimeException("Event Types Manager is already initialized");
		instance = eventTypesManager;
	}

	public static boolean isInitialized() {
		return instance != null;
	}

	protected EventTypesManager() {
	}

	public void initializeTypes() {
		if (typesInitialized)
			return;
		actuallyInitializeTypes();
		eventNameToEventType = createNameToTypeHash();
		eventLongNameToShortName = createLongNameToShortNameHash();
		typesInitialized = true;
	}

	public EventType getTypeByName(String name) {
		if (eventNameToEventType == null)
			return null;
		return eventNameToEventType.get(name);
	}

	public String getShortNameByLongName(String name) {
		if (eventLongNameToShortName == null)
			return null;
		return eventLongNameToShortName.get(name);
	}

	public List<EventType> convertNamesToTypes(String[] names) {
		List<EventType> eventTypes = new ArrayList<EventType>();
		for (String eventName : names) {
			eventTypes.add(getTypeByName(eventName));
		}
		return eventTypes;
	}

	public abstract String getEventLabel(Event event);

	public abstract Long getEventTimestamp(Event event);

	public abstract Object[] convertStringPayloadToObjectPayload(Object[] payload);

	public abstract long getAverageEventSize();

	public abstract List<EventType> getKnownEventTypes();

	public abstract List<String> getKnownEventTypeNames();

	protected abstract void actuallyInitializeTypes();

	protected abstract HashMap<String, EventType> createNameToTypeHash();

	protected abstract HashMap<String, String> createLongNameToShortNameHash();

	public abstract List<String> getAllFusedTypeNames();
}
