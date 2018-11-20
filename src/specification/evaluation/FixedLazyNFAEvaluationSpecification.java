package sase.specification.evaluation;

import sase.evaluation.nfa.lazy.LazyNFANegationTypes;
import sase.pattern.EventTypesManager;

public class FixedLazyNFAEvaluationSpecification extends LazyNFAEvaluationSpecification {

	public String[] evaluationOrder;
	
	public FixedLazyNFAEvaluationSpecification(LazyNFANegationTypes negationType, String[] evaluationOrder) {
		super(negationType);
		this.evaluationOrder  = evaluationOrder;
	}
	
	private String getShortEvaluationOrder() {
		if (evaluationOrder == null)
			return "";
		String result = "";
		for (int i = 0; i < evaluationOrder.length; ++i) {
			String currentIdentifier = EventTypesManager.getInstance().getShortNameByLongName(evaluationOrder[i]);
			result += currentIdentifier;
			if (i < evaluationOrder.length - 1) {
				result += ",";
			}
		}
		return result;
	}
	
	private String evaluationOrderAsString() {
		if (evaluationOrder == null)
			return "";
		String result = "";
		for (int i = 0; i < evaluationOrder.length; ++i) {
			result += evaluationOrder[i];
			if (i < evaluationOrder.length - 1) {
				result += ",";
			}
		}
		return result;
	}
	
	@Override
	public String getShortDescription() {
		return String.format("%s|%s|%s", type, getShortEvaluationOrder(), negationType);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("%s (fixed order (%s), negation support: %s)", type, evaluationOrderAsString(), negationType);
	}

}
