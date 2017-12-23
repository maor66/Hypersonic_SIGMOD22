package aggregator.builtin;

import java.util.List;

import aggregator.ScalarAggregator;

public class AverageAggregator extends ScalarAggregator {

	@Override
	public Object aggregate(List<Object> items) {
		Double sum = 0.0;
		for (Object item : items) {
			Double number = (Double)item;
			sum += number;
		}
		return sum / items.size();
	}
	
}
