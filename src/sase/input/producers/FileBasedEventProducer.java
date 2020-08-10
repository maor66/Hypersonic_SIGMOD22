package sase.input.producers;

import sase.config.MainConfig;
import sase.input.EventProducer;
import sase.specification.SimulationSpecification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileBasedEventProducer extends EventProducer {
	
	private FileEventStreamReader fileEventStreamReader;
	public static long ReadTime = 0;
	public static final int STOCK_TYPE_NAME_LENGTH = 4;

	public FileBasedEventProducer(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
		fileEventStreamReader = new FileEventStreamReader(MainConfig.inputDirsPaths,
														  MainConfig.inputFilesPaths,
														  MainConfig.eventsPerRead);
		if (MainConfig.isFusionSupported) {
			preprocessFileToFused(fileEventStreamReader, simulationSpecification);
		}

	}

	private void preprocessFileToFused(FileEventStreamReader fileEventStreamReader, SimulationSpecification simulationSpecification) {
		for (int i = 0; i < fileEventStreamReader.inputFilesPaths.length; i++) {
			String file_path = fileEventStreamReader.inputFilesPaths[i];
			String fused_file_path = file_path.substring(0, file_path.length() - 4) + "_fused.txt";
			runPreprocessorScript(file_path, fused_file_path, simulationSpecification);
			fileEventStreamReader.inputFilesPaths[i] = fused_file_path;
		}
	}

	private void runPreprocessorScript(String file_path, String fused_file_path, SimulationSpecification simulationSpecification) {
		String fusedType = getFusedType(simulationSpecification);
		if (fusedType == null) {
			throw new RuntimeException("No fused type found");
		}
		ProcessBuilder processBuilder = new ProcessBuilder(MainConfig.systemPythonPath, "fusion_input_pre_processor.py",
				file_path,
				fusedType.substring(0,STOCK_TYPE_NAME_LENGTH),
				fusedType.substring(STOCK_TYPE_NAME_LENGTH),
				simulationSpecification.getWorkloadSpecification().getMaxTimeWindow().toString(),
				fused_file_path);
		processBuilder.redirectErrorStream(true);
		try {
			Process process = processBuilder.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s = in.readLine();
			s = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFusedType(SimulationSpecification simulationSpecification) {
		for (String type : simulationSpecification.getWorkloadSpecification().getEventNames()) {
			if (type.length() == STOCK_TYPE_NAME_LENGTH * 2)
				return type;
		}
		return null;
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
