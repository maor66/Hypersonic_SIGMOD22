package sase.evaluation.tree;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.EvaluationPlan;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.nfa.lazy.elements.EfficientInputBuffer;
import sase.evaluation.tree.elements.TreeInstance;
import sase.evaluation.tree.elements.TreeInstanceStorage;
import sase.evaluation.tree.elements.node.InternalNode;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.NSeqInternalNode;
import sase.evaluation.tree.elements.node.Node;
import sase.evaluation.tree.elements.node.SeqInternalNode;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorType;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class TreeEvaluationMechanism implements IEvaluationMechanism, IEvaluationMechanismInfo {
	
	private Node root;
	private HashMap<EventType, LeafNode> eventTypeToLeafMap;
	private TreeInstanceStorage storage;
	private final long timeWindow;
	private EfficientInputBuffer negativeBuffer;
	private final List<EventType> iterativeTypes;
	
	public TreeEvaluationMechanism(Pattern pattern, EvaluationPlan evaluationPlan) {
		timeWindow = pattern.getTimeWindow();
		root = evaluationPlan.getTreeRepresentation();
		Environment.getEnvironment().getStatisticsManager().replaceDiscreteStatistic(Statistics.treeNodesNumber,
																					 root.getNodesInSubTree().size());
		addNSeqNodes(pattern);
		eventTypeToLeafMap = new HashMap<EventType, LeafNode>();
		List<LeafNode> leaves = root.getLeavesInSubTree();
		for (LeafNode currLeafNode : leaves) {
			eventTypeToLeafMap.put(currLeafNode.getEventType(), currLeafNode);
		}
		storage = new TreeInstanceStorage(root);
		negativeBuffer = new EfficientInputBuffer(pattern, true);
		iterativeTypes = ((CompositePattern)pattern).getIterativeEventTypes();
	}

	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		if (negativeBuffer.hasTypeBuffer(event.getType())) {
			//a negative event
			negativeBuffer.store(event);
			return null;
		}
		LeafNode leafForEvent = eventTypeToLeafMap.get(event.getType());
		if (leafForEvent == null) {
			// irrelevant event
			return null;
		}
		TreeInstance leafInstance = new TreeInstance(this, leafForEvent);
		leafInstance.addEvent(event);
		if (!leafInstance.validateNodeCondition()) {
			return storage.getMatches();
		}
		if (isIterativeInstance(leafInstance)) {
			if (canStartInstance) {
				storage.addInstance(leafInstance);//TODO: will not work properly for patterns with unbounded iterated events
			}
		}
		else {
			activateTreeProcessing(event, canStartInstance, leafInstance);
		}
		Environment.getEnvironment().getStatisticsManager().updateDiscreteIfBigger(Statistics.peakInstances,
																				   storage.getInstancesNumber());
		return storage.getMatches();
	}

	private void activateTreeProcessing(Event event, boolean canStartInstance, TreeInstance leafInstance) {
		Queue<TreeInstance> instanceQueue = new ArrayDeque<TreeInstance>();
		instanceQueue.add(leafInstance);
		Long startTimestamp = System.currentTimeMillis();
		while (!instanceQueue.isEmpty()) {
			if (Environment.getEnvironment().isTimeoutReached(System.currentTimeMillis() - startTimestamp)) {
				return;
			}
			TreeInstance currentInstance = instanceQueue.remove();
			boolean isNewlyComposedInstance = (currentInstance != leafInstance);
			if (canStartInstance || isNewlyComposedInstance) {
				storage.addInstance(currentInstance);
			}
			if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY && isNewlyComposedInstance) {
				break;
			}
			Node peerNode = currentInstance.getCurrentNode().getPeer();
			if (peerNode == null) { //i.e. if this node is the root
				continue;
			}
			List<TreeInstance> peerInstances = storage.getInstancesForNode(peerNode);
			if (peerInstances == null || peerInstances.isEmpty()) {
				continue;
			}
			List<TreeInstance> expiredInstances = new ArrayList<TreeInstance>();
			if (isIterativeInstance(peerInstances.get(0))) {
				instanceQueue.addAll(createIterativeInstances(event, peerInstances, expiredInstances));
			}
			else {
				for (TreeInstance peerInstance : peerInstances) {
					if (Environment.getEnvironment().isTimeoutReached(System.currentTimeMillis() - startTimestamp)) {
						return;
					}
					if (peerInstance.isExpired(event.getTimestamp())) {
						expiredInstances.add(peerInstance);
						continue;
					}
					TreeInstance parentInstance = currentInstance.createParentInstance(peerInstance);
					if (parentInstance.validateNodeCondition()) {
						instanceQueue.add(parentInstance);
					}
				}
			}
			peerInstances.removeAll(expiredInstances);
		}
	}

	private List<TreeInstance> createIterativeInstances(Event event, 
														List<TreeInstance> leafInstances,
														List<TreeInstance> expiredInstances) {
		List<TreeInstance> iterativeInstances = new ArrayList<TreeInstance>();
		for (TreeInstance leafInstance : leafInstances) {
			if (leafInstance.isExpired(event.getTimestamp())) {
				expiredInstances.add(leafInstance);
				continue;
			}
			Event currentEvent = leafInstance.getEvents().get(0);
			List<TreeInstance> newIterativeInstances = new ArrayList<TreeInstance>();
			newIterativeInstances.add(leafInstance);
			for (TreeInstance treeInstance : iterativeInstances) {
				TreeInstance newInstance = treeInstance.createExtendedAggregatedLeafInstance(currentEvent);
				if (newInstance.validateNodeCondition()) {
					newIterativeInstances.add(newInstance);
				}
			}
			iterativeInstances.addAll(newIterativeInstances);
		}
		return iterativeInstances;
		
	}

	private boolean isIterativeInstance(TreeInstance treeInstance) {
		return (treeInstance.getCurrentNode() instanceof LeafNode) && 
				iterativeTypes.contains(((LeafNode)treeInstance.getCurrentNode()).getEventType());
	}
	
	private void addNSeqNodes(Pattern pattern) {
		List<EventType> negativeTypes = ((CompositePattern)pattern).getNegativeEventTypes();
		for (EventType negativeEventType : negativeTypes) {
			transformNodeToNSeq(locateNodeToTransformToNSeq(pattern, negativeEventType), 
								negativeEventType, pattern.getEventTypes());
		}
	}
	
	private void transformNodeToNSeq(InternalNode node, EventType negativeEventType, List<EventType> fullOrder) {
		List<EventType> order = (node instanceof SeqInternalNode) ? ((SeqInternalNode)node).getOrder() : null;
		NSeqInternalNode negativeNode = new NSeqInternalNode(node.getMainCondition(), order, fullOrder,
															 node.getLeftChild(), node.getRightChild(), negativeEventType);
		Node parent = node.getParent();
		if (parent == null) { //should replace the root
			root = negativeNode;
			return;
		}
		negativeNode.setParent(parent);
		InternalNode parentInternalNode = (InternalNode)parent;
		if (node == parentInternalNode.getLeftChild()) {
			parentInternalNode.setLeftChild(negativeNode);
		}
		else if (node == parentInternalNode.getRightChild()) {
			parentInternalNode.setRightChild(negativeNode);
		}
		else {
			throw new RuntimeException("Tree structure is corrupted!");
		}
	}
	
	private InternalNode locateNodeToTransformToNSeq(Pattern pattern, EventType negativeEventType) {
		List<EventType> dependentEventTypes = getDependentEventTypes(pattern, negativeEventType);
		return locateLowestContainingNode(root, dependentEventTypes);
	}
	
	private List<EventType> getDependentEventTypes(Pattern pattern, EventType negativeEventType) {
		List<EventType> result = new ArrayList<EventType>();
		CNFCondition condition = (CNFCondition)pattern.getCondition();
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			List<EventType> currentEventTypes = atomicCondition.getEventTypes();
			if (currentEventTypes.contains(negativeEventType) && currentEventTypes.size() > 1) {
				result.addAll(currentEventTypes);
				result.remove(negativeEventType);
			}
		}
		if (pattern.getType() != PatternOperatorType.SEQ) {
			return result;
		}
		//add temporal conditions - assume no two adjacent negative events
		List<EventType> order = pattern.getEventTypes();
		int negativeTypeIndex = order.indexOf(negativeEventType);
		if (negativeTypeIndex > 0) {
			result.add(order.get(negativeTypeIndex - 1));
		}
		if (negativeTypeIndex < order.size() - 1) {
			result.add(order.get(negativeTypeIndex + 1));
		}
		return result;
	}
	
	private InternalNode locateLowestContainingNode(Node currentNode, List<EventType> dependentEventTypes) {
		if (!(currentNode instanceof InternalNode)) {
			throw new RuntimeException("Unbounded negative event type detected");
		}
		InternalNode currentInternalNode = (InternalNode)currentNode;
		if (currentInternalNode.getLeftChild().getEventTypes().containsAll(dependentEventTypes)) {
			return locateLowestContainingNode(currentInternalNode.getLeftChild(), dependentEventTypes);
		}
		if (currentInternalNode.getRightChild().getEventTypes().containsAll(dependentEventTypes)) {
			return locateLowestContainingNode(currentInternalNode.getRightChild(), dependentEventTypes);
		}
		return currentInternalNode;
	}

	public EfficientInputBuffer getNegativeBuffer() {
		return negativeBuffer;
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		List<Match> matches = storage.getMatches();
		//storage.validateTimeWindow(currentTime);
		return matches;
	}

	@Override
	public void completeCreation(Pattern pattern) {
	}

	@Override
	public List<Match> getLastMatches() {
		return storage.getMatches();
	}

	@Override
	public long size() {
		return storage.size();
	}

	public Node getRoot() {
		return root;
	}

	public long getTimeWindow() {
		return timeWindow;
	}

	public List<EventType> getIterativeTypes() {
		return iterativeTypes;
	}

	@Override
	public String getStructureSummary() {
		return root.toString();
	}

	@Override
	public State getStateByAtomicCondition(AtomicCondition condition) {
		return root.getNodeByAtomicCondition(condition);
	}

	@Override
	public double getStateReachProbability(State state) {
		return ((Node)state).getNodeReachProbability();
	}

	@Override
	public void removeConflictingInstances(List<Match> matches) {
		if (matches == null) {
			return;
		}
		for (Match match : matches) {
			storage.removeConflictingInstances(match);
		}		
	}
}
