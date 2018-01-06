package statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVWriter;
import config.MainConfig;

public class StatisticsManager {

	private static final String typeOfEvaluation = "Type Of Evaluation";
	
	private static StatisticsManager statisticsManager;
	
	public static StatisticsManager getInstance() {
		if (statisticsManager == null)
			throw new RuntimeException("Statistics Manager is not yet initialized");
		return statisticsManager;
	}
	
	public static void resetStatisticsManager(String currentRunDescription) throws IOException {
		CSVWriter writer = (statisticsManager == null) ? initializeCSVWriter() : statisticsManager.writer;
		statisticsManager = new StatisticsManager(writer, currentRunDescription);
	}
	
	private static CSVWriter initializeCSVWriter() throws IOException {
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(MainConfig.outputFilePath));
		} catch (IOException e) {
			System.out.println(String.format("Failed to open output file %s: %s", 
							   MainConfig.outputFilePath, e.getMessage()));
			 throw e;
		}
		writeColumnsNames(writer);
		return writer;
	}
	
	private static void writeColumnsNames(CSVWriter writer) {
		String[] discreteStatisticsNames = Statistics.getDiscreteOrderedNames();
		String[] fractionalStatisticsNames = Statistics.getFractionalOrderedNames();
		String[] columnsNames = new String[discreteStatisticsNames.length + fractionalStatisticsNames.length + 1];
		columnsNames[0] = typeOfEvaluation;
		for (int i = 0; i < discreteStatisticsNames.length; ++i) {
			columnsNames[i + 1] = discreteStatisticsNames[i];
		}
		for (int i = 0; i < fractionalStatisticsNames.length; ++i) {
			columnsNames[i + discreteStatisticsNames.length + 1] = fractionalStatisticsNames[i];
		}
		writer.writeNext(columnsNames);
	}
	
	public static void destroy() {
		if (statisticsManager == null)
			return;
		try {
			statisticsManager.writer.close();
		} catch (IOException e) {
			System.out.println(String.format("Failed to close output file %s: %s", 
							   MainConfig.outputFilePath, e.getMessage()));
		}
		statisticsManager = null;
	}
	
	public static void attemptPeriodicUpdate() throws IOException {
		long numberOfEvents = getInstance().getDiscreteStatistic(Statistics.events);
		if (numberOfEvents % MainConfig.statisticsReportPeriod != 0) {
			return;
		}
		getInstance().reportStatistics(true);
		String currentDescription = getInstance().runDescription;
		int bracketIndex = currentDescription.indexOf('[');
		if (bracketIndex != -1) {
			currentDescription = currentDescription.substring(0, bracketIndex);
		}
		resetStatisticsManager(currentDescription + String.format("[%d]", numberOfEvents));
	}
	
	
	private final CSVWriter writer;
	private String runDescription;
	private HashMap<String, Long> discreteStatistics;
	private HashMap<String, Double> fractionalStatistics;
	private HashMap<String, Long> timeMeasurementsInProgress;
	
	private StatisticsManager(CSVWriter writer, String runDescription) throws IOException {
		this.writer = writer;
		this.runDescription = runDescription;
		initializeDiscreteStatisticsHashTable();
		initializeFractionalStatisticsHashTable();
		timeMeasurementsInProgress = new HashMap<String, Long>();
	}
	
	public String getRunDescription() {
		return runDescription;
	}

	public void setRunDescription(String newRunDescription) {
		runDescription = newRunDescription;
	}
	
	private void initializeDiscreteStatisticsHashTable() {
		discreteStatistics = new HashMap<String, Long>();
		synchronized (this) {
			for (String name : Statistics.getDiscreteOrderedNames()) {
				discreteStatistics.put(name, new Long(0));
			}
		}
	}
	
	private void initializeFractionalStatisticsHashTable() {
		fractionalStatistics = new HashMap<String, Double>();
		for (String name : Statistics.getFractionalOrderedNames()) {
			fractionalStatistics.put(name, new Double(0));
		}
	}
	
	public void updateDiscreteStatistic(String key, long valueToAdd) {
		if (!(discreteStatistics.containsKey(key))) {
			throw new RuntimeException(String.format("Unknown statistic identifier: %s", key));
		}
		synchronized (this) {
			discreteStatistics.put(key, discreteStatistics.get(key) + valueToAdd);
		}
	}
	
	public void replaceDiscreteStatistic(String key, long valueToPut) {
		if (!(discreteStatistics.containsKey(key))) {
			throw new RuntimeException(String.format("Unknown statistic identifier: %s", key));
		}
		discreteStatistics.put(key, valueToPut);
	}
	
	public void incrementDiscreteStatistic(String key) {
		updateDiscreteStatistic(key, 1);
	}
	
	public void decrementDiscreteStatistic(String key) {
		updateDiscreteStatistic(key, -1);
	}
	
	public void updateDiscreteIfBigger(String key, long newValue) {
		if (!(discreteStatistics.containsKey(key))) {
			throw new RuntimeException(String.format("Unknown statistic identifier: %s", key));
		}
		synchronized (this) {
			if (discreteStatistics.get(key) < newValue) {
				discreteStatistics.put(key, newValue);
			}
		}
	}
	
	public void updateDiscreteMemoryStatistic(String key, long valueToAdd) {
		updateDiscreteStatistic(key, valueToAdd);
		updateDiscreteStatistic(Statistics.memoryOperations, valueToAdd);
	}
	
	public void incrementDiscreteMemoryStatistic(String key) {
		updateDiscreteMemoryStatistic(key, 1);
	}
	
	public long getDiscreteStatistic(String key) {
		if (!(discreteStatistics.containsKey(key))) {
			throw new RuntimeException(String.format("Unknown statistic identifier: %s", key));
		}
		synchronized (this) {
			return discreteStatistics.get(key);
		}
	}
	
	public void updateFractionalStatistic(String key, double valueToAdd) {
		if (!(fractionalStatistics.containsKey(key))) {
			throw new RuntimeException(String.format("Unknown statistic identifier: %s", key));
		}
		fractionalStatistics.put(key, fractionalStatistics.get(key) + valueToAdd);
	}
	
	public void startMeasuringTime(String timeStatisticKey) {
		if (!discreteStatistics.containsKey(timeStatisticKey) && !fractionalStatistics.containsKey(timeStatisticKey)) {
			throw new RuntimeException(String.format("Unknown statistic identifier: %s", timeStatisticKey));
		}
		timeMeasurementsInProgress.put(timeStatisticKey, System.currentTimeMillis());
	}
	
	public boolean isTimeMeasuredForStatistic(String timeStatisticKey) {
		return timeMeasurementsInProgress.containsKey(timeStatisticKey);
	}
	
	public void stopMeasuringTime(String timeStatisticKey) {
		if (!isTimeMeasuredForStatistic(timeStatisticKey)) {
			throw new RuntimeException(String.format("No time measurement was started: %s", timeStatisticKey));
		}
		long delta = System.currentTimeMillis() - timeMeasurementsInProgress.remove(timeStatisticKey);
		if (discreteStatistics.containsKey(timeStatisticKey)) {
			updateDiscreteStatistic(timeStatisticKey, delta);
		}
		else {
			updateFractionalStatistic(timeStatisticKey, delta);
		}
	}
	
	public void recordPeakMemoryUsage(long memoryUsage) {
		updateDiscreteIfBigger(Statistics.peakMemory, memoryUsage);
	}
	
	public void reportStatistics() {
		reportStatistics(false);
	}
	
	private void reportStatistics(boolean isInternalPeriodicReport) {
		String[] discreteStatisticsNames = Statistics.getDiscreteOrderedNames();
		String[] fractionalStatisticsNames = Statistics.getFractionalOrderedNames();
		String[] fieldsToWrite = new String[discreteStatisticsNames.length + fractionalStatisticsNames.length + 1];
		fieldsToWrite[0] = runDescription;
		for (int i = 0; i < discreteStatisticsNames.length; ++i) {
			fieldsToWrite[i + 1] = Long.toString(discreteStatistics.get(discreteStatisticsNames[i]));
		}
		for (int i = 0; i < fractionalStatisticsNames.length; ++i) {
			Double currentValue = fractionalStatistics.get(fractionalStatisticsNames[i]);
			currentValue = preprocessFractionalStatisticValue(fractionalStatisticsNames[i], currentValue);
			fieldsToWrite[i + discreteStatisticsNames.length + 1] = Double.toString(currentValue);
		}
		
		writer.writeNext(fieldsToWrite);
		try {
			writer.flush();
		} catch (IOException e) {
			System.out.println(String.format("Failed to write to output file %s: %s", 
							   MainConfig.outputFilePath, e.getMessage()));
		}
		if (!isInternalPeriodicReport) {
			System.out.println("Evaluation Step Completed.\n");
		}
	}
	
	private Double preprocessFractionalStatisticValue(String name, Double value) {
		if (name.equals(Statistics.averageLatency)) {
			return computeAverageStatisticValue(Statistics.matches, value);
		}

		if (name.equals(Statistics.averageInputChangeDetectionTime)) {
			return computeAverageStatisticValue(Statistics.numberOfInputChanges, value);
		}
		return value;
	}
	
	private Double computeAverageStatisticValue(String numberOfElementsName, Double value) {
		Long numberOfElements = discreteStatistics.get(numberOfElementsName);
		if (numberOfElements > 0) {
			return value / numberOfElements;
		}
		return value;
	}
}
