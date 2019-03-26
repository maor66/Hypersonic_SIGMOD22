package sase.adaptive.monitoring.invariant.compare;

public class TrivialInvariantComparer extends InvariantComparer {

	public TrivialInvariantComparer(ComparisonType comparisonType) {
		super(comparisonType);
	}

	@Override
	public boolean compareInvariants(double firstInvariantValue, double secondInvariantValue) {
		return compare(firstInvariantValue, secondInvariantValue, null);
	}

}
