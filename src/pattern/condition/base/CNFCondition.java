package sase.pattern.condition.base;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.Condition;

/**
 * Represents a condition in a format of a CNF formula, containing a conjunction of multiple atomic conditions.
 */
public class CNFCondition extends Condition {

	protected List<AtomicCondition> atomicConditions;

	public CNFCondition(List<AtomicCondition> conditions, List<EventType> eventTypes) {
		atomicConditions = conditions;
		if (eventTypes != null) {
			this.eventTypes.addAll(eventTypes);
		}
		else {
			for (AtomicCondition atomicCondition : conditions) {
				addEventTypes(atomicCondition);
			}
		}
	}
	
	public CNFCondition(List<AtomicCondition> conditions) {
		this(conditions, null);
	}
	
	public CNFCondition() {
		this(new ArrayList<AtomicCondition>());
	}
	
	@Override
	public Double getSelectivity() {
		Double selectivity = 1.0;
		for (AtomicCondition atomicCondition : atomicConditions) {
			if (atomicCondition.getSelectivity() == null) {
				return null;
			}
			selectivity *= atomicCondition.getSelectivity();
		}
		return selectivity;
	}
	
	private void addEventTypes(AtomicCondition condition) {
		for (EventType eventType : condition.getEventTypes()) {
			if (!(eventTypes.contains(eventType)))
				eventTypes.add(eventType);
		}
	}
	
	@Override
	public boolean verify(List<Event> events) {
		for (AtomicCondition condition : atomicConditions) {
			if (!(condition.verify(events)))
				return false;
		}
		return true;
	}
	
	public List<AtomicCondition> getAtomicConditions() {
		return atomicConditions;
	}
	
	public boolean addAtomicCondition(AtomicCondition atomicCondition, int index) {
		if (atomicConditions.contains(atomicCondition))
			return false;
		atomicConditions.add(index, atomicCondition);
		addEventTypes(atomicCondition);
		return true;
	}
	
	public boolean addAtomicCondition(AtomicCondition atomicCondition) {
		return addAtomicCondition(atomicCondition, 0);
	}
	
