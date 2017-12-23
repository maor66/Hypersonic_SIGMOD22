package adaptive.monitoring.invariant;

import adaptive.monitoring.invariant.compare.InvariantComparer;

public class Invariant {

	private InvariantInput leftInput;
	private double leftValue;
	private InvariantInput rightInput;
	private double rightValue;
	private IInvariantCalculator calculator;
	private InvariantComparer comparer;
	
	public Invariant(InvariantInput leftInput, InvariantInput rightInput, IInvariantCalculator calculator,
					 InvariantComparer comparer) {
		this.leftInput = leftInput;
		this.rightInput = rightInput;
		this.calculator = calculator;
		this.comparer = comparer;
		
		leftValue = calculator.calculateInvariantValue(leftInput);
		rightValue = calculator.calculateInvariantValue(rightInput);
	}
	
	public boolean verify() {
		double currentLeftValue = calculator.calculateInvariantValue(leftInput);
		double currentRightValue = calculator.calculateInvariantValue(rightInput);
		return comparer.compareInvariants(currentLeftValue, currentRightValue);
	}
	
	public double getValueDistance() {
		return Math.abs(leftValue - rightValue);
	}

	public InvariantInput getLeftInput() {
		return leftInput;
	}

	public InvariantInput getRightInput() {
		return rightInput;
	}
}
