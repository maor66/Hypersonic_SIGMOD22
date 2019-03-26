package sase.simulator;

import java.io.IOException;

import sase.adaptive.estimation.IEventArrivalRateEstimator;
import sase.adaptive.estimation.SlidingWindowEventArrivalRateEstimator;
import sase.adaptive.estimation.SlidingWindowSelectivityEstimator;
import sase.adaptive.estimation.StaticEventArrivalRateEstimator;
import sase.adaptive.monitoring.IAdaptationNecessityDetector;
import sase.adaptive.monitoring.AdaptationNecessityDetectorFactory;
import sase.config.MainConfig;
import sase.evaluation.EvaluationPlanCreator;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.pattern.workload.IWorkloadManager;
import sase.specification.SimulationSpecification;
import sase.specification.adaptation.AdaptationSpecification;
import sase.statistics.Statistics;
import sase.statistics.StatisticsManager;

public class Environment {

	private static Environment environment = null;
	
	public static Environment getEnvironment() {
		if (environment == null) {
			environment = new Environment();
		}
		return environment;
	}
	
	private StatisticsManager statisticsManager = null;
	private IEventArrivalRateEstimator eventRateEstimator = null;
	private SlidingWindowSelectivityEstimator selectivityEstimator = null;
	private IEvaluationMechanismInfo evaluationMechanismInfo = null;
	private PredicateResultsCache predicateResultsCache = null;
	private IAdaptationNecessityDetector adaptationNecessityDetector = null;
	private EvaluationPlanCreator evaluationPlanCreator = null;
	
	private Environment() {
	}

	public void reset(IWorkloadManager workloadManager, SimulationSpecification currentSpecification) {
		try {
			StatisticsManager.resetStatisticsManager(currentSpecification.getShortDescription());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		statisticsManager = StatisticsManager.getInstance();
		
		evaluationPlanCreator = new EvaluationPlanCreator(currentSpecification.getEvaluationSpecification());
		
		//TODO: for now, adaptation will only work for single-sase.pattern workloads
		if (MainConfig.adaptationTrialsIntervalToTimeWindowRatio == null) {
			//disable all sase.adaptive behavior
			eventRateEstimator = new StaticEventArrivalRateEstimator();
			selectivityEstimator = null;
			adaptationNecessityDetector = null;
		}
		else {
			AdaptationSpecification adaptationSpecification = currentSpecification.getAdaptationSpecification();
			Long statisticsEstimationWindowSize =
					workloadManager.getMaxTimeWindow() * adaptationSpecification.statisticsMonitoringWindowToTimeWindowRatio;
			eventRateEstimator = 
				new SlidingWindowEventArrivalRateEstimator(statisticsEstimationWindowSize, 
														   adaptationSpecification.maxError,
														   workloadManager.getAllEventTypes().size());
			selectivityEstimator = 
					new SlidingWindowSelectivityEstimator(statisticsEstimationWindowSize, adaptationSpecification.maxError);
			adaptationNecessityDetector = 
					AdaptationNecessityDetectorFactory.createAdaptationNecessityDetector(adaptationSpecification, 
																						 workloadManager.getCurrentWorkload());
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

	public IEventArrivalRateEstimator getEventRateEstimator() {
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

	public IAdaptationNecessityDetector getAdaptationNecessityDetector() {
		return adaptationNecessityDetector;
	}

	public EvaluationPlanCreator getEvaluationPlanCreator() {
		return evaluationPlanCreator;
	}
    
    public boolean isTimeoutReached(Long timeSinceLastMeasurement) {
    	if (MainConfig.maxExecutionTime == null) {
    		return false;
    	}
    	long currentProcessingTime = (timeSinceLastMeasurement == null) ?
    			getStatisticsManager().getDiscreteStatistic(Statistics.processingTime) :
    			getStatisticsManager().getDiscreteStatistic(Statistics.processingTime) + timeSinceLastMeasurement;
    	return currentProcessingTime > MainConfig.maxExecutionTime;
    }
	
}
