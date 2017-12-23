package specification;

public class InputSpecification {

	//stream modification settings
	public final int filteringFactor;
	public final int duplicatingFactor;
	private static final int defaultFactorValue = 1;
	
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
	
	public InputSpecification() {
		this(defaultFactorValue, defaultFactorValue);
	}
	
	public InputSpecification(int filteringFactor,
  		     				  int duplicatingFactor) {
		this(filteringFactor, duplicatingFactor,
			 0,0,0,0,0,0,0,0,0,0,0,0,0,0);
	}
	
	public InputSpecification(long numberOfEvents,
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
		this(defaultFactorValue, defaultFactorValue, numberOfEvents, numberOfEventTypes, 
			 minimalArrivalRate, maximalArrivalRate, minimalSelectivity, maximalSelectivity, conditionEvaluationDuration,
			 averageArrivalRateChangeRange, varianceInArrivalRateChangeRange, averageSelectivityChangeRange,
			 varianceInSelectivityChangeRange, averageTimeBetweenInputChanges, varianceInTimeBetweenInputChanges,
			 numberOfSimultaneousInputChanges);
	}
	
	public InputSpecification(int filteringFactor,
				  			  int duplicatingFactor,
				  			  long numberOfEvents,
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
		this.filteringFactor = filteringFactor;
		this.duplicatingFactor = duplicatingFactor;
		
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

	private String getGenerationSettingsShortDescription() {
		return String.format("%de%dt(%.3f-%.3f)(%.3f-%.3f)|[%.3f+-%.3f][%.3f+-%.3f][%d+-%d]", 
							 numberOfEvents, numberOfEventTypes, minimalArrivalRate, maximalArrivalRate, minimalSelectivity, 
							 maximalSelectivity, averageArrivalRateChangeRange, varianceInArrivalRateChangeRange, 
							 averageSelectivityChangeRange, varianceInSelectivityChangeRange, averageTimeBetweenInputChanges, 
							 varianceInTimeBetweenInputChanges);
	}

	private String getGenerationSettingsLongDescription() {
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
	
	public String getShortDescription() {
		if (filteringFactor == defaultFactorValue && duplicatingFactor == defaultFactorValue) {
			return numberOfEvents == 0 ? "" : getGenerationSettingsShortDescription();
		}
		return String.format("(%s,%s)", filteringFactor, duplicatingFactor);
	}
	
	public String getLongDescription() {
		if (filteringFactor == defaultFactorValue && duplicatingFactor == defaultFactorValue) {
			return numberOfEvents == 0 ? "" : getGenerationSettingsLongDescription();
		}
		return String.format("input filtering factor: %d, input duplicating factor: %d",
				 filteringFactor, duplicatingFactor);
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}
	
}
