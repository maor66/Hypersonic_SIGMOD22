package sase.adaptive.monitoring;

public class TrivialAdaptationNecessityDetector extends AdaptationNecessityDetector {

	@Override
	public boolean shouldAdapt() {
		return false;
	}

}
