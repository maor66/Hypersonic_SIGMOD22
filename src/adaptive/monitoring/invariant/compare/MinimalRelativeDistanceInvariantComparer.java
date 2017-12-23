package adaptive.monitoring.invariant.compare;

public class MinimalRelativeDistanceInvariantComparer extends InvariantComparer {

	private double minimalRelativeDistance;
	
	public MinimalRelativeDistanceInvariantComparer(ComparisonType comparisonType, double minimalRelativeDistance) {
		super(comparisonType);
		this.minimalRelativeDistance = minimalRelativeDistance;
	}

	@Override
	public boolean compareInvariants(double firstInvariantValue, double secondInvariantValue) {
		boolean trivialComparisonResult = compare(firstInvariantValue, secondInvariantValue, null);
		if (trivialComparisonResult) {
			return true;
		}
		Double ratio = firstInvariantValue / secondInvariantValue;
		if (ratio < 1 && ratio != 0) {
			ratio = 1 / ratio;
		}
		Double relativeDifference = ratio - 1;
		return compare(relativeDifference, minimalRelativeDistance, ComparisonType.LESS);
	}

}
