package sase.specification.input;

import sase.input.EventStreamModifierTypes;

public abstract class InputSpecification {

	public final EventStreamModifierTypes type;
	
	public InputSpecification(EventStreamModifierTypes type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return getLongDescription();
	}

	public abstract String getShortDescription();
	public abstract String getLongDescription();	
}
