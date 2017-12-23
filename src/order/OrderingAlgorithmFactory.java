package sase.order;

import java.util.List;

import sase.base.EventType;
import sase.order.algorithm.DynamicOrderingAlgorithm;
import sase.order.algorithm.EventFrequencyOrderingAlgorithm;
import sase.order.algorithm.FixedOrderingAlgorithm;
import sase.order.algorithm.GreedyCostModelOrderingAlgorithm;
import sase.order.algorithm.GreedySelectivityOrderingAlgorithm;
import sase.order.algorithm.IterativeImprovementOrderingAlgorithm;
import sase.order.algorithm.RandomOrderingAlgorithm;
import sase.order.algorithm.TrivialOrderingAlgorithm;
import sase.order.algorithm.adaptive.greedy.GreedyAdaptiveOrderingAlgorithm;

public class OrderingAlgorithmFactory {

	@SuppressWarnings("unchecked")
	public static IOrderingAlgorithm createOrderingAlgorithm(OrderingAlgorithmTypes algorithmType,
															 Object[] arguments) {
		switch(algorithmType) {
			case TRIVIAL:
				return new TrivialOrderingAlgorithm();
			case FIXED:
				return new FixedOrderingAlgorithm((List<EventType>)arguments[0]);
			case RANDOM:
				return new RandomOrderingAlgorithm();
			case EVENT_FREQUENCY:
				return new EventFrequencyOrderingAlgorithm();
			case GREEDY_COST:
				return new GreedyCostModelOrderingAlgorithm();
			case GREEDY_SELECTIVITY:
				return new GreedySelectivityOrderingAlgorithm();
			case II_RANDOM:
				return new IterativeImprovementOrderingAlgorithm(new RandomOrderingAlgorithm());
			case II_GREEDY_COST:
				return new IterativeImprovementOrderingAlgorithm(new GreedyCostModelOrderingAlgorithm());
			case DYNAMIC:
				return new DynamicOrderingAlgorithm();
			case GREEDY_ADAPTIVE:
				return new GreedyAdaptiveOrderingAlgorithm();
			default:
				throw new RuntimeException(String.format("Unexpected algorithm type: %s", algorithmType));
		}
	}
}
