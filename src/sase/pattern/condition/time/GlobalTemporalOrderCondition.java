package sase.pattern.condition.time;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.CompositePattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;

public class GlobalTemporalOrderCondition extends CNFCondition {

	private static List<AtomicCondition> createTimingConstraints(List<List<EventType>> sequences) {
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		for (List<EventType> sequence : sequences) {
			for (int i = 0; i < sequence.size() - 1; ++i) {
				for (int j = i + 1; j < sequence.size(); ++j) {
					conditions.add(new PairTemporalOrderCondition(sequence.get(i), sequence.get(j)));
				}
			}
		}
		return conditions;
	}
	
	public static List<EventTemporalPositionCondition> getPatternTemporalConstraintsForOrder(CompositePattern pattern,
																							 List<EventType> order) {
		return new GlobalTemporalOrderCondition(pattern.extractSequences(false)).getPositionConstraintsByOrder(order, false);
	}
	
	public GlobalTemporalOrderCondition(List<List<EventType>> sequences) {
		super(createTimingConstraints(sequences));
	}
	
	private boolean hasOrderConditionForPair(EventType firstEventType, EventType secondEventType) {
		return atomicConditions.contains(new PairTemporalOrderCondition(firstEventType, secondEventType));
	}
	
	private CNFCondition reduceRedundantConditions(CNFCondition condition, EventType respectiveEventType) {
		List<AtomicCondition> atomicConditions = condition.getAtomicConditions();
		int indexToRemove;
		do {//the external loop ensures that the process will continue until no more changes are possible
			indexToRemove = -1;//no choice but to use a "magic"
			for (int i = 0; i < atomicConditions.size(); ++i) {
				for (int j = i + 1; j < atomicConditions.size(); ++j) {
					PairTemporalOrderCondition firstCondition = (PairTemporalOrderCondition)atomicConditions.get(i);
					PairTemporalOrderCondition secondCondition = (PairTemporalOrderCondition)atomicConditions.get(j);
					if (firstCondition.getLeftEventType() == respectiveEventType &&
						secondCondition.getLeftEventType() == respectiveEventType) {
						//two conditions of type x < a; x < b are found
						EventType firstType = firstCondition.getRightEventType();
						EventType secondType = secondCondition.getRightEventType();
						//if a < b, the condition 'x < b' is to be removed
						if (hasOrderConditionForPair(firstType, secondType)) {
							indexToRemove = j;
							break;
						}
						//if b < a, the condition 'x < a' is to be removed
						if (hasOrderConditionForPair(secondType, firstType)) {
							indexToRemove = i;
							break;
						}
					}
					if (firstCondition.getRightEventType() == respectiveEventType &&
						secondCondition.getRightEventType() == respectiveEventType) {
						//two conditions of type a < x; b < x are found
						EventType firstType = firstCondition.getLeftEventType();
						EventType secondType = secondCondition.getLeftEventType();
						//if a < b, the condition 'a < x' is to be removed
						if (hasOrderConditionForPair(firstType, secondType)) {
							indexToRemove = i;
							break;
						}
						//if b < a, the condition 'b < x' is to be removed
						if (hasOrderConditionForPair(secondType, firstType)) {
							indexToRemove = j;
							break;
						}
					}
				}
				if (indexToRemove != -1) {
					atomicConditions.remove(indexToRemove);
					break;
				}
			}
		}
		while (indexToRemove != -1);
		return new CNFCondition(atomicConditions);
	}
	
	public List<EventTemporalPositionCondition> getPositionConstraintsByOrder(List<EventType> order,
																			  boolean shouldIgnoreUnknownTypes) {
		List<CNFCondition> eventConditionsByOrder = getSubConditionsByOrder(order, shouldIgnoreUnknownTypes);
		List<EventTemporalPositionCondition> result = new ArrayList<EventTemporalPositionCondition>();
		for (int i = 0; i < eventConditionsByOrder.size(); ++i) {
			EventType currentType = order.get(i);
			CNFCondition eventCondition = eventConditionsByOrder.get(i);
			CNFCondition reducedCondition = reduceRedundantConditions(eventCondition, currentType);
			
			List<EventType> precedingEventTypes = new ArrayList<EventType>();
			List<EventType> succeedingEventTypes = new ArrayList<EventType>();
			for (AtomicCondition atomicCondition : reducedCondition.getAtomicConditions()) {
				PairTemporalOrderCondition condition = (PairTemporalOrderCondition) atomicCondition;
				if (condition.getLeftEventType() == currentType) {
					succeedingEventTypes.add(condition.getRightEventType());
				}
				else if (condition.getRightEventType() == currentType) {
					precedingEventTypes.add(condition.getLeftEventType());
				}
			}
			
			EventTemporalPositionCondition temporalPositionCondition = 
					new EventTemporalPositionCondition(currentType, precedingEventTypes, succeedingEventTypes);
			result.add(temporalPositionCondition);
		}
		return result;
	}
	
	public EventTemporalPositionCondition getPositionConstraintsForType(EventType eventType) {
		CNFCondition conditionForType = new CNFCondition(new ArrayList<AtomicCondition>(atomicConditions));
		CNFCondition reducedConditionForType = reduceRedundantConditions(conditionForType, eventType);
		
		List<EventType> precedingEventTypes = new ArrayList<EventType>();
		List<EventType> succeedingEventTypes = new ArrayList<EventType>();
		for (AtomicCondition atomicCondition : reducedConditionForType.getAtomicConditions()) {
			PairTemporalOrderCondition condition = (PairTemporalOrderCondition) atomicCondition;
			if (condition.getLeftEventType() == eventType) {
				succeedingEventTypes.add(condition.getRightEventType());
			}
			else if (condition.getRightEventType() == eventType) {
				precedingEventTypes.add(condition.getLeftEventType());
			}
		}
		
		return new EventTemporalPositionCondition(eventType, precedingEventTypes, succeedingEventTypes);
	}
}
