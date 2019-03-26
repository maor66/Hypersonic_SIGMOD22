package sase.evaluation.tree.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.algorithm.DynamicOrderingAlgorithm;
import sase.evaluation.tree.IIncrementalTreeTopologyCreator;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.TopologyCreatorUtils;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;

public class SelingerTreeTopologyCreator extends DynamicOrderingAlgorithm implements IIncrementalTreeTopologyCreator {

	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		return createTreeTopology(pattern, mainCondition, costModel, null);
	}
	
	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, 
								   ITreeCostModel costModel, List<Node> subtrees) {
		HashMap<Set<EventType>, CostAwareTree> existingSubtreesMap = 
				subtrees == null ? null : preprocessSubtrees(subtrees, costModel);
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
					if (existingSubtreesMap != null) {
						Set<EventType> typesInSubset = new HashSet<EventType>(subset);
						CostAwareTree existingSubTree = existingSubtreesMap.get(typesInSubset);
						if (existingSubTree != null) {
							subsets.put(subset, existingSubTree);
							continue;
						}
						if (isConflictingSubset(typesInSubset, existingSubtreesMap)) {
							continue;
						}
					}
					List<List<EventType>> subSubsets = getAllSubSetsOfSize(subset, j);
					for (List<EventType> subSubSet : subSubsets) {
						List<EventType> complement = getComplement(subset, subSubSet);
						CostAwareTree firstTree = subsets.get(subSubSet);
						CostAwareTree secondTree = subsets.get(complement);
						if (firstTree == null || secondTree == null) {
							continue;
						}
						Node newTree = TopologyCreatorUtils.createNodeByPatternType(pattern, mainCondition,	eventTypes,
																					firstTree.root, secondTree.root);
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
	
	private HashMap<Set<EventType>, CostAwareTree> preprocessSubtrees(List<Node> subtrees, ITreeCostModel costModel) {
		HashMap<Set<EventType>, CostAwareTree> costAwareSubtrees = new HashMap<Set<EventType>, CostAwareTree>();
		for (Node subtree : subtrees) {
			Set<EventType> eventTypeSubset = new HashSet<EventType>(subtree.getEventTypes());
			CostAwareTree currentCostAwareTree = new CostAwareTree(subtree.cloneNode(), costModel.getCost(subtree));
			costAwareSubtrees.put(eventTypeSubset, currentCostAwareTree);
		}
		return costAwareSubtrees;
	}
	
	private boolean isConflictingSubset(Set<EventType> subset, HashMap<Set<EventType>, CostAwareTree> existingSubtreesMap) {
		for (Set<EventType> subtreeEventTypes : existingSubtreesMap.keySet()) {
			if (intersectWithoutContainment(subtreeEventTypes, subset)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean intersectWithoutContainment(Set<EventType> firstSet, Set<EventType> secondSet) {
		Set<EventType> intersection = new HashSet<EventType>(firstSet);
		intersection.retainAll(secondSet);
		return !intersection.isEmpty() && intersection.size() < firstSet.size();
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
