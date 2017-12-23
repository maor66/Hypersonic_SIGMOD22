package evaluation.nfa.lazy.optimizations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.Event;
import evaluation.nfa.lazy.elements.LazyInstance;
import pattern.condition.Condition;
import pattern.condition.base.AtomicCondition;
import pattern.condition.base.CNFCondition;
import pattern.condition.iteration.lazy.GroupByAttributeCondition;

public class BufferGrouper extends BufferPreprocessor {
	
	private final int attributeIndex;
	
	public BufferGrouper(Condition condition) {
		super(condition);
		List<AtomicCondition> atomicConditions = filterCondition.getAtomicConditions();
		if (atomicConditions.size() > 1) {
			throw new RuntimeException("Only a single group-by parameter is supported in this version");
		}
		if (isTrivial()) {
			attributeIndex = -1;
		}
		else {
			GroupByAttributeCondition groupByAttributeCondition = (GroupByAttributeCondition)atomicConditions.get(0);
			attributeIndex = groupByAttributeCondition.getAttributeIndex();
		}
	}
	
	@Override
	public List<Event> preprocessEvents(LazyInstance instance, List<Event> events, boolean isLastPreprocessor) {
		if (events.size() < 2) {
			return events;
		}
		HashMap<Object, List<Event>> eventsByAttributeValue = new HashMap<Object, List<Event>>();
		for (Event event : events) {
			Object value = event.getAttributeValue(attributeIndex);
			if (!eventsByAttributeValue.containsKey(value)) {
				eventsByAttributeValue.put(value, new ArrayList<Event>());
			}
			eventsByAttributeValue.get(value).add(event);
		}
		List<Event> groupedEvents = new ArrayList<Event>();
		for (List<Event> listOfEvents : eventsByAttributeValue.values()) {
			List<Event> listToAdd = isLastPreprocessor ? 
					createAggregatedEventsFromBufferedEvents(listOfEvents) : listOfEvents;
			groupedEvents.addAll(listToAdd);
		}
		return groupedEvents;
	}

	@Override
	protected void stripPreprocessorConditions(CNFCondition condition) {
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			if (atomicCondition instanceof GroupByAttributeCondition) {
				filterCondition.addAtomicCondition(atomicCondition);
			}
		}
		condition.removeAtomicConditions(filterCondition);
	}
}
