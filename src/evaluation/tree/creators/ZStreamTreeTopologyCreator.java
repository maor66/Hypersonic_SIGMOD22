package sase.evaluation.tree.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.TopologyCreatorUtils;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class ZStreamTreeTopologyCreator implements ITreeTopologyCreator {

	protected Node createTreeTopologyByLeavesOrder(Pattern pattern, 
												   List<EventType> originalEventOrder,
												   List<EventType> leavesOrder,
												   CNFCondition mainCondition,
												   ITreeCostModel costModel) {
		List<EventType> iterativeEventTypes = ((CompositePattern)pattern).getIterativeEventTypes();
		HashMap<List<EventType>, CostAwareTree> subsets = new HashMap<List<EventType>, CostAwareTree>();
		for (EventType eventType : leavesOrder) {
			LeafNode currLeafNode = new LeafNode(eventType, mainCondition, iterativeEventTypes.contains(eventType));
			List<EventType> listForEventType = new ArrayList<EventType>();
			listForEventType.add(eventType);
			subsets.put(listForEventType, new CostAwareTree(currLeafNode, costModel.getCost(currLeafNode)));
		}
		for (int i = 2; i <= leavesOrder.size(); ++i) {
			for (int j = 0; j <= leavesOrder.size() - i; ++j) {
				for (int k = j + 1; k <= j + i - 1; ++k) {
					List<EventType> eventsForMainTree = leavesOrder.subList(j, j + i);
					List<EventType> eventsForLeftSubTree = leavesOrder.subList(j, k);
					List<EventType> eventsForRightSubTree = leavesOrder.subList(k, j + i);
					Node leftTree = subsets.get(eventsForLeftSubTree).root;
					Node rightTree = subsets.get(eventsForRightSubTree).root;
					Node newTree = TopologyCreatorUtils.createNodeByPatternType(pattern, mainCondition, 
																				originalEventOrder, leftTree, rightTree);
					Double newCost = costModel.getCost(newTree);
					CostAwareTree oldTreeWithCost = subsets.get(eventsForMainTree);
					if (oldTreeWithCost == null || newCost < oldTreeWithCost.cost) {
						subsets.put(eventsForMainTree, new CostAwareTree(newTree, newCost));
					}
				}
			}
		}
		return subsets.get(leavesOrder).root;
		
	}
	
	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		return createTreeTopologyByLeavesOrder(pattern, pattern.getEventTypes(), 
											   pattern.getEventTypes(), mainCondition, costModel);
	}

}
