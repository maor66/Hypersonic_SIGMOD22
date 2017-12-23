package sase.input;

import sase.config.MainConfig;
import sase.input.producers.FileBasedEventProducer;
import sase.input.producers.SyntheticEventProducer;
import sase.pattern.Pattern;
import sase.specification.SimulationSpecification;

public class EventProducerFactory {

	public static EventProducer createEventProducer(Pattern pattern,
													SimulationSpecification simulationSpecification) {
		switch (MainConfig.eventProducerType) {
			case FILE_BASED:
				return new FileBasedEventProducer(simulationSpecification);
			case SYNTHETIC:
				return new SyntheticEventProducer(pattern, simulationSpecification);
			default:
				return null;
		}
	}
}
