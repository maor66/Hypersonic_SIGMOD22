package sase.evaluation.tree.creators.adaptive.zstream;


import sase.adaptive.monitoring.invariant.InvariantInput;
import sase.base.EventType;
import sase.pattern.condition.base.CNFCondition;

public class TreeInvariantInput extends InvariantInput {

	public EventType leftLeafType;
	public EventType rightLeafType;
	public TreeInvariantInput leftPairOfLeaves;
	public TreeInvariantInput rightPairOfLeaves;
	public CNFCondition conditions;

	public Double leftSubTreeCardinality;
	public Double rightSubTreeCardinality;
	public Double leftSubTreeCost;
	public Double rightSubTreeCost;
	
	public TreeInvariantInput(EventType leftLeafType, EventType rightLeafType,
			  				  TreeInvariantInput leftPairOfLeaves, TreeInvariantInput rightPairOfLeaves,
							  Double leftSubTreeCardinality, Double rightSubTreeCardinality,
							  Double leftSubTreeCost, Double rightSubTreeCost,
							  CNFCondition conditions) {
		if ((leftLeafType != null && leftSubTreeCardinality != null) ||
			(leftPairOfLeaves != null && leftSubTreeCardinality != null) ||
			(leftLeafType != null && leftPairOfLeaves != null)) {
				throw new RuntimeException("Left subtree cannot be both a leaf and an internal node.");
		}
		if ((rightLeafType != null && rightSubTreeCardinality != null) ||
			(rightPairOfLeaves != null && rightSubTreeCardinality != null) ||
			(rightLeafType != null && rightPairOfLeaves != null)) {
				throw new RuntimeException("Right subtree cannot be both a leaf and an internal node.");
		}
		this.leftLeafType = leftLeafType;
		this.rightLeafType = rightLeafType;
		this.leftPairOfLeaves = leftPairOfLeaves;
		this.rightPairOfLeaves = rightPairOfLeaves;
		this.leftSubTreeCardinality = leftSubTreeCardinality;
		this.rightSubTreeCardinality = rightSubTreeCardinality;
		this.leftSubTreeCost = leftSubTreeCost;
		this.rightSubTreeCost = rightSubTreeCost;
		this.conditions = conditions;
	}
}
