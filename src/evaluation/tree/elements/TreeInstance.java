package sase.evaluation.tree.elements;

import java.util.List;

import sase.base.Event;
import sase.evaluation.common.EventBuffer;
import sase.evaluation.common.Match;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class TreeInstance {

	private TreeEvaluationMechanism tree;
	private Node currentNode;
	private EventBuffer matchBuffer;
	
	public TreeInstance(TreeEvaluationMechanism tree, Node currentNode, EventBuffer matchBuffer) {
		this.tree = tree;
		this.currentNode = currentNode;
		this.matchBuffer = (matchBuffer == null) ? new EventBuffer(tree.getIterativeTypes()) : matchBuffer.clone();
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.instanceCreations);
	}
	
	public TreeInstance(TreeEvaluationMechanism tree, Node currentNode) {
		this(tree, currentNode, null);
	}
	
	public boolean isExpired(long currentTime) {
		List<Event> events = matchBuffer.getEvents();
		for (Event event : events) {
			if (event.getTimestamp() + tree.getTimeWindow() < currentTime)
				return true;
		}
		return false;
	}
	
	public boolean hasMatch() {
		return (currentNode == tree.getRoot());
	}
	
	public Match getMatch() {
		if (!hasMatch())
			return null;
		return new Match(matchBuffer.getEvents(), matchBuffer.getLatestTimestamp());
	}
	
	public Node getCurrentNode() {
		return currentNode;
	}

	public List<Event> getEvents() {
		return matchBuffer.getEvents();
	}

	public TreeEvaluationMechanism getEvaluationMechanism() {
		return tree;
	}

	public TreeInstance createParentInstance(TreeInstance peerInstance) {
		if (hasMatch()) {
			return null;
		}
		if (peerInstance.getCurrentNode() != currentNode.getPeer()) { //sanity check
			return null;
		}
		EventBuffer newEventBuffer = matchBuffer.clone();
		newEventBuffer.extend(peerInstance.matchBuffer);
		Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferInsertions,
																						  newEventBuffer.size());
		return new TreeInstance(tree, currentNode.getParent(), newEventBuffer);
	}
	
	public TreeInstance createExtendedAggregatedLeafInstance(Event newEvent) {
		if (!(currentNode instanceof LeafNode)) {
			return null; //this is definitely an error
		}
		EventBuffer newMatchBuffer = matchBuffer.clone();
		newMatchBuffer.addEvent(newEvent);
		return new TreeInstance(tree, currentNode, newMatchBuffer);
	}
	
	public void addEvent(Event event) {
		matchBuffer.addEvent(event);
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.bufferInsertions);
	}
	
	public boolean validateNodeCondition() {
		return currentNode.isNodeConditionSatisfied(this);
	}
	
	public long size() {
		return 4 + matchBuffer.size();
	}

}
