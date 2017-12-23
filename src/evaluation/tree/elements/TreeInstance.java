package sase.evaluation.tree.elements;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.evaluation.common.EventBuffer;
import sase.evaluation.common.Match;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class TreeInstance {

	private TreeEvaluationMechanism tree;
	private Node currentNode;
	private EventBuffer matchBuffer;
	
	public TreeInstance(TreeEvaluationMechanism tree, Node currentNode, EventBuffer matchBuffer) {
		this.tree = tree;
		this.currentNode = currentNode;
		this.matchBuffer = (matchBuffer == null) ? new EventBuffer(null) : matchBuffer.clone();
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

	public TreeInstance createParentInstance(TreeInstance peerInstance) {
		if (hasMatch()) {
			return null;
		}
		if (peerInstance.getCurrentNode() != currentNode.getPeer()) { //sanity check
			return null;
		}
		List<Event> events = new ArrayList<Event>(matchBuffer.getEvents());
		List<Event> peerEvents = peerInstance.getEvents();
		events.addAll(peerEvents);
		Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferInsertions,
																						  peerEvents.size());
		return new TreeInstance(tree, currentNode.getParent(), new EventBuffer(null, events));
	}
	
	public void addEvent(Event event) {
		matchBuffer.addEvent(event);
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.bufferInsertions);
	}
	
	public boolean validateNodeCondition() {
		return currentNode.isNodeConditionSatisfied(matchBuffer.getEvents());
	}
	
	public long size() {
		return 4 + matchBuffer.size();
	}

}
