package sase.adaptive.monitoring;

import sase.simulator.Environment;
import sase.statistics.Statistics;
import sase.statistics.StatisticsManager;

public class PerformanceDeltaAdaptationNecessityDetector implements IAdaptationNecessityDetector {

	private long currentNumberOfProcessedEvents = 0;
	private long lastTotalNumberOfProcessedEvents = 0;
	private double minThroughputDifferenceRatio;
	
	public PerformanceDeltaAdaptationNecessityDetector(double minThroughputDifferenceRatio) {
		this.minThroughputDifferenceRatio = minThroughputDifferenceRatio;
	}
	
	@Override
	public boolean shouldAdapt() {
		StatisticsManager statisticsManager = Environment.getEnvironment().getStatisticsManager();
		long currentTotalNumberOfProcessedEvents = statisticsManager.getDiscreteStatistic(Statistics.events);
		if (lastTotalNumberOfProcessedEvents == 0) {
			lastTotalNumberOfProcessedEvents = currentTotalNumberOfProcessedEvents;
			return false;
		}
		if (currentNumberOfProcessedEvents == 0) {
			currentNumberOfProcessedEvents = currentTotalNumberOfProcessedEvents - lastTotalNumberOfProcessedEvents;
			lastTotalNumberOfProcessedEvents = currentTotalNumberOfProcessedEvents;
			return false;
		}
		long newNumberOfProcessedEvents = currentTotalNumberOfProcessedEvents - lastTotalNumberOfProcessedEvents;
		if (newNumberOfProcessedEvents / (double)currentNumberOfProcessedEvents < 1 - minThroughputDifferenceRatio) {
			currentNumberOfProcessedEvents = 0;
			lastTotalNumberOfProcessedEvents = 0;
			return true;
		}
		currentNumberOfProcessedEvents = newNumberOfProcessedEvents;
		lastTotalNumberOfProcessedEvents = currentTotalNumberOfProcessedEvents;
		return false;
	}

}
