package user.stocks.converters;

import java.util.HashMap;
import java.util.List;

import pattern.EventTypesManager;
import specification.SimulationSpecification;
import base.EventType;
import input.EventTypesConverter;

public class StocksByCompanyEventTypesConverter extends EventTypesConverter {

	private HashMap<String, EventType> nameToTypeHash;
	
	public StocksByCompanyEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
		nameToTypeHash = new HashMap<String, EventType>();
		List<EventType> knownEventTypes = EventTypesManager.getInstance().getKnownEventTypes();
		for (EventType eventType : knownEventTypes) {
			nameToTypeHash.put(eventType.getName(), eventType);
		}
	}

	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		String eventCompanyLabel = rawEvent[0];
		return nameToTypeHash.get(eventCompanyLabel); 
	}
}
