package sase.aggregator;

import java.util.ArrayList;
import java.util.List;

import sase.simulator.Environment;
import sase.statistics.Statistics;

public class VectorAggregator {

	private final ScalarAggregator aggregator;
	
	public VectorAggregator(ScalarAggregator aggregator) {
		this.aggregator = aggregator;
	}
	
	public Object[] aggregateVectors(List<Object[]> vectors) {
		return aggregateVectors(vectors, 0);
	}
	
	protected Object[] aggregateVectors(List<Object[]> vectors, int startIndex) {
		return aggregateVectors(vectors, startIndex, vectors.isEmpty() ? 0 : vectors.get(0).length);
	}
	
	protected Object[] aggregateVectors(List<Object[]> vectors, int startIndex, int endIndex) {
		if (vectors.isEmpty())
			return null;
		int arrayLength = vectors.get(0).length;
		for (int i = 1; i < vectors.size(); ++i) {
			if (vectors.get(i).length != arrayLength) {
				throw new RuntimeException("Events with illegal payload encountered");
			}
		}
		int aggregatedArraySize = endIndex - startIndex;
		Object[] aggregated = new Object[aggregatedArraySize];
		for (int i = 0; i < aggregatedArraySize; ++i) {
			List<Object> currObjectsList = new ArrayList<Object>();
			for (Object[] item : vectors) {
				currObjectsList.add(item[startIndex + i]);
			}
			aggregated[i] = aggregator.aggregate(currObjectsList);
		}
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.computations);
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.aggregationComputations);
		return aggregated;
	}

}
