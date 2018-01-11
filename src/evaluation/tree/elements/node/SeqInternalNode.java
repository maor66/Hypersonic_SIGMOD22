package sase.evaluation.tree.elements.node;

import java.util.List;

import sase.base.EventSelectionStrategies;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.contiguity.PairwiseContiguityCondition;
import sase.pattern.condition.time.PairTemporalOrderCondition;

public class SeqInternalNode extends InternalNode {

	protected final List<EventType> order;

	public SeqInternalNode(CNFCondition condition, List<EventType> order, Node leftSon, Node rightSon) {
		super(condition, leftSon, rightSon);
		this.order = order;
	}

	public List<EventType> getOrder() {
		return order;
	}

	@Override
	public void finalizeNode(Node parent) {
		super.finalizeNode(parent);
		calculateTemporalCondition();
	}
	
	private void calculateTemporalCondition() {
		if (order == null) {
			return;
		}
		List<EventType> leftEventTypes = leftChild.getEventTypes();
		List<EventType> rightEventTypes = rightChild.getEventTypes();
		CNFCondition temporalCondition = new CNFCondition();
		for (EventType eventType : rightEventTypes) {
			for (int i = order.indexOf(eventType) - 1; i >= 0; --i) {
				EventType currEventType = order.get(i);
				if (rightEventTypes.contains(currEventType)) {
					break;//already tightly bound
				}
				if (leftEventTypes.contains(currEventType)) {
					//tightest bound found
					PairTemporalOrderCondition currentTemporalCondition = 
												new PairTemporalOrderCondition(currEventType, eventType);
					currentTemporalCondition.setSelectivity(1.0);
					temporalCondition.addAtomicCondition(currentTemporalCondition);
					if (MainConfig.selectionStrategy == EventSelectionStrategies.CONTUGUITY) {
						temporalCondition.addAtomicCondition(createContiguityCondition(currEventType, eventType));
					}
					break;
				}
			}
			for (int i = order.indexOf(eventType) + 1; i < order.size(); ++i) {
				EventType currEventType = order.get(i);
				if (rightEventTypes.contains(currEventType)) {
					break;//already tightly bound
				}
				if (leftEventTypes.contains(currEventType)) {
					//tightest bound found
					PairTemporalOrderCondition currentTemporalCondition = 
												new PairTemporalOrderCondition(eventType, currEventType);
					currentTemporalCondition.setSelectivity(1.0);
					temporalCondition.addAtomicCondition(currentTemporalCondition);
					if (MainConfig.selectionStrategy == EventSelectionStrategies.CONTUGUITY) {
						temporalCondition.addAtomicCondition(createContiguityCondition(eventType, currEventType));
					}
					break;
				}
			}
		}
		nodeCondition.addAtomicConditions(temporalCondition);
	}
	
	private PairwiseContiguityCondition createContiguityCondition(EventType firstEventType, EventType secondEventType) {
		int firstIndex = order.indexOf(firstEventType);
		int secondIndex = order.indexOf(secondEventType);
		return new PairwiseContiguityCondition(firstEventType, secondEventType, secondIndex - firstIndex);
	}

}
