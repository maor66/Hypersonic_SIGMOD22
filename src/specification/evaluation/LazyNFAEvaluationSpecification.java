package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.LazyNFANegationTypes;

public class LazyNFAEvaluationSpecification extends EvaluationSpecification {

	public LazyNFANegationTypes negationType;

	public LazyNFAEvaluationSpecification(LazyNFANegationTypes negationType) {
		super(EvaluationMechanismTypes.LAZY_CHAIN);
		this.negationType = negationType;
	}

}
