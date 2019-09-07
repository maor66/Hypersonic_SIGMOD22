package sase.statistics;

public class Statistics {

	//discrete statistics
	public static final String processingTime = "Processing Time (ms)";
	public static final String evaluationMechanismCreationTime = "NFA/Tree Creation Time (ms)";
	public static final String computations = "Number Of Computations";
	public static final String parallelComputations = "Number Of Parallel Computations";
	public static final String timeComparisons = "Number of comparison of timestamp";
	public static final String parallelTimeComparisons = "Number of Parallel Comparisons Of Timestamps";
	public static final String correlationComputations = "Number Of Correlation Computations";
	public static final String aggregationComputations = "Number Of Aggregation Computations";
	public static final String memoryOperations = "Number Of Memory Operations";
	public static final String instanceCreations = "Number Of Instance Creations";
	public static final String parallelPartialMatchesCreations = "Parallel Partial Matches Creations";
	public static final String instanceDeletions = "Number Of Instance Deletions";
	public static final String parallelPartialMatchesDeleltions = "Parallel Partial Instance Deletions";
	public static final String bufferInsertions = "Number Of Buffer Insertion Operations";
	public static final String parallelBufferInsertions = "Number Of Parallel Buffer Insertion Operations";
	public static final String bufferRemovals = "Number Of Buffer Removal Operations";
	public static final String parallelBufferRemovals = "Number Of Parallel Buffer Removal Operations";
	public static final String events = "Number Of Events";
	public static final String matches = "Number Of Matches";
	public static final String peakInstances = "Peak Instances";
	public static final String peakBufferedEvents = "Peak Buffered Events";
	public static final String peakMemory = "Peak Memory Consumption";
	public static final String adaptationTime = "Adaptation Time (ms)";
	public static final String numberOfAdaptations = "Number Of Adaptations";
	public static final String automatonStatesNumber = "Automaton States Number";
	public static final String automatonTransitionsNumber = "Automaton Transitions Number";
	public static final String treeNodesNumber = "Tree Nodes Number";
	public static final String isTimeoutReached = "Timeout";
	public static final String numberOfInputChanges = "Number Of Input Changes";
	public static final String numberOfUndetectedInputChanges = "Number Of Undetected Input Changes";
	public static final String numberOfFalseAdaptations = "Number Of False Adaptations";
	public static final String numberOfSynchronizationActions = "Number Of Synchronization Actions";
	public static final String successfulOptimisticReads = "Number Of Successful optimistic reads";
	public static final String failedOptimisticReads = "Number Of failed optimistic reads";
	public static final String isSmartBalancing = "Balancing for the parallel algorithm";

	
	//fractional statistics
	public static final String averageLatency = "Average Latency";
	public static final String averageInputChangeDetectionTime = "Average Input Change Detection Time";
	public static final String evaluationPlanCost = "Evaluation Plan Cost";


    public static String[] getDiscreteOrderedNames() {
		return new String[] {
			processingTime,
			evaluationMechanismCreationTime,
			computations,
			parallelComputations,
				timeComparisons,
				parallelTimeComparisons,
			correlationComputations,
			aggregationComputations,
			memoryOperations,
			instanceCreations,
				parallelPartialMatchesCreations,
			instanceDeletions,
				parallelPartialMatchesDeleltions,
			bufferInsertions,
				parallelBufferInsertions,
			bufferRemovals,
				parallelBufferRemovals,
			events,
			matches,
			peakInstances,
			peakBufferedEvents,
			peakMemory,
			adaptationTime,
			numberOfAdaptations,
			automatonStatesNumber,
			automatonTransitionsNumber,
			treeNodesNumber,
			isTimeoutReached,
			numberOfInputChanges,
			numberOfUndetectedInputChanges,
			numberOfFalseAdaptations,
			numberOfSynchronizationActions,
				successfulOptimisticReads,
				failedOptimisticReads,
			isSmartBalancing,
		};
	}
	
	public static String[] getFractionalOrderedNames() {
		return new String[] {
			averageLatency,
			averageInputChangeDetectionTime,
			evaluationPlanCost,
		};
	}

}
