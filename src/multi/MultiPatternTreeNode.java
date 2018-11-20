package sase.multi;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.condition.base.CNFCondition;

public class MultiPatternTreeNode {

	private static Long idCounter = (long) 0;
	
	private final Long id;
	private final MultiPatternTreeNode parent;
	private final EventType eventType;
	private final CNFCondition condition;
	private final Long originalTimeWindow;
	private Long timeWindow;
	
	protected List<MultiPatternTreeNode> children;
	private Long acceptingPatternId = null;
	
	private final int level;
	
	public MultiPatternTreeNode() {
		this(null, null, null, null);
	}
	
	public MultiPatternTreeNode(MultiPatternTreeNode parent, EventType eventType, CNFCondition condition, Long timeWindow) {
		this.id = idCounter++;
		this.parent = parent;
		this.eventType = eventType;
		this.condition = condition;
		this.originalTimeWindow = timeWindow;
		this.timeWindow = timeWindow;
		this.children = new ArrayList<MultiPatternTreeNode>();
		this.level = parent == null ? 1 : parent.level + 1;
	}

	public Long getAcceptingPatternId() {
		return acceptingPatternId;
	}

	public void setAcceptingPatternId(Long acceptingPatternId) {
		this.acceptingPatternId = acceptingPatternId;
	}
	
	public void resetAcceptingPatternId() {
		acceptingPatternId = null;
	}

	public MultiPatternTreeNode getParent() {
		return parent;
	}

	public List<MultiPatternTreeNode> getAllChildren() {
		return children;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	public CNFCondition getCondition() {
		return condition;
	}
	
	public Long getTimeWindow() {
		return timeWindow;
	}

	public Long getOriginalTimeWindow() {
		return originalTimeWindow;
	}

	public boolean isRoot() {
		return (parent == null);
	}
	
	protected void setMaxTimeWindow(Long newTimeWindow) {
		if (timeWindow == null) {
			return;
		}
		if (newTimeWindow > timeWindow) {
			timeWindow = newTimeWindow;
		}
	}
	
	public boolean refreshTimeWindow(Long deprecatedTimeWindow) {
		if (timeWindow == null || timeWindow > deprecatedTimeWindow) {
			return false;
		}
		timeWindow = originalTimeWindow;
		for (MultiPatternTreeNode child : children) {
			setMaxTimeWindow(child.getTimeWindow());
		}
		return true;
	}
	
	public MultiPatternTreeNode addChild(MultiPatternTreeNode child) {
		setMaxTimeWindow(child.getTimeWindow());
		children.add(child);
		return child;
	}
	
	public boolean removeChild(MultiPatternTreeNode child) {
		children.remove(child);
		refreshTimeWindow(child.getTimeWindow());
		return true;
	}
	
	public String getSignature() {
		String result = "";
		for (MultiPatternTreeNode child : children) {
			result += child.getSignature() + "|";
		}
		String nodeSignature = eventType == null ? "" : eventType.toString();
		return result.isEmpty() ? nodeSignature : String.format("%s(%s)", nodeSignature, result);
	}

	@Override
	public String toString() {
		String indentation = new String(new char[level]).replace("\0", "\t");
		String result = "";
		for (MultiPatternTreeNode child : children) {
			String childDescription = child.acceptingPatternId == null ?
										String.format("%d", child.id) :
										String.format("%d(accepting %d)", child.id, child.acceptingPatternId);
			result += String.format("%s%d ->(%s)-> %s\n", indentation, id, child.eventType, childDescription);
			result += child.toString();
		}
		return result;
	}

}
