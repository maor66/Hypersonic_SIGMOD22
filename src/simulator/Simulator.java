package sase.simulator;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sase.adaptive.monitoring.AdaptationNecessityDetector;
import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.config.MainConfig;
import sase.config.SimulationConfig;
import sase.evaluation.EvaluationMechanismFactory;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.input.EventProducer;
import sase.input.EventProducerFactory;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern;
import sase.pattern.creation.PatternFactory;
import sase.specification.PatternSpecification;
import sase.specification.SimulationSpecification;
import sase.specification.creators.ISimulationSpecificationCreator;
import sase.specification.creators.SpecificationCreatorFactory;
import sase.statistics.ConditionSelectivityCollector;
import sase.statistics.EventRateCollector;
import sase.statistics.Statistics;
import sase.statistics.StatisticsManager;

public class Simulator {
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private List<SimulationSpecification> specifications;
	
	private Pattern pattern;
    private EventProducer eventProducer;
    private	EventRateCollector eventRateEstimator;

    private IEvaluationMechanism primaryEvaluationMechanism;
    private IEvaluationMechanism secondaryEvaluationMechanism;
    
    private Long lastAdaptCheckTimestamp;
    private Long secondaryEvaluationMechanismCreationTimestamp;
    
    private SimulationHistory simulationHistory = new SimulationHistory();
	private String currentHistoryId = null;
    private StatisticsManager oldStatisticsManager = null;
    
    private int currentStepNumber = 0;
    
