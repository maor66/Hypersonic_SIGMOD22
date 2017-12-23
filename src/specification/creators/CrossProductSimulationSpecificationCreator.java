package specification.creators;

import java.util.ArrayList;
import java.util.List;

import config.SimulationConfig;
import specification.AdaptationSpecification;
import specification.EvaluationSpecification;
import specification.InputSpecification;
import specification.PatternSpecification;
import specification.SimulationSpecification;

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
