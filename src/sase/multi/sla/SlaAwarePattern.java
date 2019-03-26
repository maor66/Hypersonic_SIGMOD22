package sase.multi.sla;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public class SlaAwarePattern extends CompositePattern {

	private ISlaVerifier slaVerifier;
	
	public SlaAwarePattern(CompositePattern source, ISlaVerifier slaVerifier) {
		super(source.getType(), source.getNestedPatterns(), source.getCondition(), source.getTimeWindow());
		this.slaVerifier = slaVerifier;
	}
	
	public boolean verifySlaRequirements(MultiPlan globalPlan, IAlgoUnit algoUnit) {
		return slaVerifier.verifySlaRequirements(this, globalPlan, algoUnit);
	}

}
