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
import sase.order.algorithm.DynamicOrderingAlgorithm;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class SelingerTreeTopologyCreator extends DynamicOrderingAlgorithm implements ITreeTopologyCreator {

	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		List<EventType> eventTypes = pattern.getEventTypes();
		List<EventType> iterativeEventTypes = ((CompositePattern)pattern).getIterativeEventTypes();
		HashMap<List<EventType>, CostAwareTree> subsets = new HashMap<List<EventType>, CostAwareTree>();
		for (EventType eventType : eventTypes) {
			LeafNode currLeafNode = new LeafNode(eventType, mainCondition, iterativeEventTypes.contains(eventType));
			List<EventType> listForEventType = new ArrayList<EventType>();
			listForEventType.add(eventType);
			subsets.put(listForEventType, new CostAwareTree(currLeafNode, costModel.getCost(currLeafNode)));
		}
		for (int i = 2; i <= eventTypes.size(); ++i) {
			List<List<EventType>> currentSubsets = getAllSubSetsOfSize(eventTypes, i);
			for (int j = 1; j <= i/2; ++j) {
				for (List<EventType> subset : currentSubsets) {
					List<List<EventType>> subSubsets = getAllSubSetsOfSize(subset, j);
					for (List<EventType> subSubSet : subSubsets) {
						List<EventType> complement = getComplement(subset, subSubSet);
						Node firstTree = subsets.get(subSubSet).root;
						Node secondTree = subsets.get(complement).root;
						Node newTree = TopologyCreatorUtils.createNodeByPatternType(pattern, mainCondition, 
																					eventTypes, firstTree, secondTree);
						Double newCost = costModel.getCost(newTree);
						CostAwareTree oldTreeWithCost = subsets.get(subset);
						if (oldTreeWithCost == null || newCost < oldTreeWithCost.cost) {
							subsets.put(subset, new CostAwareTree(newTree, newCost));
						}
					}
				}
			}
		}
		return subsets.get(eventTypes).root;
	}
	
	private List<EventType> getComplement(List<EventType> set, List<EventType> subset) {
		List<EventType> result = new ArrayList<EventType>();
		for (EventType eventType : set) {
			if (!subset.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}

}
