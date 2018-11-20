package sase.specification.workload;

import java.util.List;

import sase.pattern.workload.WorkloadManagerTypes;

public class DynamicMultiPatternWorkloadSpecification extends WorkloadSpecification {

	public final Double initialCurrentToReservedRatio;
	public final Double workloadModificationProbability;
	
	public DynamicMultiPatternWorkloadSpecification(List<PatternSpecification> patternSpecifications,
													Double initialCurrentToReservedRatio,
													Double workloadModificationProbability) {
		super(patternSpecifications, WorkloadManagerTypes.MULTI_DYNAMIC);
		this.initialCurrentToReservedRatio = initialCurrentToReservedRatio;
		this.workloadModificationProbability = workloadModificationProbability;
	}

}
