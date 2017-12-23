package input;

import java.util.LinkedList;
import java.util.List;

import base.Event;
import base.EventType;
import config.MainConfig;
import specification.InputSpecification;
import specification.SimulationSpecification;
import user.speedd.fraud.CreditCardFraudEventTypesConverter;
import user.speedd.traffic.TrafficSpeedEventTypesConverter;
import user.stocks.converters.StocksByCompanyEventTypesConverter;
import user.stocks.converters.StocksByIndustryEventTypesConverter;
import user.stocks.converters.StocksByRegionEventTypesConverter;
import user.synthetic.SyntheticEventTypesConverter;
import user.traffic.AarhusTrafficEventTypesConverter;
import user.trams.TramCongestionEventTypesConverter;



public abstract class EventProducer {
	
	private EventTypesConverter converter;
	private EventStreamModifier modifier;
	private List<Event> pendingEvents;
	
	protected EventProducer(SimulationSpecification simulationSpecification) {
		switch(MainConfig.eventTypesConverterType) {
			case STOCK_BY_INDUSTRY_MEGA_LARGE_COMPANY:
				converter = new StocksByIndustryEventTypesConverter(simulationSpecification);
				break;
			case STOCK_BY_REGION:
				converter = new StocksByRegionEventTypesConverter(simulationSpecification);
				break;
			case STOCK_BY_COMPANY:
				converter = new StocksByCompanyEventTypesConverter(simulationSpecification);
				break;
			case TRAM_CONGESTION:
				converter = new TramCongestionEventTypesConverter(simulationSpecification);
				break;
			case SPEEDD_TRAFFIC:
				converter = new TrafficSpeedEventTypesConverter(simulationSpecification);
				break;
			case SPEEDD_CREDIT_CARD_FRAUD:
				converter = new CreditCardFraudEventTypesConverter(simulationSpecification);
				break;
			case SYNTHETIC:
				converter = new SyntheticEventTypesConverter(simulationSpecification);
				break;
			case TRAFFIC_SPEED_VEHICLES_NUMBER:
				converter = new AarhusTrafficEventTypesConverter(simulationSpecification);
				break;
			default:
				converter = null;
				break;
		}
		InputSpecification inputSpecification = simulationSpecification.getInputSpecification();
		try {
			modifier = new EventStreamModifier(inputSpecification.filteringFactor, 
											   inputSpecification.duplicatingFactor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pendingEvents = new LinkedList<Event>();
	}

	protected boolean produceActualEvents(String[] rawEvent) {
		EventType eventType = converter.convertToKnownEventType(rawEvent);
		if (eventType == null)
			return false;
		Event event = new Event(eventType, rawEvent);
		if (MainConfig.enableDynamicEventStreamModification) {
			pendingEvents.addAll(modifier.produceModifiedEvents(event));
		}
		else {
			pendingEvents.add(event);
		}
		return true;
	}
	
	public Event getNextEvent() {
		if (pendingEvents.isEmpty()) {
			if (!createMoreEvents()) {
				return null;
			}
		}
		return pendingEvents.remove(0);
	}
	
	public boolean hasMoreEvents() {
		return !pendingEvents.isEmpty() || canCreateMoreEvents();
	}
	
	protected abstract boolean createMoreEvents();
	protected abstract boolean canCreateMoreEvents();
	public abstract void finish();
}





