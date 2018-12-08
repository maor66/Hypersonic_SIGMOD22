package sase.specification.creators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sase.config.SimulationConfig;
import sase.specification.SimulationSpecification;
import sase.specification.adaptation.AdaptationSpecification;
import sase.specification.evaluation.EvaluationSpecification;
import sase.specification.input.InputSpecification;
import sase.specification.workload.WorkloadSpecification;
import sase.specification.workload.WorkloadCreationSpecification;

public abstract class CrossProductSimulationSpecificationCreator implements ISimulationSpecificationCreator {

	@Override
	public SimulationSpecification[] createSpecifications() {
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		Map<WorkloadSpecification, WorkloadCreationSpecification> workloadSpecifications = createWorkloadSpecifications();
		for (Entry<WorkloadSpecification, WorkloadCreationSpecification> workloadSpecification : 
																						workloadSpecifications.entrySet()) {
			for (EvaluationSpecification evaluationSpecification : SimulationConfig.evaluationSpecifications) {
				for (AdaptationSpecification adaptationSpecification : SimulationConfig.adaptationSpecifications) {
					for (InputSpecification inputSpecification : SimulationConfig.inputSpecifications) {
						result.add(new SimulationSpecification(workloadSpecification.getKey(),
															   workloadSpecification.getValue(),
															   evaluationSpecification,
															   adaptationSpecification, 
															   inputSpecification));
					}
				}
			}
		}
		return result.toArray(new SimulationSpecification[0]);
	}

	protected abstract Map<WorkloadSpecification, WorkloadCreationSpecification> createWorkloadSpecifications();

}
