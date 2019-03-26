package sase.multi.sla;

import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.pattern.CompositePattern;

public interface ISlaVerifier {

	public boolean verifySlaRequirements(CompositePattern pattern, MultiPlan globalPlan, IAlgoUnit algoUnit);
	
}
