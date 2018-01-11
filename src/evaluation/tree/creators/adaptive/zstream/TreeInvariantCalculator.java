package sase.evaluation.tree.creators.adaptive.zstream;

import sase.adaptive.estimation.IEventArrivalRateEstimator;
import sase.adaptive.monitoring.invariant.IInvariantCalculator;
import sase.adaptive.monitoring.invariant.InvariantInput;
import sase.simulator.Environment;

//TODO:I'm so sorry for the completely unjustified code duplication in this class..
public class TreeInvariantCalculator implements IInvariantCalculator {

	@Override
	public double calculateInvariantValue(InvariantInput input) {
		TreeInvariantInput treeInvariantInput = (TreeInvariantInput)input;
		double leftSubTreeCardinality = calculateLeftSubTreeCardinality(treeInvariantInput);
		double leftSubTreeCost = calculateLeftSubTreeCost(treeInvariantInput);
		double rightSubTreeCardinality = calculateRightSubTreeCardinality(treeInvariantInput);
		double rightSubTreeCost = calculateRightSubTreeCost(treeInvariantInput);
		Double conditionSelectivity = treeInvariantInput.conditions == null ? 
									  1.0 : treeInvariantInput.conditions.getSelectivity();
		return leftSubTreeCost + rightSubTreeCost + (leftSubTreeCardinality * rightSubTreeCardinality * conditionSelectivity);
	}
	
	private double calculateLeftSubTreeCardinality(TreeInvariantInput treeInvariantInput) {
		if (treeInvariantInput.leftLeafType != null) {
			return Environment.getEnvironment().getEventRateEstimator().getEventRateEstimate(treeInvariantInput.leftLeafType);
		}
		if (treeInvariantInput.leftPairOfLeaves != null) {
			IEventArrivalRateEstimator eventRateEstimator = Environment.getEnvironment().getEventRateEstimator();
			int leftRate = eventRateEstimator.getEventRateEstimate(treeInvariantInput.leftPairOfLeaves.leftLeafType);
			int rightRate = eventRateEstimator.getEventRateEstimate(treeInvariantInput.leftPairOfLeaves.rightLeafType);
			return leftRate * rightRate * treeInvariantInput.leftPairOfLeaves.conditions.getSelectivity();
		}
		return treeInvariantInput.leftSubTreeCardinality;
	}
	
	private double calculateLeftSubTreeCost(TreeInvariantInput treeInvariantInput) {
		if (treeInvariantInput.leftLeafType != null) {
			return calculateLeftSubTreeCardinality(treeInvariantInput);
		}
		if (treeInvariantInput.leftPairOfLeaves != null) {
			return calculateInvariantValue(treeInvariantInput.leftPairOfLeaves);
		}
		return treeInvariantInput.leftSubTreeCost;
	}
	
	private double calculateRightSubTreeCardinality(TreeInvariantInput treeInvariantInput) {
		if (treeInvariantInput.rightLeafType != null) {
			return Environment.getEnvironment().getEventRateEstimator().getEventRateEstimate(treeInvariantInput.rightLeafType);
		}
		if (treeInvariantInput.rightPairOfLeaves != null) {
			IEventArrivalRateEstimator eventRateEstimator = Environment.getEnvironment().getEventRateEstimator();
			int leftRate = eventRateEstimator.getEventRateEstimate(treeInvariantInput.rightPairOfLeaves.leftLeafType);
			int rightRate = eventRateEstimator.getEventRateEstimate(treeInvariantInput.rightPairOfLeaves.rightLeafType);
			return leftRate * rightRate * treeInvariantInput.rightPairOfLeaves.conditions.getSelectivity();
		}
		return treeInvariantInput.rightSubTreeCardinality;
	}
	
	private double calculateRightSubTreeCost(TreeInvariantInput treeInvariantInput) {
		if (treeInvariantInput.rightLeafType != null) {
			return calculateRightSubTreeCardinality(treeInvariantInput);
		}
		if (treeInvariantInput.rightPairOfLeaves != null) {
			return calculateInvariantValue(treeInvariantInput.rightPairOfLeaves);
		}
		return treeInvariantInput.rightSubTreeCost;
	}

}
