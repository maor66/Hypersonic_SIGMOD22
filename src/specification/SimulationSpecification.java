package sase.specification;

public class SimulationSpecification {

	private final PatternSpecification patternSpecification;
	private final EvaluationSpecification evaluationSpecification;
	private final InputSpecification inputSpecification;
	private final AdaptationSpecification adaptationSpecification;
	
	public SimulationSpecification(PatternSpecification patternSpecification, EvaluationSpecification evaluationSpecification) {
		this(patternSpecification, evaluationSpecification, new AdaptationSpecification());
	}
	
	public SimulationSpecification(PatternSpecification patternSpecification,
								   EvaluationSpecification evaluationSpecification,
								   AdaptationSpecification adaptationSpecification,
								   InputSpecification inputSpecification) {
		this.patternSpecification = patternSpecification;
		this.evaluationSpecification = evaluationSpecification;
		this.adaptationSpecification = adaptationSpecification;
		this.inputSpecification = inputSpecification;
	}
	
	public SimulationSpecification(PatternSpecification patternSpecification,
								   EvaluationSpecification evaluationSpecification,
								   AdaptationSpecification adaptationSpecification) {
		this(patternSpecification, evaluationSpecification, adaptationSpecification, new InputSpecification());
	}

	public PatternSpecification getPatternSpecification() {
		return patternSpecification;
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
							 patternSpecification.getShortDescription(),
							 evaluationSpecification.getShortDescription(),
							 adaptationSpecification.getShortDescription(),
							 inputSpecification.getShortDescription());
	}
	
	public String getLongDescription() {
		return String.format("Simulation Details:\n%s\n%s\n%s\n%s\n", 
							 patternSpecification, evaluationSpecification, adaptationSpecification, inputSpecification);
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}

}
