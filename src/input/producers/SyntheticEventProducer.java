package input.producers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import base.Event;
import base.EventType;
import input.EventProducer;
import pattern.Pattern;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;
import simulator.Environment;
import specification.InputSpecification;
import specification.SimulationSpecification;
import statistics.Statistics;
import statistics.StatisticsManager;
import user.synthetic.SyntheticCondition;

public class SyntheticEventProducer extends EventProducer implements ISyntheticSelectivityProvider {
	
	private final InputSpecification specification;
	private long currentTimestamp = 0;
	private long numberOfSentEvents;
	private Double[] currentArrivalRates;
	private List<Integer> indicesOfParticipatingTypes;
	private Double[] currentSelectivities;
	private Double[] previousSelectivities;
	
	private Long timeOfLastInputModification = null;
	private Long timeOfNextInputModification = null;
	
	public SyntheticEventProducer(Pattern pattern, SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
		this.specification = simulationSpecification.getInputSpecification();
		initializeArrivalRates();
		SyntheticCondition.reset();
		initializeSelectivities((CNFCondition)pattern.getCondition());
		initializeIndicesOfParticipatingTypes(pattern.getEventTypes());
		calculateNextInputModificationTime();
	}
	
	private void initializeArrivalRates() {
		currentArrivalRates = new Double[specification.numberOfEventTypes];
		double arrivalRateDelta = specification.maximalArrivalRate - specification.minimalArrivalRate;
		for (int i = 0; i < specification.numberOfEventTypes; ++i) {
			currentArrivalRates[i] = specification.minimalArrivalRate + new Random().nextDouble() * arrivalRateDelta;
		}
	}
	
	private void initializeSelectivities(CNFCondition mainCondition) {
		List<AtomicCondition> atomicConditions = mainCondition.getAtomicConditions();
		currentSelectivities = new Double[atomicConditions.size()];
		previousSelectivities = new Double[atomicConditions.size()];
		Double selectivityDelta = specification.maximalSelectivity - specification.minimalSelectivity;
		for (AtomicCondition atomicCondition : atomicConditions) {
			if (!(atomicCondition instanceof SyntheticCondition)) {
				throw new RuntimeException(String.format("Invalid condition detected: %s", atomicCondition));
			}
			SyntheticCondition condition = (SyntheticCondition)atomicCondition;
			condition.setSelectivityProvider(this);
			int conditionID = condition.getId();
			validateArrayBounds(conditionID, currentSelectivities.length, "Illegal condition ID specified: %d");
			currentSelectivities[conditionID] = 
				specification.minimalSelectivity + new Random().nextDouble() * selectivityDelta;
		}
	}
	
	private void initializeIndicesOfParticipatingTypes(List<EventType> eventTypes) {
		indicesOfParticipatingTypes = new ArrayList<Integer>();
		for (EventType eventType : eventTypes) {
			indicesOfParticipatingTypes.add(Integer.parseInt(eventType.getName()));
		}
	}
	
	private void validateArrayBounds(int index, int size, String error) {
		if (index < 0 || index >= size) {
			throw new RuntimeException(String.format(error, index));
		}
	}
	
	private List<String[]> createEventsForType(int typeID) {
		Double currentArrivalRate = currentArrivalRates[typeID];
		long numberOfEvents = (long)Math.floor(currentArrivalRate);
		if (new Random().nextDouble() <= (currentArrivalRate - Math.floor(currentArrivalRate))) {
			numberOfEvents++;
		}
		List<String[]> result = new ArrayList<String[]>();
		for (int i = 0; i < numberOfEvents; ++i) {
			String[] newEvent = new String[] {
				String.valueOf(typeID),
				String.valueOf(currentTimestamp),	
			};
			result.add(newEvent);
		}
		return result;
	}
	
	private void executeInputModification() {
		int numberOfArrivalRateModifications = new Random().nextInt(specification.numberOfSimultaneousInputChanges);
		if (numberOfArrivalRateModifications > indicesOfParticipatingTypes.size()) {
			numberOfArrivalRateModifications = indicesOfParticipatingTypes.size();
		}
		int numberOfSelectivityModifications = 
									specification.numberOfSimultaneousInputChanges - numberOfArrivalRateModifications;
		modifyArrivalRates(numberOfArrivalRateModifications);
		modifySelectivities(numberOfSelectivityModifications);
	}

	private void modifyArrivalRates(int numberOfArrivalRateModifications) {
		List<Integer> usedIndices = new ArrayList<Integer>();
		int i = 0;
		while (i < numberOfArrivalRateModifications) {
			int currentIndex = new Random().nextInt(currentArrivalRates.length);
			while (!isValidRateUpdateIndex(currentIndex, usedIndices)) {
				currentIndex = new Random().nextInt(currentArrivalRates.length);
			}
			double variance = new Random().nextDouble() * 2 * specification.varianceInArrivalRateChangeRange;
			variance -= specification.varianceInArrivalRateChangeRange;
			double delta = specification.averageArrivalRateChangeRange + variance;
			if (new Random().nextBoolean()) {
				delta = -delta;
			}
			if (currentArrivalRates[currentIndex] + delta >= specification.minimalArrivalRate &&
				currentArrivalRates[currentIndex] + delta <= specification.maximalArrivalRate) {
					currentArrivalRates[currentIndex] += delta;
					usedIndices.add(currentIndex);
					i++;
			}
		}
	}
	
