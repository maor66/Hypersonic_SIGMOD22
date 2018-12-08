package sase.specification.input;

import sase.input.EventStreamModifierTypes;

public class RarestTypeDuplicatorInputSpecification extends InputSpecification {

	public final int filteringFactor;
	public final int duplicatingFactor;
	
	public RarestTypeDuplicatorInputSpecification(int filteringFactor, int duplicatingFactor) {
		super(EventStreamModifierTypes.RAREST_TYPE);
		this.filteringFactor = filteringFactor;
		this.duplicatingFactor = duplicatingFactor;
	}
	
	@Override
	public String getShortDescription() {
		return String.format("(%s,%s)", filteringFactor, duplicatingFactor);
	}
	
	@Override
	public String getLongDescription() {
		return String.format("input filtering factor: %d, input duplicating factor: %d",
				 filteringFactor, duplicatingFactor);
	}

}
