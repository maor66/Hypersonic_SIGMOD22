package sase.specification.input;

import sase.input.EventStreamModifierTypes;

public class ShuffleEventTypesInputSpecification extends InputSpecification {

	public final Integer reShufflingPeriod;
	
	public ShuffleEventTypesInputSpecification(Integer reShufflingPeriod) {
		super(EventStreamModifierTypes.TYPE_SHUFFLE);
		this.reShufflingPeriod = reShufflingPeriod;
	}

	@Override
	public String getShortDescription() {
		return "shuffle";
	}

	@Override
	public String getLongDescription() {
		return String.format("Arbitrary event type shuffling (map changes once per %d events)", reShufflingPeriod);
	}

}
