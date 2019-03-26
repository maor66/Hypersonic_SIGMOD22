package sase.adaptive.monitoring.invariant.compare;

public abstract class InvariantComparer {

	public enum ComparisonType {
		LESS,
		EQUALS,
		MORE,
	}
	
	protected ComparisonType comparisonType;
	
	public InvariantComparer(ComparisonType comparisonType) {
		this.comparisonType = comparisonType;
	}
	
	protected boolean compare(double left, double right, ComparisonType comparisonType) {
		if (comparisonType == null) {
			comparisonType = this.comparisonType;
		}
		switch (comparisonType) {
			case EQUALS:
				return left == right;
			case LESS:
				return left < right;
			case MORE:
				return left > right;
			default:
				return false;
		}
	}
	
	public abstract boolean compareInvariants(double firstInvariantValue, double secondInvariantValue);
}
