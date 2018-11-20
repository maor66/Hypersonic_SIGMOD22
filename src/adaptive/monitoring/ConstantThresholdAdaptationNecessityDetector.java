package sase.adaptive.monitoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import sase.adaptive.estimation.IEventArrivalRateEstimator;
import sase.base.EventType;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.simulator.Environment;

//TODO: as of now, combining adaptivity and workload modification in a single run is not possible - an
//interface for updating the detector upon pattern addition and removal is to be added
public class ConstantThresholdAdaptationNecessityDetector implements IMultiPatternAdaptationNecessityDetector {

	private final HashMap<EventType, Double> lastKnownEventRates;
	private final HashMap<AtomicCondition, Double> lastKnownConditionSelectivities;
	private final HashMap<EventType, Set<CompositePattern>> eventTypeToPattern;
	private final HashMap<AtomicCondition, Set<CompositePattern>> conditionToPattern;
	private final Set<EventType> deviatedTypes;
	private final Set<AtomicCondition> deviatedConditions;
	private final Double threshold;
	
	public ConstantThresholdAdaptationNecessityDetector(List<Pattern> workload, Double threshold) {
		lastKnownEventRates = new HashMap<EventType, Double>();
		lastKnownConditionSelectivities = new HashMap<AtomicCondition, Double>();
		eventTypeToPattern = new HashMap<EventType, Set<CompositePattern>>();
		conditionToPattern = new HashMap<AtomicCondition, Set<CompositePattern>>();
		deviatedTypes = new HashSet<EventType>();
		deviatedConditions = new HashSet<AtomicCondition>();
		this.threshold = threshold;
		for (Pattern pattern : workload) {
			CompositePattern compositePattern = (CompositePattern)pattern;
			registerEventTypes(compositePattern);
			registerConditions(compositePattern);
		}
	}
	
	private void registerEventTypes(CompositePattern pattern) {
		IEventArrivalRateEstimator eventRateEstimator = Environment.getEnvironment().getEventRateEstimator();
		for (EventType eventType : pattern.getEventTypes()) {
			if (!lastKnownEventRates.containsKey(eventType)) {
				lastKnownEventRates.put(eventType, eventRateEstimator.getEventRateEstimate(eventType));
			}
			if (!eventTypeToPattern.containsKey(eventType)) {
				eventTypeToPattern.put(eventType, new HashSet<CompositePattern>());
			}
			eventTypeToPattern.get(eventType).add(pattern);
		}
	}
	
	private void registerConditions(CompositePattern pattern) {
		for (AtomicCondition condition : pattern.getCNFCondition().getAtomicConditions()) {
			if (!lastKnownConditionSelectivities.containsKey(condition)) {
				lastKnownConditionSelectivities.put(condition, condition.getSelectivity());
			}
			if (!conditionToPattern.containsKey(condition)) {
				conditionToPattern.put(condition, new HashSet<CompositePattern>());
			}
			conditionToPattern.get(condition).add(pattern);
		}
	}
	
	@Override
	public boolean shouldAdapt() {
		IEventArrivalRateEstimator eventRateEstimator = Environment.getEnvironment().getEventRateEstimator();
		for (Entry<EventType, Double> entry : lastKnownEventRates.entrySet()) {
			Double estimation = eventRateEstimator.getEventRateEstimate(entry.getKey());
			Double deviation = Math.abs(entry.getValue() - estimation);
			if (deviation / entry.getValue() > threshold) {
				deviatedTypes.add(entry.getKey());
				entry.setValue(estimation);
			}
		}
		for (Entry<AtomicCondition, Double> entry : lastKnownConditionSelectivities.entrySet()) {
			Double estimation = entry.getKey().getSelectivity();
			Double deviation = Math.abs(entry.getValue() - estimation);
			if (deviation / entry.getValue() > threshold) {
				deviatedConditions.add(entry.getKey());
				entry.setValue(estimation);
			}
		}
		return !deviatedTypes.isEmpty() || !deviatedConditions.isEmpty();
	}

	@Override
	public Set<CompositePattern> getAffectedPatterns() {
		Set<CompositePattern> affectedPatterns = new HashSet<CompositePattern>();
		for (EventType eventType : deviatedTypes) {
			affectedPatterns.addAll(eventTypeToPattern.get(eventType));
		}
		deviatedTypes.clear();
		for (AtomicCondition condition : deviatedConditions) {
			affectedPatterns.addAll(conditionToPattern.get(condition));
		}
		deviatedConditions.clear();
		return affectedPatterns;
	}

}
