package sase.specification.workload;

import sase.pattern.workload.WorkloadManagerTypes;
import sase.specification.creators.condition.ConditionSpecificationCreatorTypes;

public class WorkloadCreationSpecification {

	public final int workloadSize;
	public final int minPatternSize;
	public final int maxPatternSize;
	public final long timeWindow;
	
	public final Integer minDependencyGraphVertexDegree;
	public final Integer maxDependencyGraphVertexDegree;
	public final Integer minPeerIntersectionSize;
	
	public final PatternReorderingSensitivityTypes patternReorderingSensitivity;
	public final ConditionSpecificationCreatorTypes conditionCreatorType;
	
	public final double slaConstraintProbability;
	public final WorkloadManagerTypes managerType;
	public final Double initialCurrentToReservedRatio;
	public final Double workloadModificationProbability;
	
	public WorkloadCreationSpecification(int workloadSize, int minPatternSize, int maxPatternSize, long timeWindow,
										 Integer minDependencyGraphVertexDegree, Integer maxDependencyGraphVertexDegree, 
										 Integer minPeerIntersectionSize,
										 PatternReorderingSensitivityTypes patternReorderingSensitivity,
										 ConditionSpecificationCreatorTypes conditionCreatorType, 
										 double slaConstraintProbability, WorkloadManagerTypes managerType,
										 Double initialCurrentToReservedRatio, Double workloadModificationProbability) {
		this.workloadSize = workloadSize;
		this.minPatternSize = minPatternSize;
		this.maxPatternSize = maxPatternSize;
		this.timeWindow = timeWindow;
		this.minDependencyGraphVertexDegree = minDependencyGraphVertexDegree;
		this.maxDependencyGraphVertexDegree = maxDependencyGraphVertexDegree;
		this.minPeerIntersectionSize = minPeerIntersectionSize;
		this.patternReorderingSensitivity = patternReorderingSensitivity;
		this.conditionCreatorType = conditionCreatorType;
		this.slaConstraintProbability = slaConstraintProbability;
		this.managerType = managerType;
		this.initialCurrentToReservedRatio = initialCurrentToReservedRatio;
		this.workloadModificationProbability = workloadModificationProbability;
	}
	
	public String getShortDescription() {
		String baseDescription = String.format("%d(%d-%d):%d:%s", 
								 workloadSize, minPatternSize, maxPatternSize, timeWindow, patternReorderingSensitivity);
		if (minDependencyGraphVertexDegree != null) {
			baseDescription += String.format("|%d-%d|%d", 
											 minDependencyGraphVertexDegree, maxDependencyGraphVertexDegree,
											 minPeerIntersectionSize);
		}
		if (slaConstraintProbability > 0) {
			baseDescription +=String.format("|SLA(%.0f%%)", slaConstraintProbability * 100);
		}
		if (managerType == WorkloadManagerTypes.MULTI_DYNAMIC) {
			baseDescription += String.format("|DYN(%.0f%%)", workloadModificationProbability * 100);
		}
		return baseDescription;
	}
	
	@Override
	public String toString() {
		String baseDescription = String.format("Workload of %d patterns sized %d-%d, time window %d, %s pattern variance", 
								 workloadSize, minPatternSize, maxPatternSize, timeWindow, patternReorderingSensitivity);
		if (minDependencyGraphVertexDegree != null) {
			baseDescription += String.format(", dependency graph degrees %d-%d, minimal intersection size %d", 
											 minDependencyGraphVertexDegree, maxDependencyGraphVertexDegree,
											 minPeerIntersectionSize);
		}
		if (slaConstraintProbability > 0) {
			baseDescription +=String.format(", SLA on (%.0f%%)", slaConstraintProbability * 100);
		}
		if (managerType == WorkloadManagerTypes.MULTI_DYNAMIC) {
			baseDescription += String.format(", workload modification on (%.0f%%)", workloadModificationProbability * 100);
		}
		return baseDescription;
	}
	
}
