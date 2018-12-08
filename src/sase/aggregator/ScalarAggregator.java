package sase.aggregator;

import java.util.List;

public abstract class ScalarAggregator {

	public abstract Object aggregate(List<Object> items);

}
