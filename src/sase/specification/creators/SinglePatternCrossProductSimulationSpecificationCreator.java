package sase.specification.creators;

import java.util.HashMap;
import java.util.Map;

import sase.config.SimulationConfig;
import sase.specification.workload.PatternSpecification;
import sase.specification.workload.SinglePatternWorkloadSpecification;
import sase.specification.workload.WorkloadCreationSpecification;
import sase.specification.workload.WorkloadSpecification;

public class SinglePatternCrossProductSimulationSpecificationCreator extends CrossProductSimulationSpecificationCreator
		implements ISimulationSpecificationCreator {

	@Override
	protected Map<WorkloadSpecification, WorkloadCreationSpecification> createWorkloadSpecifications() {
		Map<WorkloadSpecification, WorkloadCreationSpecification> result = 
									new HashMap<WorkloadSpecification, WorkloadCreationSpecification>();
		for (PatternSpecification patternSpecification : SimulationConfig.patternSpecifications) {
			result.put(new SinglePatternWorkloadSpecification(patternSpecification), null);
		}
		return result;
	}

}
