package sase.specification.workload;

import java.util.Arrays;

import sase.pattern.workload.WorkloadManagerTypes;

public class SinglePatternWorkloadSpecification extends WorkloadSpecification {

	public SinglePatternWorkloadSpecification(PatternSpecification patternSpecification) {
		super(Arrays.asList(patternSpecification), WorkloadManagerTypes.SINGLE);
	}
	
	public PatternSpecification getPatternSpecification() {
		return patternSpecifications.get(0);
	}
	
	@Override
	public String getShortDescription() {
		return getPatternSpecification().getShortDescription();
	}
	
	@Override
	public String toString() {
		return getPatternSpecification().toString();
	}

}
