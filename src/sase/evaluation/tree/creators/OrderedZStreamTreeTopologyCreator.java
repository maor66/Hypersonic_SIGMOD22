package sase.evaluation.tree.creators;

import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmFactory;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.evaluation.nfa.lazy.order.cost.ThroughputCostModel;
import sase.evaluation.nfa.lazy.order.cost.ThroughputLatencyCostModel;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.cost.ThroughputLatencyTreeCostModel;
import sase.evaluation.tree.cost.ThroughputTreeCostModel;
import sase.evaluation.tree.elements.node.Node;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class OrderedZStreamTreeTopologyCreator extends ZStreamTreeTopologyCreator implements ITreeTopologyCreator {

	private static final OrderingAlgorithmTypes orderingAlgorithmType = OrderingAlgorithmTypes.GREEDY_COST;
	
	private final IOrderingAlgorithm orderingAlgorithm;
	
	public OrderedZStreamTreeTopologyCreator() {
		orderingAlgorithm = OrderingAlgorithmFactory.createOrderingAlgorithm(orderingAlgorithmType, new Object[]{});
	}
	
	private ICostModel createOrderCostModel(ITreeCostModel treeCostModel, List<EventType> eventTypes) {
		if (treeCostModel instanceof ThroughputTreeCostModel) {
			return new ThroughputCostModel();
		}
		if (treeCostModel instanceof ThroughputLatencyTreeCostModel) {
			return new ThroughputLatencyCostModel(eventTypes, ((ThroughputLatencyTreeCostModel)treeCostModel).getRatio());
		}
		return null;
	}
	
	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		ICostModel orderCostModel = createOrderCostModel(costModel, pattern.getEventTypes());
		List<EventType> calculatedEventOrder = orderingAlgorithm.calculateEvaluationOrder(pattern, orderCostModel);
		return createTreeTopologyByLeavesOrder(pattern, pattern.getEventTypes(), 
											   calculatedEventOrder, mainCondition, costModel);
	}

}
