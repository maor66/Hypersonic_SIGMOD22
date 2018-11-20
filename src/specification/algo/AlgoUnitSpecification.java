package sase.specification.algo;

import sase.multi.algo.AlgoUnitTypes;

public class AlgoUnitSpecification {

	public AlgoUnitTypes type;
	
	public AlgoUnitSpecification(AlgoUnitTypes type) {
		this.type = type;
	}
	
	public String getShortDescription() {
		return type.toString();
	}
	
	public String getLongDescription() {
		return type.toString();
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}
	
}