	public boolean addAtomicConditions(CNFCondition condition) {
		if (condition == null) {
			return true;
		}
		boolean isSuccess = true;
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			isSuccess &= addAtomicCondition(atomicCondition);
		}
		return isSuccess;
	}
	
	public boolean removeAtomicCondition(AtomicCondition atomicCondition) {
		return atomicConditions.remove(atomicCondition);
	}
	
	public boolean removeAtomicConditions(CNFCondition condition) {
		if (condition == null) {
			return true;
		}
		boolean isSuccess = true;
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			isSuccess &= removeAtomicCondition(atomicCondition);
		}
		return isSuccess;
	}
	
	private boolean shouldAddCondition(AtomicCondition condition,
									   EventType eventType,
									   List<EventType> processedTypes,
									   List<EventType> unknownTypes,
									   boolean shouldIgnoreUnknownTypes,
									   List<AtomicCondition> addedConditions) {
		if (addedConditions.contains(condition))
			return false;
		for (EventType currentEventType : condition.getEventTypes()) {
			if (currentEventType == eventType) {
				continue;
			}
			if (processedTypes.contains(currentEventType)) {
				continue;
			}
			if (shouldIgnoreUnknownTypes && unknownTypes.contains(currentEventType)) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	private List<EventType> getUnknownTypes(List<EventType> order) {
		List<EventType> result = new ArrayList<EventType>();
		for (EventType eventType : eventTypes) {
			if (!order.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}
	
	public List<CNFCondition> getSubConditionsByOrder(List<EventType> order, boolean shouldIgnoreUnknownTypes) {
		List<CNFCondition> outputConditions = new ArrayList<CNFCondition>();
		List<AtomicCondition> addedConditions = new ArrayList<AtomicCondition>();
		List<EventType> processedTypes = new ArrayList<EventType>();
		List<EventType> unknownTypes = getUnknownTypes(order);
		for (EventType type : order) {
			List<AtomicCondition> currentAtomicConditions = new ArrayList<AtomicCondition>();
			for (AtomicCondition atomicCondition : atomicConditions) {
				if (shouldAddCondition(atomicCondition, type, processedTypes, unknownTypes,
									   shouldIgnoreUnknownTypes, addedConditions))
					currentAtomicConditions.add(atomicCondition);
			}
			outputConditions.add(new CNFCondition(currentAtomicConditions));
			processedTypes.add(type);
			addedConditions.addAll(currentAtomicConditions);
		}
		return outputConditions;
	}
	
	private List<AtomicCondition> getFilteredConditions(List<EventType> typesToInclude, 
														List<EventType> typesToExclude,
														boolean exclusive) {
		List<AtomicCondition> filteredConditions = new ArrayList<AtomicCondition>();
		for (AtomicCondition atomicCondition : atomicConditions) {
			boolean shouldSkip = false;
			if (typesToExclude != null) {
				for (EventType prohibitedType : typesToExclude) {
					if (atomicCondition.getEventTypes().contains(prohibitedType)) {
						shouldSkip = true;
						break;
					}
				}
			}
			//NOTE: in case of collisions of include and exclude lists, exclude list wins
			if (shouldSkip)
				continue;
			if (typesToInclude != null) {
				List<EventType> conditionEventTypes = atomicCondition.getEventTypes();
				if (exclusive) {
					//all event types are requested to appear
					for (EventType conditionEventType : conditionEventTypes) {
						if (!(typesToInclude.contains(conditionEventType))) {
							shouldSkip = true;
							break;
						}
					}
				}
				else {
					//at least a single event type is requested to appear
					shouldSkip = true;
					for (EventType requiredType : typesToInclude) {
						if (conditionEventTypes.contains(requiredType)) {
							shouldSkip = false;
							break;
						}
					}
				}
			}
			if (!shouldSkip) {
				filteredConditions.add(atomicCondition);
			}
		}
		return filteredConditions;
	}
	
	public CNFCondition getConditionForTypes(List<EventType> types, boolean exclusive) {
		return new CNFCondition(getFilteredConditions(types, null, exclusive));
	}
	
	public CNFCondition getConditionExcludingTypes(List<EventType> types) {
		return new CNFCondition(getFilteredConditions(null, types, false));
	}
	
	public CNFCondition getConditionForType(EventType type, boolean exclusive) {
		List<EventType> types = new ArrayList<EventType>();
		types.add(type);
		return getConditionForTypes(types, exclusive);
	}
	
	public CNFCondition getConditionExcludingType(EventType type) {
		List<EventType> types = new ArrayList<EventType>();
		types.add(type);
		return getConditionExcludingTypes(types);
	}
	
	public CNFCondition getFiltersForType(EventType type) {
		List<EventType> otherTypes = new ArrayList<EventType>(eventTypes);
		otherTypes.remove(type);
		return getConditionExcludingTypes(otherTypes);
	}
	
	public CNFCondition getConditionBetweenTypeSets(List<EventType> firstSet, List<EventType> secondSet) {
		List<AtomicCondition> filteredConditions = new ArrayList<AtomicCondition>();
		List<EventType> otherTypes = new ArrayList<EventType>(eventTypes);
		otherTypes.removeAll(firstSet);
		otherTypes.removeAll(secondSet);
		for (AtomicCondition atomicCondition : atomicConditions) {
			boolean firstSetPresent = false;
			boolean secondSetPresent = false;
			boolean otherTypePresent = false;
			for (EventType eventType : atomicCondition.getEventTypes()) {
				if (otherTypes.contains(eventType)) {
					otherTypePresent = true;
					break;
				}
				if (firstSet.contains(eventType)) {
					firstSetPresent = true;
				}
				else if (secondSet.contains(eventType)) {
					secondSetPresent = true;
				}
			}
			if (firstSetPresent && secondSetPresent && (!otherTypePresent)) {
				filteredConditions.add(atomicCondition);
			}
		}
		return new CNFCondition(filteredConditions);
	}

	@Override
	public String toString() {
		if (atomicConditions.isEmpty())
			return "Empty CNF condition";
		String baseString = String.format("CNF Condition: %s", atomicConditions);
		return baseString;
		/*Double selectivity = getSelectivity();
		if (selectivity != null) {
			return baseString + String.format("(selectivity: %f)", selectivity);
		}
		return baseString + "(selectivity unknown)";*/
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}