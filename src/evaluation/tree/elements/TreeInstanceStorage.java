package sase.evaluation.tree.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.evaluation.common.Match;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class TreeInstanceStorage {

	private final Node treeRoot;
	private HashMap<Node, List<TreeInstance>> instances;

	public TreeInstanceStorage(Node treeRoot) {
		this.treeRoot = treeRoot;
		instances = new HashMap<Node, List<TreeInstance>>();
		List<Node> nodes = treeRoot.getNodesInSubTree();
		for (Node node : nodes) {
			instances.put(node, new ArrayList<TreeInstance>());
		}
	}
	
	public void addInstance(TreeInstance instance) {
		getSubListForInstance(instance).add(instance);
	}
	
	public void addSameNodeInstances(List<TreeInstance> instanceList) {
		if (instanceList.isEmpty()) {
			return;
		}
		getSubListForInstance(instanceList.get(0)).addAll(instanceList);
	}
	
	private List<TreeInstance> getSubListForInstance(TreeInstance instance) {
		Node currentNode = instance.getCurrentNode();
		if (!(instances.containsKey(currentNode))) {
			throw new RuntimeException("Unknown node detected.");
		}
		return instances.get(currentNode);
	}
	
	public List<Match> getMatches() {
		List<Match> matches = new ArrayList<Match>();
		for (TreeInstance instance : instances.get(treeRoot)) {
			matches.add(instance.getMatch());
		}
		instances.get(treeRoot).clear();
		return matches;
	}
	
	public void validateTimeWindow(long currentTime) {
		for (List<TreeInstance> instanceList : instances.values()) {
			if (instanceList.isEmpty()) {
				continue;
			}
			TreeInstance currentInstance = instanceList.get(0);
			long numOfRemovedInstances = 0;
			while (currentInstance.isExpired(currentTime)) {
				instanceList.remove(0);
				++numOfRemovedInstances;
				if (instanceList.isEmpty()) {
					break;
				}
				currentInstance = instanceList.get(0);
			}
			Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.instanceDeletions,
																							  numOfRemovedInstances);
		}
	}
	
	public List<TreeInstance> getInstancesForNode(Node node) {
		return instances.get(node);
	}
	
	public long size() {
		long size = 0;
		for (List<TreeInstance> instanceList : instances.values()) {
			if (instanceList.isEmpty()) {
				continue;
			}
			size += instanceList.size() * instanceList.get(0).size();
		}
		return size;
	}
	
	public int getInstancesNumber() {
		int number = 0;
		for (List<TreeInstance> instanceList : instances.values()) {
			number += instanceList.size();
		}
		return number;
	}

}
