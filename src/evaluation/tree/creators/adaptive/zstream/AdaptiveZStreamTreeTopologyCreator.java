package evaluation.tree.creators.adaptive.zstream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import adaptive.estimation.IEventArrivalRateEstimator;
import adaptive.monitoring.invariant.Invariant;
import adaptive.monitoring.invariant.InvariantAdaptationNecessityDetector;
import adaptive.monitoring.invariant.compare.InvariantComparer.ComparisonType;
import base.EventType;
import evaluation.tree.ITreeCostModel;
import evaluation.tree.ITreeTopologyCreator;
import evaluation.tree.TopologyCreatorUtils;
import evaluation.tree.cost.ThroughputTreeCostModel;
import evaluation.tree.creators.ZStreamTreeTopologyCreator;
import evaluation.tree.elements.node.InternalNode;
import evaluation.tree.elements.node.LeafNode;
import evaluation.tree.elements.node.Node;
import pattern.CompositePattern;
import pattern.Pattern;
import pattern.condition.base.CNFCondition;
import simulator.Environment;

public class AdaptiveZStreamTreeTopologyCreator implements ITreeTopologyCreator {
	
	private class TreeInfo implements Comparable<TreeInfo> {
		public Node tree;
		public Double cost;
		public Double cardinality;
		public Integer size;
		public Invariant invariant = null;
		
		public TreeInfo leftTreeInfo = null;
		public TreeInfo rightTreeInfo = null;
		
		public TreeInfo(Node tree, Double cost, Double cardinality, Integer size) {
			this.tree = tree;
			this.cost = cost;
			this.cardinality = cardinality;
			this.size = size;
		}

		@Override
		public int compareTo(TreeInfo o) {
			return size - o.size;
		}
	}
	
	@Override
	public Node createTreeTopology(Pattern pattern, CNFCondition mainCondition, ITreeCostModel costModel) {
		if (!isInvariantBasedAdaptationEnabled()) {
			return new ZStreamTreeTopologyCreator().createTreeTopology(pattern, mainCondition, new ThroughputTreeCostModel());
		}
		List<EventType> eventTypes = pattern.getEventTypes();
		HashMap<List<EventType>, TreeInfo> subsets = new HashMap<List<EventType>, TreeInfo>();
		IEventArrivalRateEstimator eventRateEstimator = Environment.getEnvironment().getEventRateEstimator();
		List<EventType> iterativeEventTypes = ((CompositePattern)pattern).getIterativeEventTypes();
		for (EventType eventType : eventTypes) {
			LeafNode currLeafNode = new LeafNode(eventType, mainCondition, iterativeEventTypes.contains(eventType));
			List<EventType> listForEventType = new ArrayList<EventType>();
			listForEventType.add(eventType);
			double leafCardinality = 
					eventRateEstimator.getEventRateEstimate(eventType) * currLeafNode.getNodeCondition().getSelectivity();
			subsets.put(listForEventType, new TreeInfo(currLeafNode, leafCardinality, leafCardinality, 1));
		}
		for (int i = 2; i <= eventTypes.size(); ++i) {
			for (int j = 0; j <= eventTypes.size() - i; ++j) {
				List<EventType> eventsForMainTree = eventTypes.subList(j, j + i);
				for (int k = j + 1; k <= j + i - 1; ++k) {
					processSingleCandidateTree(pattern, i, j, k, eventsForMainTree, eventTypes, mainCondition, subsets);
				}
			}
		}
		TreeInfo finalTree = subsets.get(eventTypes);
		InvariantAdaptationNecessityDetector detector = 
				(InvariantAdaptationNecessityDetector)Environment.getEnvironment().getAdaptationNecessityDetector();
		detector.setInvariants(getInvariants(finalTree));
		return finalTree.tree;
	}
	
	private boolean isInvariantBasedAdaptationEnabled() {
		return (Environment.getEnvironment().getAdaptationNecessityDetector() instanceof InvariantAdaptationNecessityDetector);
	}
	
	private void processSingleCandidateTree(Pattern pattern,
											int i, int j, int k,
											List<EventType> eventsForMainTree,
											List<EventType> eventTypes, CNFCondition mainCondition,
											HashMap<List<EventType>, TreeInfo> subsets) {
		List<EventType> eventsForLeftSubTree = eventTypes.subList(j, k);
		List<EventType> eventsForRightSubTree = eventTypes.subList(k, j + i);
		
		TreeInfo leftTreeInfo = subsets.get(eventsForLeftSubTree);
		TreeInfo rightTreeInfo = subsets.get(eventsForRightSubTree);
		Node newTree = TopologyCreatorUtils.createNodeByPatternType(pattern, mainCondition, 
																	eventTypes, leftTreeInfo.tree, rightTreeInfo.tree);
		CNFCondition newCondition = newTree.getNodeCondition();
		Double newCardinality =
					leftTreeInfo.cardinality * rightTreeInfo.cardinality * newCondition.getSelectivity();
		Double newCost = leftTreeInfo.cost + rightTreeInfo.cost + newCardinality;
		Integer newSize = leftTreeInfo.size + leftTreeInfo.size + 1;
		TreeInfo newTreeInfo = new TreeInfo(newTree, newCost, newCardinality, newSize);
		newTreeInfo.leftTreeInfo = leftTreeInfo;
		newTreeInfo.rightTreeInfo = rightTreeInfo;
		
		TreeInfo oldTreeInfo = subsets.get(eventsForMainTree);
		if (oldTreeInfo == null) {
			subsets.put(eventsForMainTree, newTreeInfo);
			return;
		}
		if (newCost < oldTreeInfo.cost) {
			createOrImproveInvariant(newTreeInfo, oldTreeInfo);
			subsets.put(eventsForMainTree, newTreeInfo);
		}
		else {
			createOrImproveInvariant(oldTreeInfo, newTreeInfo);
		}
	}
	
