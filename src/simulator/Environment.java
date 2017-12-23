package simulator;

import java.io.IOException;

import adaptive.estimation.SlidingWindowEventArrivalRateEstimator;
import adaptive.estimation.SlidingWindowSelectivityEstimator;
import adaptive.monitoring.AdaptationNecessityDetector;
import adaptive.monitoring.AdaptationNecessityDetectorFactory;
import config.MainConfig;
import evaluation.EvaluationPlanCreator;
import evaluation.IEvaluationMechanismInfo;
import pattern.Pattern;
import specification.AdaptationSpecification;
import specification.SimulationSpecification;
import statistics.StatisticsManager;

public class Environment {

	private static Environment environment = null;
	
	public static Environment getEnvironment() {
		if (environment == null) {
			environment = new Environment();
		}
		return environment;
	}
	
	private StatisticsManager statisticsManager = null;
	private SlidingWindowEventArrivalRateEstimator eventRateEstimator = null;
	private SlidingWindowSelectivityEstimator selectivityEstimator = null;
	private IEvaluationMechanismInfo evaluationMechanismInfo = null;
	private PredicateResultsCache predicateResultsCache = null;
	private AdaptationNecessityDetector adaptationNecessityDetector = null;
	private EvaluationPlanCreator evaluationPlanCreator = null;
	
	private Environment() {
	}

	public void reset(Pattern pattern, SimulationSpecification currentSpecification) {
		try {
			StatisticsManager.resetStatisticsManager(currentSpecification.getShortDescription());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		statisticsManager = StatisticsManager.getInstance();
		
		evaluationPlanCreator = new EvaluationPlanCreator(currentSpecification.getEvaluationSpecification());
		
		if (MainConfig.adaptationTrialsIntervalToTimeWindowRatio == null) {
			//disable all adaptive behavior
			eventRateEstimator = null;
			selectivityEstimator = null;
			adaptationNecessityDetector = null;
		}
		else {
			AdaptationSpecification adaptationSpecification = currentSpecification.getAdaptationSpecification();
			Long statisticsEstimationWindowSize =
					pattern.getTimeWindow() * adaptationSpecification.statisticsMonitoringWindowToTimeWindowRatio;
			eventRateEstimator = 
				new SlidingWindowEventArrivalRateEstimator(statisticsEstimationWindowSize, 
														   adaptationSpecification.maxError,
														   pattern.getEventTypes().size());
			selectivityEstimator = 
					new SlidingWindowSelectivityEstimator(statisticsEstimationWindowSize, adaptationSpecification.maxError);
			adaptationNecessityDetector = 
					new AdaptationNecessityDetectorFactory().createAdaptationNecessityDetector(
							adaptationSpecification.adaptationDetectorType, pattern,
							adaptationSpecification.comparerType,
							adaptationSpecification.minimalInvariantRelativeDistance,
							adaptationSpecification.minThroughputDifferenceRatio);
		}
		evaluationMechanismInfo = null;
		predicateResultsCache = new PredicateResultsCache();
	}
	
	public void destroy() {
		StatisticsManager.destroy();
	}

	public StatisticsManager getStatisticsManager() {
		return statisticsManager;
	}

	public SlidingWindowSelectivityEstimator getSelectivityEstimator() {
		return selectivityEstimator;
	}

	public SlidingWindowEventArrivalRateEstimator getEventRateEstimator() {
		return eventRateEstimator;
	}

	public IEvaluationMechanismInfo getEvaluationMechanismInfo() {
		return evaluationMechanismInfo;
	}

	public void setEvaluationMechanismInfo(IEvaluationMechanismInfo evaluationMechanismInfo) {
		this.evaluationMechanismInfo = evaluationMechanismInfo;
	}

	public PredicateResultsCache getPredicateResultsCache() {
		return predicateResultsCache;
	}

	public AdaptationNecessityDetector getAdaptationNecessityDetector() {
		return adaptationNecessityDetector;
	}

	public EvaluationPlanCreator getEvaluationPlanCreator() {
		return evaluationPlanCreator;
	}
	
}
