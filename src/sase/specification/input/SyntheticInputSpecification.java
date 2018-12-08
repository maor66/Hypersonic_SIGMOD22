package sase.specification.input;

import sase.input.EventStreamModifierTypes;

public class SyntheticInputSpecification extends InputSpecification {

	//stream generation settings
	public final long numberOfEvents;
	public final int numberOfEventTypes;
	public final double minimalArrivalRate;
	public final double maximalArrivalRate;
	public final double minimalSelectivity;
	public final double maximalSelectivity;
	public final long conditionEvaluationDuration;
	
	//dynamic stream generation settings
	public final double averageArrivalRateChangeRange;
	public final double varianceInArrivalRateChangeRange;
	public final double averageSelectivityChangeRange;
	public final double varianceInSelectivityChangeRange;
	public final long averageTimeBetweenInputChanges;
	public final long varianceInTimeBetweenInputChanges;
	public final int numberOfSimultaneousInputChanges;
	
	public SyntheticInputSpecification(long numberOfEvents,
									   int numberOfEventTypes,
									   double minimalArrivalRate,
									   double maximalArrivalRate,
									   double minimalSelectivity,
									   double maximalSelectivity,
									   long conditionEvaluationDuration,
									   double averageArrivalRateChangeRange,
									   double varianceInArrivalRateChangeRange,
									   double averageSelectivityChangeRange,
									   double varianceInSelectivityChangeRange,
									   long averageTimeBetweenInputChanges,
									   long varianceInTimeBetweenInputChanges,
									   int numberOfSimultaneousInputChanges) {
		super(EventStreamModifierTypes.SYNTHETIC);
		
		//stream generation settings
		this.numberOfEvents = numberOfEvents;
		this.numberOfEventTypes = numberOfEventTypes;
		this.minimalArrivalRate = minimalArrivalRate;
		this.maximalArrivalRate = maximalArrivalRate;
		this.minimalSelectivity = minimalSelectivity;
		this.maximalSelectivity = maximalSelectivity;
		this.conditionEvaluationDuration = conditionEvaluationDuration;
		
		//dynamic stream generation settings
		this.averageArrivalRateChangeRange = averageArrivalRateChangeRange;
		this.varianceInArrivalRateChangeRange = varianceInArrivalRateChangeRange;
		this.averageSelectivityChangeRange = averageSelectivityChangeRange;
		this.varianceInSelectivityChangeRange = varianceInSelectivityChangeRange;
		this.averageTimeBetweenInputChanges = averageTimeBetweenInputChanges;
		this.varianceInTimeBetweenInputChanges = varianceInTimeBetweenInputChanges;
		this.numberOfSimultaneousInputChanges = numberOfSimultaneousInputChanges;
	}

	@Override
	public String getShortDescription() {
		return String.format("%de%dt(%.3f-%.3f)(%.3f-%.3f)|[%.3f+-%.3f][%.3f+-%.3f][%d+-%d]", 
				 numberOfEvents, numberOfEventTypes, minimalArrivalRate, maximalArrivalRate, minimalSelectivity, 
				 maximalSelectivity, averageArrivalRateChangeRange, varianceInArrivalRateChangeRange, 
				 averageSelectivityChangeRange, varianceInSelectivityChangeRange, averageTimeBetweenInputChanges, 
				 varianceInTimeBetweenInputChanges);
	}

	@Override
	public String getLongDescription() {
		return String.format("Generated %d events of %d types\n" + 
				 "Initial arrival rates range: (%.3f-%.3f)\n" +
				 "Initial selectivities rage: (%.3f-%.3f)\n" + 
				 "Changes in arrival rates: [%.3f+-%.3f]\n" +
				 "Changes in selectivities: [%.3f+-%.3f]\n" +
				 "Dynamic modification intervals: [%d+-%d]\n", 
				 numberOfEvents, numberOfEventTypes, minimalArrivalRate, maximalArrivalRate, minimalSelectivity, 
				 maximalSelectivity, averageArrivalRateChangeRange, varianceInArrivalRateChangeRange, 
				 averageSelectivityChangeRange, varianceInSelectivityChangeRange, averageTimeBetweenInputChanges, 
				 varianceInTimeBetweenInputChanges);
	}

}
