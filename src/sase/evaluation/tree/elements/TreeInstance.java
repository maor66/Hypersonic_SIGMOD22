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

	protected TreeEvaluationMechanism tree;
	protected Node currentNode;
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
			if (event.getTimestamp() + getInstanceTimeWindow() < currentTime)
				return true;
		}
		return false;
	}
	
	protected Long getInstanceTimeWindow() {
		return tree.getTimeWindow();
	}
	
	public boolean hasMatch() {
		return (currentNode == tree.getRoot());
	}
	
	public Match getMatch() {
		if (!hasMatch())
			return null;
		return new Match(matchBuffer.getEvents());
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
		Node commonParent = Node.getCommonParent(currentNode, peerInstance.getCurrentNode());
		if (commonParent == null) {
			throw new RuntimeException("No common parent found");
		}
		EventBuffer newEventBuffer = matchBuffer.clone();
		newEventBuffer.extend(peerInstance.matchBuffer);
		Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferInsertions,
																						  newEventBuffer.size());
		return createInstanceForNodeAndBuffer(tree, commonParent, newEventBuffer);
	}
	
	public TreeInstance createExtendedAggregatedLeafInstance(Event newEvent) {
		if (!(currentNode instanceof LeafNode)) {
			return null; //this is definitely an error
		}
		EventBuffer newMatchBuffer = matchBuffer.clone();
		newMatchBuffer.addEvent(newEvent);
		return createInstanceForNodeAndBuffer(tree, currentNode, newMatchBuffer);
	}

	protected TreeInstance createInstanceForNodeAndBuffer(TreeEvaluationMechanism tree, Node node, EventBuffer buffer) {
		return new TreeInstance(tree, node, buffer);
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
