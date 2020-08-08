package sase.specification;

import sase.specification.adaptation.AdaptationSpecification;
import sase.specification.adaptation.TrivialAdaptationSpecification;
import sase.specification.evaluation.EvaluationSpecification;
import sase.specification.input.InputSpecification;
import sase.specification.input.TrivialInputSpecification;
import sase.specification.workload.WorkloadCreationSpecification;
import sase.specification.workload.WorkloadSpecification;

public class SimulationSpecification {

	private final WorkloadSpecification workloadSpecification;
	private final WorkloadCreationSpecification workloadCreationSpecification;
	private final EvaluationSpecification evaluationSpecification;
	private final InputSpecification inputSpecification;
	private final AdaptationSpecification adaptationSpecification;
	
	public SimulationSpecification(WorkloadSpecification workloadSpecification, EvaluationSpecification evaluationSpecification) {
		this(workloadSpecification, evaluationSpecification, new TrivialAdaptationSpecification());
	}
	
	
	
	public SimulationSpecification(WorkloadSpecification workloadSpecification,
			WorkloadCreationSpecification workloadCreationSpecification,
			EvaluationSpecification evaluationSpecification,
			AdaptationSpecification adaptationSpecification,
			InputSpecification inputSpecification) {
		this.workloadSpecification = workloadSpecification;
		this.workloadCreationSpecification = workloadCreationSpecification;
		this.evaluationSpecification = evaluationSpecification;
		this.inputSpecification = inputSpecification;
		this.adaptationSpecification = adaptationSpecification;
	}



	public SimulationSpecification(WorkloadSpecification workloadSpecification,
								   EvaluationSpecification evaluationSpecification,
								   AdaptationSpecification adaptationSpecification,
								   InputSpecification inputSpecification) {
		this(workloadSpecification, null, evaluationSpecification, adaptationSpecification, inputSpecification);
	}
	
	public SimulationSpecification(WorkloadSpecification patternSpecification,
								   EvaluationSpecification evaluationSpecification,
								   AdaptationSpecification adaptationSpecification) {
		this(patternSpecification, evaluationSpecification, adaptationSpecification, new TrivialInputSpecification());
	}

	public WorkloadSpecification getWorkloadSpecification() {
		return workloadSpecification;
	}

	public WorkloadCreationSpecification getWorkloadCreationSpecification() {
		return workloadCreationSpecification;
	}

	public EvaluationSpecification getEvaluationSpecification() {
		return evaluationSpecification;
	}

	public AdaptationSpecification getAdaptationSpecification() {
		return adaptationSpecification;
	}

	public InputSpecification getInputSpecification() {
		return inputSpecification;
	}

	public String getShortDescription() {
		return String.format("%s;%s;%s;%s",
				 			 evaluationSpecification.getShortDescription(),
				 			 workloadCreationSpecification == null ? workloadSpecification.toString() :
				 				 									 workloadCreationSpecification.getShortDescription(),
							 adaptationSpecification.getShortDescription(),
							 inputSpecification.getShortDescription());
	}
	
	public String getLongDescription() {
		return String.format("Simulation Details:\n%s\n%s\n%s\n%s\n", 
	 			 			 workloadCreationSpecification == null ? workloadSpecification : workloadCreationSpecification, 
							 evaluationSpecification, 
							 adaptationSpecification, 
							 inputSpecification);
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}

}
