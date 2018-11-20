package sase.user.trams;

import sase.specification.SimulationSpecification;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.input.EventTypesConverter;

public class TramCongestionEventTypesConverter extends EventTypesConverter {

	public TramCongestionEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}
	
	private EventType getEventTypeByLinesCount(String lineNumbersStr) {
		String[] lineNumbers = lineNumbersStr.split(TramEventTypesManager.lineNumbersSeparator);
		if (lineNumbers.length >= MainConfig.heavyCongestionThreshold) {
			return TramEventTypesManager.heavyCongestionEventType;
		}
		if (lineNumbers.length >= MainConfig.severeCongestionThreshold) {
			return TramEventTypesManager.severeCongestionEventType;
		}
		if (lineNumbers.length >= MainConfig.mediumCongestionThreshold) {
			return TramEventTypesManager.mediumCongestionEventType;
		}
		if (lineNumbers.length >= MainConfig.lightCongestionThreshold) {
			return TramEventTypesManager.lightCongestionEventType;
		}
		return TramEventTypesManager.normalTrafficEventType;
	}
	
	private EventType getEventTypeByPatternLength(EventType initialEventType) {
		int numberOfEventTypes = workloadSpecification.getEventNames().size();
		if (numberOfEventTypes < 4 && initialEventType == TramEventTypesManager.mediumCongestionEventType) {
			return TramEventTypesManager.lightCongestionEventType;
		}
		if (numberOfEventTypes < 5 && initialEventType == TramEventTypesManager.severeCongestionEventType) {
			return TramEventTypesManager.heavyCongestionEventType;
		}
		return initialEventType;
	}

	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		String lineNumbers = rawEvent[TramEventTypesManager.lineNumbersAttributeIndex];
		EventType initialEventType = getEventTypeByLinesCount(lineNumbers);
		EventType actualEventType = getEventTypeByPatternLength(initialEventType);
		return actualEventType;
	}
}
