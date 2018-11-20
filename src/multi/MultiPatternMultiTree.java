package sase.multi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import sase.base.EventType;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.TreeEvaluationPlan;
import sase.evaluation.tree.elements.node.InternalNode;
import sase.evaluation.tree.elements.node.LeafNode;
import sase.evaluation.tree.elements.node.Node;
import sase.multi.sla.SlaAwarePattern;
import sase.pattern.CompositePattern;

public class MultiPatternMultiTree extends MultiPlan {

	private List<CompositePattern> patterns;
	private HashMap<Long, Node> acceptingNodes;
	private HashMap<Long, Node> singlePatternTrees;
	private HashMap<EventType, LeafNode> leaves;
	
	public MultiPatternMultiTree() {
		this(true);
	}
	
	public MultiPatternMultiTree(boolean enableSharing) {
		super(enableSharing);
		initDataStructures();
	}
	
	public MultiPatternMultiTree(MultiPatternMultiTree source) {
		super(source);
		initDataStructures();
		if (!enableSharing) {
			patterns = new ArrayList<CompositePattern>(source.patterns);
			singlePatternTrees = new HashMap<Long, Node>(source.singlePatternTrees);
			return;
		}
		for (CompositePattern pattern : source.patterns) {
			addPatternPlan(pattern, source.singlePatternTrees.get(pattern.getPatternId()));
		}
	}
	
	private void initDataStructures() {
		patterns = new ArrayList<CompositePattern>();
		acceptingNodes = new HashMap<Long, Node>();
		singlePatternTrees = new HashMap<Long, Node>();
		leaves = new HashMap<EventType, LeafNode>();
	}
	
	private void addPatternPlan(CompositePattern pattern, Node treePlan) {
		patterns.add(pattern);
		singlePatternTrees.put(pattern.getPatternId(), treePlan);
		if (pattern instanceof SlaAwarePattern) {
			slaAwarePatterns.add((SlaAwarePattern)pattern);
		}
		if (!enableSharing) {
			return;
		}
		enableEventTypeCache(true);
		Node newNode = getOrCreateSharedNode(treePlan);
		//we assume that acceptingNodes contains no duplicates, that is, there are no identical patterns in a workload
		((InternalNode)newNode).setAcceptingPatternId(pattern.getPatternId());
		acceptingNodes.put(pattern.getPatternId(), newNode);
		recursiveUpdateTimeWindow(newNode, pattern.getTimeWindow());
		enableEventTypeCache(false);
	}
	
	private void recursiveUpdateTimeWindow(Node node, Long timeWindow) {
		Long oldTimeWindow = node.getTimeWindow();
		if (oldTimeWindow != null && oldTimeWindow >= timeWindow) {
			return;
		}
		node.setTimeWindow(timeWindow);
		if (!(node instanceof InternalNode)) {
			return;
		}
		InternalNode internalNode = (InternalNode)node;
		recursiveUpdateTimeWindow(internalNode.getLeftChild(), timeWindow);
		recursiveUpdateTimeWindow(internalNode.getRightChild(), timeWindow);
	}
	
	private Node getOrCreateSharedNode(Node sourceNode) {
		Node existingNode = getSharedNode(sourceNode);
		if (existingNode != null) {
			return existingNode;
		}
		//at this point, we know for sure that sourceNode is not a leaf
		InternalNode internalSourceNode = (InternalNode)sourceNode;
		Node leftChild = getOrCreateSharedNode(internalSourceNode.getLeftChild());
		Node rightChild = getOrCreateSharedNode(internalSourceNode.getRightChild());
		if (Node.getCommonParent(leftChild, rightChild) != null) {
			throw new RuntimeException("getSharedNode() failed to find an existing node");
		}
		return internalSourceNode.createEquivalentNode(leftChild, rightChild);
	}
	
	private Node getSharedNode(Node node) {
		if (node instanceof LeafNode) {
			return getOrCreateSharedLeaf((LeafNode)node);
		}
		InternalNode internalNode = (InternalNode)node;
		InternalNode searchResult = null;
		for (Node rootNode : acceptingNodes.values()) {
			searchResult = findInternalNodeInSubtree(internalNode, rootNode);
			if (searchResult != null) {
				break;
			}
		}
		for (Node rootNode : acceptingNodes.values()) {
			rootNode.markVisited(false, true);
		}
		return searchResult;
	}
	
	private InternalNode findInternalNodeInSubtree(InternalNode sourceNode, Node rootNode) {
		if (rootNode.isVisited() || (rootNode instanceof LeafNode)) {
			return null;
		}
		InternalNode rootAsInternalNode = (InternalNode)rootNode;
		if (rootNode.equals(sourceNode)) {
			return rootAsInternalNode;
		}
		if (sourceNode.isSuperNode(rootNode)) {
			rootNode.markVisited(true, true);
			return null;//no point in continuing deeper
		}
		rootNode.markVisited(true, false);
		InternalNode nodeInLeftSubtree = findInternalNodeInSubtree(sourceNode, rootAsInternalNode.getLeftChild());
		if (nodeInLeftSubtree != null) {
			return nodeInLeftSubtree;
		}
		return findInternalNodeInSubtree(sourceNode, rootAsInternalNode.getRightChild());
	}
	
