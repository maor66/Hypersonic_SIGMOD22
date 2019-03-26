package sase.adaptive.monitoring;

public class TrivialAdaptationNecessityDetector implements IAdaptationNecessityDetector {

	@Override
	public boolean shouldAdapt() {
		return false;
	}

}
