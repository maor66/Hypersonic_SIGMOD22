package sase.evaluation.tree.elements.node;

import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.tree.elements.TreeInstance;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.time.PairTemporalOrderCondition;

public class NSeqInternalNode extends SeqInternalNode {
	
	private final EventType negativeEventType;
	private final CNFCondition negativeCondition;

	public NSeqInternalNode(CNFCondition condition, List<EventType> order, List<EventType> fullOrder, 
							Node leftSon, Node rightSon, EventType negativeEventType) {
		super(condition, order, leftSon, rightSon);
		this.negativeEventType = negativeEventType;
		this.negativeCondition = createNegativeCondition(fullOrder);
	}

	private CNFCondition createNegativeCondition(List<EventType> fullOrder) {
		CNFCondition negativeCondition = getMainCondition().getConditionForType(negativeEventType, false);
		int negativeEventIndex = fullOrder.indexOf(negativeEventType);
		EventType precedingEventType = (negativeEventIndex == 0) ? null : fullOrder.get(negativeEventIndex - 1);
		EventType succeedingEventType = 
					(negativeEventIndex == fullOrder.size() - 1) ? null : fullOrder.get(negativeEventIndex + 1);
		if ((precedingEventType != null && !getEventTypes().contains(precedingEventType)) || 
			(succeedingEventType != null && !getEventTypes().contains(succeedingEventType))) {
				throw new RuntimeException(String.format("Invalid placement for negative event type %s", negativeEventType));
		}
		if (precedingEventType != null) {
			negativeCondition.addAtomicCondition(new PairTemporalOrderCondition(precedingEventType, negativeEventType));
		}
		if (succeedingEventType != null) {
			negativeCondition.addAtomicCondition(new PairTemporalOrderCondition(negativeEventType, succeedingEventType));
		}
		return negativeCondition;
	}
	
	private boolean validateNegativeCondition(List<Event> positiveEvents, Event negativeEvent) {
		positiveEvents.add(0, negativeEvent);
		if (negativeCondition.verify(positiveEvents)) {
			return false;
		}
		positiveEvents.remove(0);//restore the match buffer
		return true;
	}

	public EventType getNegativeEventType() {
		return negativeEventType;
	}
	
	@Override
	public boolean isNodeConditionSatisfied(TreeInstance treeInstance) {
		if (!super.isNodeConditionSatisfied(treeInstance)) {
			return false;
		}
		List<Event> negativeEvents = treeInstance.getEvaluationMechanism().getNegativeBuffer().getTypeBuffer(negativeEventType);
		for (Event negativeEvent : negativeEvents) {
			if (!validateNegativeCondition(treeInstance.getEvents(), negativeEvent)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected String printNode(String indentation) {
		return super.printNode(indentation) + 
				String.format("\n%s NEGATE[%s](%s)", indentation, negativeEventType, negativeCondition);
	}
}