	private void processIncomingEvent(Event event) {
		if (MainConfig.eventRateMeasurementMode) {
    		eventRateEstimator.recordEvent(event);
		}
		if (MainConfig.isArrivalRateMonitoringAllowed) {
			Environment.getEnvironment().getEventRateEstimator().registerEventArrival(event.getType());
		}
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.events);
		List<Match> matches = actuallyProcessIncomingEvent(event);
		Environment.getEnvironment().getPredicateResultsCache().clear();
		recordNewMatches(matches);
		tryAdaptEvaluation(event.getTimestamp());
	}
	
	private List<Match> actuallyProcessIncomingEvent(Event event) {
		if (secondaryEvaluationMechanism == null) {
			List<Match> matches = validateTimeWindowOnEvaluationMechanism(primaryEvaluationMechanism, event);
			addIfNotNull(processNewEventOnEvaluationMechanism(primaryEvaluationMechanism, event, true), matches);
			return matches;
		}
		List<Match> matches = validateTimeWindowOnEvaluationMechanism(primaryEvaluationMechanism, event);
		matches.addAll(validateTimeWindowOnEvaluationMechanism(secondaryEvaluationMechanism, event));
		addIfNotNull(processNewEventOnEvaluationMechanism(primaryEvaluationMechanism, event, false), matches);
		addIfNotNull(processNewEventOnEvaluationMechanism(secondaryEvaluationMechanism, event, true), matches);
		return matches;
	}
	
	private void addIfNotNull(List<Match> listToAdd, List<Match> listToAddTo) {
		if (listToAdd != null) {
			listToAddTo.addAll(listToAdd);
		}
	}
	
	@SuppressWarnings("unused")
	private List<Match> processNewEventOnEvaluationMechanism(IEvaluationMechanism mechanism,
															 Event event, boolean canStartInstance) {
		List<Match> matches = mechanism.processNewEvent(event, canStartInstance);
		if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY) {
			mechanism.removeConflictingInstances(matches);
		}
		if (MainConfig.debugMode && matches != null && !matches.isEmpty()) {
			System.out.println(matches);
		}
		return matches;
	}

	private List<Match> validateTimeWindowOnEvaluationMechanism(IEvaluationMechanism mechanism, Event event) {
		List<Match> matches = mechanism.validateTimeWindow(event.getTimestamp());
		if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY) {
			mechanism.removeConflictingInstances(matches);
		}
		return matches;
	}
	
	private void tryAdaptEvaluation(long currentTimestamp) {
		if (secondaryEvaluationMechanism != null) {
			if (currentTimestamp - secondaryEvaluationMechanismCreationTimestamp > pattern.getTimeWindow()) {
				primaryEvaluationMechanism = secondaryEvaluationMechanism;
				secondaryEvaluationMechanism = null;
			}
			return;
		}
		if (MainConfig.adaptationTrialsIntervalToTimeWindowRatio == null) { //adaptive behavior is disabled
			return;
		}
		if (lastAdaptCheckTimestamp == null) {
			lastAdaptCheckTimestamp = currentTimestamp;
			return;
		}
		if (currentTimestamp - lastAdaptCheckTimestamp < 
										pattern.getTimeWindow() * MainConfig.adaptationTrialsIntervalToTimeWindowRatio) {
			return;
		}
		lastAdaptCheckTimestamp = currentTimestamp;
		AdaptationNecessityDetector detector = Environment.getEnvironment().getAdaptationNecessityDetector();
		Environment.getEnvironment().getStatisticsManager().startMeasuringTime(Statistics.adaptationTime);
		if (detector.shouldAdapt()) {
			StatisticsManager statisticsManager = Environment.getEnvironment().getStatisticsManager();
			statisticsManager.incrementDiscreteStatistic(Statistics.numberOfAdaptations);
			if (MainConfig.isSyntheticInputEnabled) {
				if (statisticsManager.isTimeMeasuredForStatistic(Statistics.averageInputChangeDetectionTime)) {
					statisticsManager.stopMeasuringTime(Statistics.averageInputChangeDetectionTime);
				}
			}
			secondaryEvaluationMechanism = createNewEvaluationMechanism();
			secondaryEvaluationMechanismCreationTimestamp = currentTimestamp;
			if (primaryEvaluationMechanism.getStructureSummary().equals(secondaryEvaluationMechanism.getStructureSummary())) {
				statisticsManager.incrementDiscreteStatistic(Statistics.numberOfFalseAdaptations);
			}
		}
		Environment.getEnvironment().getStatisticsManager().stopMeasuringTime(Statistics.adaptationTime);
	}
	
	private IEvaluationMechanism createNewEvaluationMechanism() {
    	Environment.getEnvironment().getStatisticsManager().startMeasuringTime(Statistics.evaluationMechanismCreationTime);
    	Object evaluationMechanismObject = EvaluationMechanismFactory.createEvaluationMechanism(pattern);
    	Environment.getEnvironment().getStatisticsManager().stopMeasuringTime(Statistics.evaluationMechanismCreationTime);
    	IEvaluationMechanism evaluationMechanism = (IEvaluationMechanism)evaluationMechanismObject;
    	evaluationMechanism.completeCreation(pattern);
    	System.out.println(evaluationMechanism.getStructureSummary());
    	Environment.getEnvironment().setEvaluationMechanismInfo((IEvaluationMechanismInfo)evaluationMechanismObject);
    	return evaluationMechanism;
	}
	
	private void recordNewMatches(List<Match> matches) {
		if (matches == null) {
			return;
		}
		for (Match match : matches) {
			Environment.getEnvironment().getStatisticsManager().updateFractionalStatistic(Statistics.averageLatency,
																						  match.getDetectionLatency());
			Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.matches);
		}
	}
	
	private void attemptToRecoverExistingStatistics(SimulationSpecification currentSpecification) {
		currentHistoryId = String.format("%s|%s", 
 				 						 currentSpecification.getPatternSpecification().getShortDescription(), 
 				 						 primaryEvaluationMechanism.getStructureSummary());
		oldStatisticsManager = simulationHistory.getStatisticsManagerForSimulation(currentHistoryId);
		if (oldStatisticsManager == null) {
			return;
		}
		oldStatisticsManager.setRunDescription(Environment.getEnvironment().getStatisticsManager().getRunDescription());
		oldStatisticsManager.replaceDiscreteStatistic(
				Statistics.evaluationMechanismCreationTime, 
				Environment.getEnvironment().getStatisticsManager().getDiscreteStatistic(
																			Statistics.evaluationMechanismCreationTime));
	}

    private void prepareNextEvaluationStep(SimulationSpecification currentSpecification) throws Exception {
    	System.out.println(String.format("Running Simulation Step %d/%d %s\n%s", 
    									 ++currentStepNumber, specifications.size(), 
    									 dateFormat.format(new Date()), currentSpecification));
    	
    	pattern = PatternFactory.createPattern(currentSpecification.getPatternSpecification(),
    										   currentSpecification.getInputSpecification());
    	Environment.getEnvironment().reset(pattern, currentSpecification);
    	
    	eventProducer = EventProducerFactory.createEventProducer(pattern, currentSpecification);
    	
    	primaryEvaluationMechanism = createNewEvaluationMechanism();
    	secondaryEvaluationMechanism = null;
    	lastAdaptCheckTimestamp = null;
    	
    	eventRateEstimator = new EventRateCollector(EventTypesManager.getInstance().getKnownEventTypes(), 
    												pattern.getTimeWindow());
    	if (MainConfig.useSimulationHistory) {
    		attemptToRecoverExistingStatistics(currentSpecification);
    	}
    }
    
    private void runEvaluationStep() throws IOException {
    	if (oldStatisticsManager != null) {
    		oldStatisticsManager.reportStatistics();
    		return;
    	}
    	try {
    		while (eventProducer.hasMoreEvents()) {
    			if (Environment.getEnvironment().isTimeoutReached(null)) {
    				Environment.getEnvironment().getStatisticsManager().updateDiscreteStatistic(Statistics.isTimeoutReached, 1);
    				return;
    			}
	    		Event event = eventProducer.getNextEvent();
	    		if (event == null) {
	    			break;
	    		}
	    		Environment.getEnvironment().getStatisticsManager().startMeasuringTime(Statistics.processingTime);
	    		processIncomingEvent(event);
	    		Environment.getEnvironment().getStatisticsManager().stopMeasuringTime(Statistics.processingTime);
	    		long memoryUsage = secondaryEvaluationMechanism == null ?
	    											primaryEvaluationMechanism.size() :
	    											primaryEvaluationMechanism.size() + secondaryEvaluationMechanism.size();
	    		Environment.getEnvironment().getStatisticsManager().recordPeakMemoryUsage(memoryUsage);
	    		if (MainConfig.periodicallyReportStatistics) {
	    			StatisticsManager.attemptPeriodicUpdate();
	    		}
    		}
    	}
    	finally {
    		//get last matches
    		recordNewMatches(primaryEvaluationMechanism.getLastMatches());
    		if (secondaryEvaluationMechanism != null) {
    			recordNewMatches(secondaryEvaluationMechanism.getLastMatches());
    		}
    		Environment.getEnvironment().getStatisticsManager().reportStatistics();
			simulationHistory.registerSimulation(currentHistoryId, Environment.getEnvironment().getStatisticsManager());
    	}
    }
    
    private void cleanupEvaluationStep() {
    	eventProducer.finish();
    	Event.resetCounter();
    	if (MainConfig.eventRateMeasurementMode) {
    		System.out.println(eventRateEstimator.getEventRates());
    	}
    	if (MainConfig.debugMode) {
    		return;
    	}
    	System.gc();
    }
    
    private SimulationSpecification[] generateSpecifications() {
    	ISimulationSpecificationCreator specificationCreator = SpecificationCreatorFactory.createSpecificationCreator();
    	if (specificationCreator == null) {
    		return SimulationConfig.specifications;
    	}
    	return specificationCreator.createSpecifications();
    }
	
	private void start() {
		specifications = new ArrayList<SimulationSpecification>();
		SimulationSpecification[] baseSpecifications = generateSpecifications();
		for (SimulationSpecification currentSpecification : baseSpecifications) {
			PatternSpecification patternSpecification = currentSpecification.getPatternSpecification();
			if (SimulationConfig.timeWindows.length == 0 || patternSpecification.getTimeWindow() != null) {
				specifications.add(currentSpecification);
				continue;
			}
			for (long timeWindow : SimulationConfig.timeWindows) {
				long actualTimeWindow = (patternSpecification.getIteratedEventNames().length == 0) ?
										 timeWindow : timeWindow * 2 / 5;
				PatternSpecification newPatternSpecification = 
						new PatternSpecification(patternSpecification.getName(), 
												 patternSpecification.getType(), 
												 actualTimeWindow,
												 patternSpecification.getStructure(),
												 patternSpecification.getNegatedEventNames(),
												 patternSpecification.getIteratedEventNames(),
												 patternSpecification.getConditions());
				specifications.add(new SimulationSpecification(newPatternSpecification, 
															   currentSpecification.getEvaluationSpecification()));
			}
		}
    }
	
	private void runEvaluation() {
		for (SimulationSpecification specification : specifications) {
			try {
				prepareNextEvaluationStep(specification);
				runEvaluationStep();
				cleanupEvaluationStep();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void finish() {
    	Environment.getEnvironment().destroy();
    	if (MainConfig.conditionSelectivityMeasurementMode) {
    		ConditionSelectivityCollector.getInstance().serializeSelectivityEstimates();
    	}
    }
	
	private void execute() {
		start();
		runEvaluation();
		finish();
	}
	
	public static void main(String [] args) {
		if (args.length != 0 && args.length != 2) {
			throw new RuntimeException("Illegal arguments number");
		}
		if (args.length == 2) {
			MainConfig.inputDirsPaths = new String[] {};
			MainConfig.inputFilesPaths = new String[] {args[0]};
			MainConfig.outputFilePath = args[1];
		}
		new Simulator().execute();
	}
}