	private boolean isValidRateUpdateIndex(int index, List<Integer> usedIndices) {
		boolean found = false;
		for (Integer integer : indicesOfParticipatingTypes) {
			if (integer.intValue() == index) {
				found = true;
				break;
			}
		}
		if (!found) {
			return false;
		}
		for (Integer integer : usedIndices) {
			if (integer.intValue() == index) {
				return false;
			}
		}
		return true;
	}
	
	private void modifySelectivities(int numberOfSelectivityModifications) {
		for (int i = 0; i < currentSelectivities.length; ++i) {
			previousSelectivities[i] = currentSelectivities[i];
		}
		List<Integer> usedIndices = new ArrayList<Integer>();
		int i = 0;
		while (i < numberOfSelectivityModifications) {
			int currentIndex = new Random().nextInt(currentSelectivities.length);
			while (!isValidSelectivityUpdateIndex(currentIndex, usedIndices)) {
				currentIndex = new Random().nextInt(currentSelectivities.length);
			}
			double variance = new Random().nextDouble() * 2 * specification.varianceInSelectivityChangeRange;
			variance -= specification.varianceInSelectivityChangeRange;
			double delta = specification.averageSelectivityChangeRange + variance;
			if (new Random().nextBoolean()) {
				delta = -delta;
			}
			if (currentSelectivities[currentIndex] + delta >= specification.minimalSelectivity &&
				currentSelectivities[currentIndex] + delta <= specification.maximalSelectivity) {
					currentSelectivities[currentIndex] += delta;
					usedIndices.add(currentIndex);
					i++;
			}
		}
	}
	
	private boolean isValidSelectivityUpdateIndex(int index, List<Integer> usedIndices) {
		for (Integer integer : usedIndices) {
			if (integer.intValue() == index) {
				return false;
			}
		}
		return true;
	}

	private void calculateNextInputModificationTime() {
		int delta = 0;
		if (specification.varianceInTimeBetweenInputChanges > 0) {
			delta = new Random().nextInt(2 * (int)specification.varianceInTimeBetweenInputChanges);
			delta -= specification.varianceInTimeBetweenInputChanges;
		}
		timeOfNextInputModification = currentTimestamp + specification.averageTimeBetweenInputChanges + delta;
	}
	
	private boolean actuallyCreateMoreEvents() {
		List<String[]> newEvents = new ArrayList<String[]>();
		for (int i = 0; i < specification.numberOfEventTypes; ++i) {
			newEvents.addAll(createEventsForType(i));
		}
		Collections.shuffle(newEvents);
		boolean newEventsCreated = false;
		for (String[] event : newEvents) {
			newEventsCreated |= produceActualEvents(event);
			numberOfSentEvents++;
			if (!canCreateMoreEvents()) {
				break;
			}
		}
		return newEventsCreated;
	}

	@Override
	protected boolean createMoreEvents() {
		if (!canCreateMoreEvents()) {
			return false;
		}
		currentTimestamp++;
		if (specification.numberOfSimultaneousInputChanges > 0 && currentTimestamp >= timeOfNextInputModification) {
			executeInputModification();
			timeOfLastInputModification = currentTimestamp;
			calculateNextInputModificationTime();
			StatisticsManager statisticsManager = Environment.getEnvironment().getStatisticsManager();
			statisticsManager.incrementDiscreteStatistic(Statistics.numberOfInputChanges);
			if (statisticsManager.isTimeMeasuredForStatistic(Statistics.averageInputChangeDetectionTime)) {
				statisticsManager.incrementDiscreteStatistic(Statistics.numberOfUndetectedInputChanges);
			}
			statisticsManager.startMeasuringTime(Statistics.averageInputChangeDetectionTime);
		}
		boolean newEventsCreated = false;
		while (!newEventsCreated) {
			newEventsCreated = actuallyCreateMoreEvents();
		}
		return true;
	}

	@Override
	protected boolean canCreateMoreEvents() {
		return numberOfSentEvents < specification.numberOfEvents;
	}

	@Override
	public void finish() {
	}

	@Override
	public double getSelectivity(int conditionID, Event firstEvent, Event secondEvent) {
		validateArrayBounds(conditionID, currentSelectivities.length, "Illegal condition ID specified: %d");
		if (timeOfLastInputModification != null &&
			firstEvent.getTimestamp() < timeOfLastInputModification &&
			secondEvent.getTimestamp() < timeOfLastInputModification) {
				return previousSelectivities[conditionID];
		}
		return currentSelectivities[conditionID];
	}

	@Override
	public long getConditionEvaluationDuration() {
		return specification.conditionEvaluationDuration;
	}
}
