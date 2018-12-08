package sase.input;

import java.util.LinkedList;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.specification.SimulationSpecification;
import sase.specification.input.InputSpecification;
import sase.user.speedd.fraud.CreditCardFraudEventTypesConverter;
import sase.user.speedd.traffic.TrafficSpeedEventTypesConverter;
import sase.user.stocks.converters.StocksByCompanyEventTypesConverter;
import sase.user.stocks.converters.StocksByIndustryEventTypesConverter;
import sase.user.stocks.converters.StocksByRegionEventTypesConverter;
import sase.user.synthetic.SyntheticEventTypesConverter;
import sase.user.traffic.AarhusTrafficEventTypesConverter;
import sase.user.trams.TramCongestionEventTypesConverter;



public abstract class EventProducer {
	
	private EventTypesConverter converter;
	private IEventStreamModifier modifier;
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
		modifier = EventStreamModifierFactory.createEventStreamModifier(inputSpecification);
		pendingEvents = new LinkedList<Event>();
	}

	protected boolean produceActualEvents(String[] rawEvent) {
		EventType eventType = converter.convertToKnownEventType(rawEvent);
		if (eventType == null) {
			return false;
		}
		Event event = new Event(eventType, rawEvent);
		pendingEvents.addAll(modifier.produceModifiedEvents(event));
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