	private void createOrImproveInvariant(TreeInfo goodTreeInfo, TreeInfo badTreeInfo) {
		TreeInvariantInput goodTreeInvariantInput = createTreeInvariantInput(goodTreeInfo);
		TreeInvariantInput badTreeInvariantInput = createTreeInvariantInput(badTreeInfo);
		InvariantAdaptationNecessityDetector detector = 
				(InvariantAdaptationNecessityDetector)Environment.getEnvironment().getAdaptationNecessityDetector();
		Invariant newInvariant = 
				new Invariant(goodTreeInvariantInput, badTreeInvariantInput,
							  new TreeInvariantCalculator(),
							  detector.getComparerFactory().createInvariantComparer(ComparisonType.LESS));
		if (goodTreeInfo.invariant == null || 
			newInvariant.getValueDistance() < goodTreeInfo.invariant.getValueDistance()) {
				goodTreeInfo.invariant = newInvariant;
		}
	}
	
	private TreeInvariantInput createTreeInvariantInput(TreeInfo treeInfo) {
		EventType leftLeafType = null, rightLeafType = null;
		TreeInvariantInput leftPairOfLeaves = null, rightPairOfLeaves = null;
		Double leftSubTreeCardinality = null, leftSubTreeCost = null, rightSubTreeCardinality = null, rightSubTreeCost = null;
		CNFCondition conditions = treeInfo.tree.getNodeCondition();
		
		InternalNode root = (InternalNode)treeInfo.tree;
		Node leftChild = root.getLeftChild();
		if (leftChild instanceof LeafNode) {
			leftLeafType = ((LeafNode)leftChild).getEventType();
		}
		else {
			InternalNode leftSubTree = (InternalNode)leftChild;
			if ((leftSubTree.getLeftChild() instanceof LeafNode) && (leftSubTree.getRightChild() instanceof LeafNode)) {
				leftPairOfLeaves = createTreeInvariantInput(treeInfo.leftTreeInfo);
			}
			else {
				leftSubTreeCardinality = treeInfo.leftTreeInfo.cardinality;
				leftSubTreeCost = treeInfo.leftTreeInfo.cost;
			}
		}
		Node rightChild = root.getRightChild();
		if (rightChild instanceof LeafNode) {
			rightLeafType = ((LeafNode)rightChild).getEventType();
		}
		else {
			InternalNode rightSubTree = (InternalNode)rightChild;
			if ((rightSubTree.getLeftChild() instanceof LeafNode) && (rightSubTree.getRightChild() instanceof LeafNode)) {
				rightPairOfLeaves = createTreeInvariantInput(treeInfo.rightTreeInfo);
			}
			else {
				rightSubTreeCardinality = treeInfo.rightTreeInfo.cardinality;
				rightSubTreeCost = treeInfo.rightTreeInfo.cost;
			}
		}
		
		return new TreeInvariantInput(leftLeafType, rightLeafType, leftPairOfLeaves, rightPairOfLeaves,
									  leftSubTreeCardinality, rightSubTreeCardinality, leftSubTreeCost, rightSubTreeCost,
									  conditions);
	}

	private List<Invariant> getInvariants(TreeInfo rootTreeInfo) {
		List<Invariant> result = new ArrayList<Invariant>();
		List<TreeInfo> treeInfos = new ArrayList<TreeInfo>();
		collectTreeInfos(rootTreeInfo, treeInfos);
		Collections.sort(treeInfos);
		for (TreeInfo treeInfo : treeInfos) {
			Invariant invariant = treeInfo.invariant;
			if (invariant != null) {
				result.add(invariant);
			}
		}
		return result;
	}
	
	private void collectTreeInfos(TreeInfo treeInfo, List<TreeInfo> treeInfos) {
		if (treeInfo == null) {
			return;
		}
		collectTreeInfos(treeInfo.leftTreeInfo, treeInfos);
		collectTreeInfos(treeInfo.rightTreeInfo, treeInfos);
		treeInfos.add(treeInfo);
	}

}
