package sase.specification.evaluation;

import sase.evaluation.EvaluationMechanismTypes;
import sase.evaluation.nfa.lazy.LazyNFANegationTypes;

public class LazyNFAEvaluationSpecification extends EvaluationSpecification {

	public LazyNFANegationTypes negationType;

	public LazyNFAEvaluationSpecification(LazyNFANegationTypes negationType) {
		//Maor: Here I should have a different EvaluationSpecification such that the MechanismType is different. Check createEvaluationMechanism in EvaluationMechanismFactory and look at switch clause there.
		super(EvaluationMechanismTypes.LAZY_CHAIN);
		this.negationType = negationType;
	}

}
