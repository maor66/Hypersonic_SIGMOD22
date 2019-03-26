package sase.input.producers;

import sase.config.MainConfig;
import sase.input.EventProducer;
import sase.specification.SimulationSpecification;

public class FileBasedEventProducer extends EventProducer {
	
	private FileEventStreamReader fileEventStreamReader;

	public FileBasedEventProducer(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
		fileEventStreamReader = new FileEventStreamReader(MainConfig.inputDirsPaths, 
														  MainConfig.inputFilesPaths,
														  MainConfig.eventsPerRead);
	}

	@Override
	protected boolean createMoreEvents() {
		if (!fileEventStreamReader.hasMoreEvents()) {
			return false;
		}
		boolean eventsCreated = false;
		while (!eventsCreated) {
			String[] rawEvent = fileEventStreamReader.getRawEvent();
			if (rawEvent == null) {
				return false;
			}
			eventsCreated = produceActualEvents(rawEvent);
		}
		return true;
	}

	@Override
	protected boolean canCreateMoreEvents() {
		return fileEventStreamReader.hasMoreEvents();
	}

	@Override
	public void finish() {
		fileEventStreamReader.finish();
	}

}
