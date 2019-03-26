package sase.specification.input;

import sase.input.EventStreamModifierTypes;

public class TrivialInputSpecification extends InputSpecification {

	public TrivialInputSpecification() {
		super(EventStreamModifierTypes.NONE);
	}
	@Override
	public String getShortDescription() {
		return "N/A";
	}
	
	@Override
	public String getLongDescription() {
		return "No sase.input modification is performed.";
	}

}
