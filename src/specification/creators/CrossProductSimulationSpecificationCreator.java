package sase.specification.creators;

import java.util.ArrayList;
import java.util.List;

import sase.config.SimulationConfig;
import sase.specification.AdaptationSpecification;
import sase.specification.EvaluationSpecification;
import sase.specification.InputSpecification;
import sase.specification.PatternSpecification;
import sase.specification.SimulationSpecification;

public class CrossProductSimulationSpecificationCreator implements ISimulationSpecificationCreator {

	@Override
	public SimulationSpecification[] createSpecifications() {
		List<SimulationSpecification> result = new ArrayList<SimulationSpecification>();
		for (PatternSpecification patternSpecification : SimulationConfig.patternSpecifications) {
			for (EvaluationSpecification evaluationSpecification : SimulationConfig.evaluationSpecifications) {
				for (AdaptationSpecification adaptationSpecification : SimulationConfig.adaptationSpecifications) {
					for (InputSpecification inputSpecification : SimulationConfig.inputSpecifications) {
						result.add(new SimulationSpecification(patternSpecification, 
															   evaluationSpecification,
															   adaptationSpecification, 
															   inputSpecification));
					}
				}
			}
		}
		return result.toArray(new SimulationSpecification[0]);
	}

}
