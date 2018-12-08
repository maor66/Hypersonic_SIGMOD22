package sase.specification.adaptation;

import sase.adaptive.monitoring.AdaptationNecessityDetectorTypes;

public class TrivialAdaptationSpecification extends AdaptationSpecification {

	public TrivialAdaptationSpecification() {
		super(AdaptationNecessityDetectorTypes.NONE, null, null);
	}

	@Override
	protected String getInternalShortDescription() {
		return "";
	}

	@Override
	protected String getInternalLongDescription() {
		return "";
	}
}
