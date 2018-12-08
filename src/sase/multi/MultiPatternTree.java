package sase.multi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.OrderEvaluationPlan;
import sase.multi.sla.SlaAwarePattern;
import sase.pattern.CompositePattern;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.time.EventTemporalPositionCondition;
import sase.pattern.condition.time.GlobalTemporalOrderCondition;

public class MultiPatternTree extends MultiPlan {

	private final MultiPatternTreeNode root;
	private final HashMap<Long, MultiPatternTreeNode> acceptingNodes;
	private final HashMap<Long, CompositePattern> idToPattern;	
	
	public MultiPatternTree() {
		this(true);
	}
	
	public MultiPatternTree(boolean enableSharing) {
		super(enableSharing);
		root = enableSharing ? new SharedMultiPatternTreeNode() : new MultiPatternTreeNode();
		acceptingNodes = new HashMap<Long, MultiPatternTreeNode>();
		idToPattern = new HashMap<Long, CompositePattern>();
	}
	
	public MultiPatternTree(MultiPatternTree source) {
		super(source);
		acceptingNodes = new HashMap<Long, MultiPatternTreeNode>();
		idToPattern = new HashMap<Long, CompositePattern>(source.idToPattern);
		root = recursiveCopy(source.root, null);
	}
	
	private MultiPatternTreeNode recursiveCopy(MultiPatternTreeNode source, MultiPatternTreeNode parent) {
		MultiPatternTreeNode destination = enableSharing ?
				new SharedMultiPatternTreeNode(parent, 
											   source.getEventType(), source.getCondition(), source.getOriginalTimeWindow()) :
				new MultiPatternTreeNode(parent, source.getEventType(), source.getCondition(), source.getOriginalTimeWindow());
		destination.setMaxTimeWindow(source.getTimeWindow());
		Long acceptingPatternId = source.getAcceptingPatternId();
		if (acceptingPatternId != null) {
			destination.setAcceptingPatternId(acceptingPatternId);
			acceptingNodes.put(acceptingPatternId, destination);
		}
		for (MultiPatternTreeNode child : source.getAllChildren()) {
			destination.addChild(recursiveCopy(child, destination));
		}
		return destination;
	}
	
	public MultiPatternTreeNode getRoot() {
		return root;
	}

	@Override
	public void addPatternPlan(CompositePattern pattern, EvaluationPlan plan) {
		List<EventType> orderBasedPlan = ((OrderEvaluationPlan)plan).getRepresentation();
		List<CNFCondition> conditionsByOrder = pattern.getCNFCondition().getSubConditionsByOrder(orderBasedPlan, false);
		List<EventTemporalPositionCondition> temporalConditionsByOrder = 
							GlobalTemporalOrderCondition.getPatternTemporalConstraintsForOrder(pattern, orderBasedPlan);
		MultiPatternTreeNode currentNode = root;
		for (int i = 0; i < orderBasedPlan.size(); ++i) {
			CNFCondition conditionForCurrentNode = new CNFCondition(conditionsByOrder.get(i));
			conditionForCurrentNode.addAtomicCondition(temporalConditionsByOrder.get(i));
			MultiPatternTreeNode newNode = enableSharing ?
										   new SharedMultiPatternTreeNode(currentNode, orderBasedPlan.get(i), 
												   						  conditionForCurrentNode, pattern.getTimeWindow()) :
										   new MultiPatternTreeNode(currentNode, orderBasedPlan.get(i), 
												   					conditionForCurrentNode, pattern.getTimeWindow());
			//returns either a new or an existing child
			currentNode = currentNode.addChild(newNode);
		}
		currentNode.setAcceptingPatternId(pattern.getPatternId());
		acceptingNodes.put(pattern.getPatternId(), currentNode);
		idToPattern.put(pattern.getPatternId(), pattern);
		if (pattern instanceof SlaAwarePattern) {
			slaAwarePatterns.add((SlaAwarePattern)pattern);
		}
	}
	
	@Override
	public boolean removePatternPlan(CompositePattern pattern) {
		MultiPatternTreeNode acceptingNode = acceptingNodes.remove(pattern.getPatternId());
		if (acceptingNode == null) {
			return false;
		}
		acceptingNode.resetAcceptingPatternId();
		MultiPatternTreeNode currentNode = acceptingNode;
		while (!currentNode.hasChildren() && //a node can only be removed if it has no more children (i.e., it is not shared)
				currentNode.getAcceptingPatternId() == null) {
			MultiPatternTreeNode parentNode = currentNode.getParent();
			if (parentNode == null) { //the root is reached
				break;
			}
			if (!parentNode.removeChild(currentNode)) {
				throw new RuntimeException("Corrupt MPT encountered");
			}
			currentNode = parentNode;
		}
		//have to reset the time window for the remainder of the states till the root is reached
		while (currentNode.getParent() != null) {
			if (!currentNode.refreshTimeWindow(pattern.getTimeWindow())) {
				break;
			}
			currentNode = currentNode.getParent();
		}
		idToPattern.remove(pattern.getPatternId());
		if (pattern instanceof SlaAwarePattern) {
			slaAwarePatterns.remove((SlaAwarePattern)pattern);
		}
		return true;
	}
	
	@Override
	public EvaluationPlan getPlanForPattern(CompositePattern pattern) {
		MultiPatternTreeNode currentNode = acceptingNodes.get(pattern.getPatternId());
		if (currentNode == null) {
			throw new RuntimeException("Unexpected pattern specified");
		}
		List<EventType> result = new ArrayList<EventType>();
		while (currentNode != root) {
			result.add(0, currentNode.getEventType());
			currentNode = currentNode.getParent();
		}
		return new OrderEvaluationPlan(result);
	}
	
	@Override
	public Collection<CompositePattern> getPatterns() {
		return idToPattern.values();
	}
	
	@Override
	public String getSignature() {
		return root.getSignature();
	}
	
	@Override
	public String toString() {
		return root.toString();
	}

}
