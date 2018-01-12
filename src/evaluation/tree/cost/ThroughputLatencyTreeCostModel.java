package evaluation.tree.cost;

import java.util.List;

import base.EventType;
import evaluation.tree.ITreeCostModel;
import evaluation.tree.elements.node.InternalNode;
import evaluation.tree.elements.node.LeafNode;
import evaluation.tree.elements.node.Node;

public class ThroughputLatencyTreeCostModel extends ThroughputTreeCostModel implements ITreeCostModel {

	private final Double throughputToLatencyRatio;
	private final List<EventType> totalOrderOfEventTypes;
	private final EventType lastEventType;
	
	public ThroughputLatencyTreeCostModel(List<EventType> totalOrderOfEventTypes, Double throughputToLatencyRatio) {
		this.throughputToLatencyRatio = throughputToLatencyRatio;
		this.totalOrderOfEventTypes = totalOrderOfEventTypes;
		this.lastEventType = this.totalOrderOfEventTypes.get(this.totalOrderOfEventTypes.size() - 1);
	}
	
	@Override
	public Double getCost(Node root) {
		Double throughputCost = super.getCost(root);
		if (throughputCost == null || throughputToLatencyRatio == 0.0) {
			return throughputCost;
		}
		LeafNode lastEventTypeLeaf = getLeafByEventType(root, lastEventType);
		if (lastEventTypeLeaf == null) {
			return throughputCost;
		}
		Double latencyCost = getLatencyCost(lastEventTypeLeaf);
		if (latencyCost == null) {
			return null;
		}
		return throughputCost + throughputToLatencyRatio * latencyCost;
	}

	private Double getLatencyCost(Node node) {
		if (node.getParent() == null)
			return 0.0;
		return getCardinality(node.getPeer()) + getLatencyCost(node.getParent());
	}

	private LeafNode getLeafByEventType(Node node, EventType eventType) {
		if (node instanceof LeafNode) {
			LeafNode leafNode = (LeafNode)node;
			return (leafNode.getEventType() == eventType) ? leafNode : null;
		}
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			LeafNode leftResult = getLeafByEventType(internalNode.getLeftChild(), eventType);
			if (leftResult != null) {
				return leftResult;
			}
			return getLeafByEventType(internalNode.getRightChild(), eventType);
		}
		return null;
	}
	
	public Double getRatio() {
		return throughputToLatencyRatio;
	}

}
