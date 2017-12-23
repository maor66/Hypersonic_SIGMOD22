package sase.adaptive.monitoring.invariant.compare;

import sase.adaptive.monitoring.invariant.compare.InvariantComparer.ComparisonType;

public class InvariantComparerFactory {

	private InvariantComparerType invariantComparerType;
	private Double minimalRelativeDistance;
	
	public InvariantComparerFactory(InvariantComparerType invariantComparerType, Double minimalRelativeDistance) {
		this.invariantComparerType = invariantComparerType;
		this.minimalRelativeDistance = minimalRelativeDistance;
	}
	
	public InvariantComparer createInvariantComparer(ComparisonType comparisonType) {
		switch (invariantComparerType) {
			case TRIVIAL:
				return new TrivialInvariantComparer(comparisonType);
			case MINIMAL_DISTANCE:
				return new MinimalRelativeDistanceInvariantComparer(comparisonType, minimalRelativeDistance);
			default:
				return null;
		}
	}
}
