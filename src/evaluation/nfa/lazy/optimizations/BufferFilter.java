package sase.evaluation.nfa.lazy.optimizations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.nfa.lazy.elements.LazyInstance;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.iteration.lazy.IteratedFilterCondition;

public class BufferFilter extends BufferPreprocessor {
	
	private class Activator {
		private final HashMap<IteratedFilterCondition, Event> conditionToExternalEvent;
		
		public Activator(LazyInstance instance) {
			conditionToExternalEvent = new HashMap<IteratedFilterCondition, Event>();
			for (IteratedFilterCondition filter : conditionToType.keySet()) {
				EventType eventType = conditionToType.get(filter);
				Event event = instance.getMatchBufferEventByType(eventType);
				conditionToExternalEvent.put(filter, event);
			}
		}
		
		public boolean applyFilter(Event eventToFilter) {
			for (AtomicCondition atomicCondition : filterCondition.getAtomicConditions()) {
				IteratedFilterCondition atomicFilterCondition = (IteratedFilterCondition)atomicCondition;
				Event externalEventToVerifyWith = conditionToExternalEvent.get(atomicFilterCondition);
				if (!atomicFilterCondition.filterEvent(eventToFilter, externalEventToVerifyWith)) {
					return false;
				}
			}
			return true;
		}
	};

	private final HashMap<IteratedFilterCondition, EventType> conditionToType;
	
	public BufferFilter(Condition condition) {
		super(condition);
		conditionToType = new HashMap<IteratedFilterCondition, EventType>();
		for (AtomicCondition atomicCondition : filterCondition.getAtomicConditions()) {
			IteratedFilterCondition atomicFilterCondition = (IteratedFilterCondition)atomicCondition;
			conditionToType.put(atomicFilterCondition, atomicFilterCondition.getRightEventType());
		}
	}
	
	@Override
	public List<Event> preprocessEvents(LazyInstance instance, List<Event> events, boolean isLastPreprocessor) {
		List<Event> filteredEvents = new ArrayList<Event>();
		Activator filterActivator = new Activator(instance);
		for (Event event : events) {
			if (filterActivator.applyFilter(event)) {
				filteredEvents.add(event);
			}
		}
		if (isLastPreprocessor) {
			return BufferPreprocessor.createAggregatedEventsFromBufferedEvents(filteredEvents);
		}
		else {
			return filteredEvents;
		}
	}

	@Override
	protected void stripPreprocessorConditions(CNFCondition condition) {
		for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {
			if (atomicCondition instanceof IteratedFilterCondition) {
				filterCondition.addAtomicCondition(atomicCondition);
			}
		}
		condition.removeAtomicConditions(filterCondition);
	}
}
