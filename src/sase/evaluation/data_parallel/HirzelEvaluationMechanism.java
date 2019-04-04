package sase.evaluation.data_parallel;

import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.specification.evaluation.HirzelEvaluationSpecification;

public final class HirzelEvaluationMechanism extends DataParallelEvaluationMechanism {
	// For Hirzel algorithm implementation we need to choose an attribute!
	private String attribute;
	
	public HirzelEvaluationMechanism(Pattern pattern, HirzelEvaluationSpecification specification, EvaluationPlan evaluationPlan) {
		super(pattern, specification, evaluationPlan);
		this.attribute = specification.attribute;
	}
	
	@Override
	protected void scheduleEvent(EvaluationInput evaluationInput) {
		// Find the relevant attribute value
		String attributeValue = (String)evaluationInput.event.getAttributeValue(attribute).toString();
		// Select a thread id by attribute value
		
		int id = attributeValue.hashCode() % numOfThreads;
		
		// MAX : THIS CODE IS FOR THE HIRZEL TEST CASE!
		// TO ENABLE IT: please go to MainConfig and set eventTypesConverterType to
		// EventTypesConverterTypes.STOCK_BY_REGION
		// Also go to PatternConfig and in stockByCompanyPatternSpecifications comment all
		// Except the lines under MAX comment
//		String firstLetter = attributeValue.substring(0, 1);
//		int id = firstLetter.hashCode() % numOfThreads;
		
		// Add to thread blocking queue
		((ParallelThread)threads[id]).threadInput.add(evaluationInput);
	}

	
}
