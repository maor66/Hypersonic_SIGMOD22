package sase.multi.sla;

import sase.pattern.CompositePattern;

public class SlaVerifierFactory {

	public static ISlaVerifier createSlaVerifier(SlaVerifierTypes type) {
		switch (type) {
			case BEST_PLAN:
				return new BestPlanSlaVerifier();
			case NONE:
				return null;
			default:
				throw new RuntimeException("Illegal SLA verifier type");
		}
	}
	
	public static SlaAwarePattern createSlaAwarePattern(CompositePattern sourcePattern, SlaVerifierTypes verifierType) {
		return new SlaAwarePattern(sourcePattern, createSlaVerifier(verifierType));
	}
}
