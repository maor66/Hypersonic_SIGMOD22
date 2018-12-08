package sase.input;

import java.util.List;

import sase.config.MainConfig;
import sase.input.producers.FileBasedEventProducer;
import sase.input.producers.SyntheticEventProducer;
import sase.pattern.Pattern;
import sase.specification.SimulationSpecification;

public class EventProducerFactory {

	public static EventProducer createEventProducer(List<Pattern> patterns,
													SimulationSpecification simulationSpecification) {
		switch (MainConfig.eventProducerType) {
			case FILE_BASED:
				return new FileBasedEventProducer(simulationSpecification);
			case SYNTHETIC:
				//TODO: for now, will only work as intended for single-pattern workloads
				return new SyntheticEventProducer(patterns.get(0), simulationSpecification);
			default:
				return null;
		}
	}
}
