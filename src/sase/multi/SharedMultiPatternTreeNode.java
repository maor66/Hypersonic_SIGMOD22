package sase.multi;

import java.util.HashMap;

import sase.base.EventType;
import sase.pattern.condition.base.CNFCondition;

public class SharedMultiPatternTreeNode extends MultiPatternTreeNode {

	private HashMap<EventType, MultiPatternTreeNode> childrenByType;
	private HashMap<EventType, HashMap<CNFCondition, MultiPatternTreeNode>> childrenByTypeAndCondition;

	
	public SharedMultiPatternTreeNode(MultiPatternTreeNode parent, EventType eventType, CNFCondition condition, Long timeWindow) {
		super(parent, eventType, condition, timeWindow);
		childrenByType = new HashMap<EventType, MultiPatternTreeNode>();
		childrenByTypeAndCondition = new HashMap<EventType, HashMap<CNFCondition, MultiPatternTreeNode>>();
	}

	public SharedMultiPatternTreeNode() {
		this(null, null, null, null);
	}
	
	@Override
	public MultiPatternTreeNode addChild(MultiPatternTreeNode child) {
		setMaxTimeWindow(child.getTimeWindow());
		EventType childEventType = child.getEventType();
		if (childrenByType.containsKey(childEventType)) {
			MultiPatternTreeNode existingChild = childrenByType.get(childEventType);
			if (child.getCondition().equals(existingChild.getCondition())) {
				existingChild.setMaxTimeWindow(child.getTimeWindow());
				return existingChild;//an equivalent node already exists
			}
			HashMap<CNFCondition, MultiPatternTreeNode> newHashMap = new HashMap<CNFCondition, MultiPatternTreeNode>();
			newHashMap.put(existingChild.getCondition(), existingChild);
			newHashMap.put(child.getCondition(), child);
			childrenByType.remove(childEventType);
			childrenByTypeAndCondition.put(childEventType, newHashMap);
		}
		else if (childrenByTypeAndCondition.containsKey(childEventType)) {
			HashMap<CNFCondition, MultiPatternTreeNode> existingHashMap = childrenByTypeAndCondition.get(childEventType);
			CNFCondition childCondition = child.getCondition();
			if (existingHashMap.containsKey(childCondition)) {
				MultiPatternTreeNode existingChild = existingHashMap.get(childCondition);
				existingChild.setMaxTimeWindow(child.getTimeWindow());
				return existingChild;//an equivalent node already exists
			}
			existingHashMap.put(childCondition, child);
		}
		else {
			childrenByType.put(childEventType, child);
		}
		children.add(child);
		return child;
	}
	
	@Override
	public boolean removeChild(MultiPatternTreeNode child) {
		EventType childEventType = child.getEventType();
		if (childrenByType.containsKey(childEventType)) {
			childrenByType.remove(childEventType);
		}
		else if (childrenByTypeAndCondition.containsKey(childEventType)) {
			HashMap<CNFCondition, MultiPatternTreeNode> existingHashMap = childrenByTypeAndCondition.get(childEventType);
			CNFCondition childCondition = child.getCondition();
			if (existingHashMap.containsKey(childCondition)) {
				existingHashMap.remove(childCondition);
				if (existingHashMap.size() == 1) {
					childrenByTypeAndCondition.remove(existingHashMap);
					childrenByType.put(childEventType, existingHashMap.values().iterator().next());
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
		children.remove(child);
		refreshTimeWindow(child.getTimeWindow());
		return true;
	}

}
