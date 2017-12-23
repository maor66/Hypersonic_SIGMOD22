package evaluation.tree.creators;

import java.util.List;

import base.EventType;
import evaluation.tree.ITreeCostModel;
import evaluation.tree.ITreeTopologyCreator;
import evaluation.tree.cost.ThroughputLatencyTreeCostModel;
import evaluation.tree.cost.ThroughputTreeCostModel;
import evaluation.tree.elements.Node;
import order.IOrderingAlgorithm;
import order.OrderingAlgorithmFactory;
import order.OrderingAlgorithmTypes;
import order.cost.ICostModel;
import order.cost.ThroughputCostModel;
import order.cost.ThroughputLatencyCostModel;
import pattern.Pattern;
import pattern.condition.base.CNFCondition;

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
		return createTreeTopologyFromEventList(calculatedEventOrder, mainCondition, costModel);
	}

}
