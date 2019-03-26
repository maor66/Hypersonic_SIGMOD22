package sase.adaptive.monitoring.invariant;

import java.util.ArrayList;
import java.util.List;

import sase.adaptive.monitoring.IAdaptationNecessityDetector;
import sase.adaptive.monitoring.invariant.compare.InvariantComparerFactory;
import sase.adaptive.monitoring.invariant.compare.InvariantComparerType;

public class InvariantAdaptationNecessityDetector implements IAdaptationNecessityDetector {

	private List<Invariant> invariants;
	private final InvariantComparerFactory comparerFactory;
	
	public InvariantAdaptationNecessityDetector(InvariantComparerType comparerType, Double minimalDistance) {
		invariants = new ArrayList<Invariant>();
		this.comparerFactory = new InvariantComparerFactory(comparerType, minimalDistance);
	}
	
	@Override
	public boolean shouldAdapt() {
		for (Invariant invariant : invariants) {
			if (invariant.verify()) {
				continue;
			}
			return true;
		}
		return false;
	}

	public InvariantComparerFactory getComparerFactory() {
		return comparerFactory;
	}
	
	public void setInvariants(List<Invariant> invariants) {
		this.invariants = new ArrayList<Invariant>(invariants);
	}

}
