package sase.aggregator.builtin;

import sase.aggregator.VectorAggregator;

public class AverageVectorAggregator extends VectorAggregator {

	public AverageVectorAggregator() {
		super(new AverageAggregator());
	}

}
