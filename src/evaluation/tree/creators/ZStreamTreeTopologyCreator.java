package evaluation.tree.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.EventType;
import evaluation.tree.ITreeCostModel;
import evaluation.tree.ITreeTopologyCreator;
import evaluation.tree.elements.InternalNode;
import evaluation.tree.elements.LeafNode;
import evaluation.tree.elements.Node;
import pattern.Pattern;
import pattern.condition.base.CNFCondition;

public class ZStreamTreeTopologyCreator implements ITreeTopologyCreator {

	protected Node createTreeTopologyFromEventList(List<EventType> eventTypes,
												   CNFCondition mainCondition,
												   ITreeCostModel costModel) {
		HashMap<List<EventType>, CostAwareTree> subsets = new HashMap<List<EventType>, CostAwareTree>();
		for (EventType eventType : eventTypes) {
			LeafNode currLeafNode = new LeafNode(eventType, mainCondition);
			List<EventType> listForEventType = new ArrayList<EventType>();
			listForEventType.add(eventType);
			subsets.put(listForEventType, new CostAwareTree(currLeafNode, costModel.getCost(currLeafNode)));
		}
		for (int i = 2; i <= eventTypes.size(); ++i) {
			for (int j = 0; j <= eventTypes.size() - i; ++j) {
				for (int k = j + 1; k <= j + i - 1; ++k) {
					List<EventType> eventsForMainTree = eventTypes.subList(j, j + i);
					List<EventType> eventsForLeftSubTree = eventTypes.subList(j, k);
					List<EventType> eventsForRightSubTree = eventTypes.subList(k, j + i);
					Node leftTree = subsets.get(eventsForLeftSubTree).root;
					Node rightTree = subsets.get(eventsForRightSubTree).root;
					Node newTree = new InternalNode(mainCondition, eventTypes, leftTree, rightTree);
					Double newCost = costModel.getCost(newTree);
					CostAwareTree oldTreeWithCost = subsets.get(eventsForMainTree);
					if (oldTreeWithCost == null || newCost < oldTreeWithCost.cost) {
						subsets.put(eventsForMainTree, new CostAwareTree(newTree, newCost));
					}
				}
			}
		}
		return subsets.get(eventTypes).root;
		
	}
	
	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		return createTreeTopologyFromEventList(pattern.getEventTypes(), mainCondition, costModel);
	}

}
