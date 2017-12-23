package user.stocks;

import java.util.List;

import aggregator.builtin.AverageVectorAggregator;
import base.Event;

public class AverageStocksVectorAggregator extends AverageVectorAggregator {

	@Override
	public Object[] aggregateVectors(List<Object[]> vectors) {
		if (vectors.isEmpty())
			return null;
		
		Object[] signature = Event.getEventSignature(vectors.get(0));
		Object[] aggregatedNumbers = super.aggregateVectors(vectors, 2);
		
		Object[] result = new Object[vectors.get(0).length];
		int i = 0;
		for(; i < signature.length; ++i) {
			result[i] = signature[i];
		}
		for(; i < result.length; ++i) {
			result[i] = aggregatedNumbers[i - signature.length];
		}
		return result;
	}
}
