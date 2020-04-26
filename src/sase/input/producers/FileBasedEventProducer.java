package sase.input.producers;

import sase.config.MainConfig;
import sase.input.EventProducer;
import sase.specification.SimulationSpecification;

public class FileBasedEventProducer extends EventProducer {
	
	private FileEventStreamReader fileEventStreamReader;
	public static long ReadTime = 0;

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
			long time = System.nanoTime();
			String[] rawEvent = fileEventStreamReader.getRawEvent();
			ReadTime += System.nanoTime() - time;
			if (rawEvent == null) {
				return false;
			}
			String[] preprocessLine  = preprocessLineBeforeProducingEvent(rawEvent);
			eventsCreated = produceActualEvents(preprocessLine);
		}
		return true;
	}

	protected String[] preprocessLineBeforeProducingEvent(String[] rawEvent) {
		return rawEvent;
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
