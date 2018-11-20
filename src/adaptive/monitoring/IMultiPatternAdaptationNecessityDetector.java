package sase.adaptive.monitoring;

import java.util.Set;

import sase.pattern.CompositePattern;

public interface IMultiPatternAdaptationNecessityDetector extends IAdaptationNecessityDetector {

	public Set<CompositePattern> getAffectedPatterns();
}