	private LeafNode getOrCreateSharedLeaf(LeafNode leafNode) {
		LeafNode targetLeaf = leaves.get(leafNode.getEventType());
		if (targetLeaf != null) {
			return targetLeaf;
		}
		LeafNode newLeaf = new LeafNode(leafNode);
		leaves.put(newLeaf.getEventType(), newLeaf);
		return newLeaf;
	}
	
	@Override
	public void addPatternPlan(CompositePattern pattern, EvaluationPlan plan) {
		Node treePlan = ((TreeEvaluationPlan)plan).getRepresentation();
		addPatternPlan(pattern, treePlan);
	}
	
	@Override
	public boolean removePatternPlan(CompositePattern pattern) {
		boolean hadPattern = patterns.remove(pattern);
		if (!hadPattern) {
			return false;
		}
		singlePatternTrees.remove(pattern.getPatternId());
		if (enableSharing) {
			enableEventTypeCache(true);
			recursiveRemovePlan(acceptingNodes.remove(pattern.getPatternId()), null);
			enableEventTypeCache(false);
		}
		if (pattern instanceof SlaAwarePattern) {
			slaAwarePatterns.remove((SlaAwarePattern)pattern);
		}
		return true;
	}

	private void recursiveRemovePlan(Node node, InternalNode parent) {
		if (parent != null) {
			//this is not a root - have to remove the parent
			List<Node> nodeParents = node.getParents();
			nodeParents.remove(parent);
			if (!nodeParents.isEmpty()) {
				//still shared by other parents
				node.setMaxParentTimeWindow();
				return;
			}
			if (node instanceof LeafNode) {
				leaves.remove(((LeafNode)node).getEventType());
				return;
			}
		}
		InternalNode internalNode = (InternalNode)node;
		recursiveRemovePlan(internalNode.getLeftChild(), internalNode);
		recursiveRemovePlan(internalNode.getRightChild(), internalNode);
	}

	@Override
	public EvaluationPlan getPlanForPattern(CompositePattern pattern) {
		return new TreeEvaluationPlan(acceptingNodes.get(pattern.getPatternId()));
	}
	
	@Override
	public Collection<CompositePattern> getPatterns() {
		return patterns;
	}
	
	@Override
	public String getSignature() {
		String result = "";
		for (Node acceptingNode : acceptingNodes.values()) {
			result += acceptingNode.getShortDescription() + "|";
		}
		return result;
	}
	
	private void enableEventTypeCache(boolean enable) {
		for (Node node : acceptingNodes.values()) {
			node.enableEventTypeCache(enable);
		}
	}
	
	public List<Node> getFilteredSharedSubtrees(CompositePattern pattern, Node filter) {
		List<Node> unfilteredSharedSubtrees = getSharedSubtrees(pattern);
		List<Node> filteredSharedSubtrees = new ArrayList<Node>();
		for (Node node : unfilteredSharedSubtrees) {
			if (filter != null && hasIntersection(node.getEventTypes(), filter.getEventTypes())) {
				continue;
			}
			filteredSharedSubtrees.add(node);
		}
		return filteredSharedSubtrees.isEmpty() ? null : filteredSharedSubtrees;
	}
	
	private boolean hasIntersection(List<EventType> firstList, List<EventType> secondList) {
		List<EventType> intersection = new ArrayList<EventType>(firstList);
		intersection.retainAll(secondList);
		return !intersection.isEmpty();
	}
	
	public List<Node> getSharedSubtrees(CompositePattern pattern) {
		Node root = acceptingNodes.get(pattern.getPatternId());
		if (root == null) {
			return null;
		}
		List<Node> result = new ArrayList<Node>();
		recursiveGetSharedSubtrees(root, result);
		return result;
	}
	
	private void recursiveGetSharedSubtrees(Node node, List<Node> sharedSubtrees) {
		if (node.isMultiTreeNode()) {
			sharedSubtrees.add(node);
			return;
		}
		if (!(node instanceof InternalNode)) {
			return;
		}
		InternalNode internalNode = (InternalNode)node;
		recursiveGetSharedSubtrees(internalNode.getLeftChild(), sharedSubtrees);
		recursiveGetSharedSubtrees(internalNode.getRightChild(), sharedSubtrees);
	}

	public HashMap<Long, Node> getAcceptingNodes() {
		return acceptingNodes;
	}

	public HashMap<Long, Node> getSinglePatternTrees() {
		return singlePatternTrees;
	}

	public HashMap<EventType, LeafNode> getLeaves() {
		return leaves;
	}

	@Override
	public String toString() {
		String result = "";
		for (Node singlePatternTree : singlePatternTrees.values()) {
			result += singlePatternTree.toString();
		}
		return result;
	}
}
