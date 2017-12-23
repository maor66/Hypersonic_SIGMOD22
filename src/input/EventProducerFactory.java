package input;

import config.MainConfig;
import input.producers.FileBasedEventProducer;
import input.producers.SyntheticEventProducer;
import pattern.Pattern;
import specification.SimulationSpecification;

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
