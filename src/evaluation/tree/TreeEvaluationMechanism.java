package sase.evaluation.tree;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.HashMap;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.EvaluationPlan;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.tree.elements.LeafNode;
import sase.evaluation.tree.elements.Node;
import sase.evaluation.tree.elements.TreeInstance;
import sase.evaluation.tree.elements.TreeInstanceStorage;
import sase.pattern.Pattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class TreeEvaluationMechanism implements IEvaluationMechanism, IEvaluationMechanismInfo {
	
	private Node root;
	private HashMap<EventType, LeafNode> eventTypeToLeafMap;
	private TreeInstanceStorage storage;
	private final long timeWindow;
	
	public TreeEvaluationMechanism(Pattern pattern, EvaluationPlan evaluationPlan) {
		timeWindow = pattern.getTimeWindow();
		root = evaluationPlan.getTreeRepresentation();
		Environment.getEnvironment().getStatisticsManager().replaceDiscreteStatistic(Statistics.treeNodesNumber,
																					 root.getNodesInSubTree().size());
		eventTypeToLeafMap = new HashMap<EventType, LeafNode>();
		List<LeafNode> leaves = root.getLeavesInSubTree();
		for (LeafNode currLeafNode : leaves) {
			eventTypeToLeafMap.put(currLeafNode.getEventType(), currLeafNode);
		}
		storage = new TreeInstanceStorage(root);
	}

	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
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
		Queue<TreeInstance> instanceQueue = new ArrayDeque<TreeInstance>();
		instanceQueue.add(leafInstance);
		while (!instanceQueue.isEmpty()) {
			TreeInstance currentInstance = instanceQueue.remove();
			if (canStartInstance || currentInstance != leafInstance) {
				storage.addInstance(currentInstance);
			}
			Node peerNode = currentInstance.getCurrentNode().getPeer();
			if (peerNode == null) { //i.e. if this node is the root
				continue;
			}
			List<TreeInstance> peerInstances = storage.getInstancesForNode(peerNode);
			if (peerInstances == null) {
				continue;
			}
			for (TreeInstance peerInstance : peerInstances) {
				TreeInstance parentInstance = currentInstance.createParentInstance(peerInstance);
				if (parentInstance.validateNodeCondition()) {
					instanceQueue.add(parentInstance);
				}
			}
		}
		Environment.getEnvironment().getStatisticsManager().updateDiscreteIfBigger(Statistics.peakInstances,
																				   storage.getInstancesNumber());
		return storage.getMatches();
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		List<Match> matches = storage.getMatches();
		storage.validateTimeWindow(currentTime);
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

}
