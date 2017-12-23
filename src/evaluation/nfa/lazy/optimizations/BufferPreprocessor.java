package evaluation.nfa.lazy.optimizations;

import java.util.ArrayList;
import java.util.List;

import base.AggregatedEvent;
import base.Event;
import evaluation.nfa.lazy.elements.LazyInstance;
import pattern.condition.Condition;
import pattern.condition.base.CNFCondition;

public abstract class BufferPreprocessor {
	
	public static List<Event> createAggregatedEventsFromBufferedEvents(List<Event> bufferedEvents) {
		List<Event> result = new ArrayList<Event>();
		if (bufferedEvents.isEmpty()) {
			return result;
		}
		for (Event bufferedEvent : bufferedEvents) {
			List<Event> newAggregatedEvents = new ArrayList<Event>();
			AggregatedEvent trivialAggregatedEvent = new AggregatedEvent(bufferedEvent.getType());
			trivialAggregatedEvent.addPrimitiveEvent(bufferedEvent);
			newAggregatedEvents.add(trivialAggregatedEvent);
			for (Event previousEvent : result) {
				AggregatedEvent aggregatedEvent = (AggregatedEvent)previousEvent;
				AggregatedEvent newAggregatedEvent = new AggregatedEvent(bufferedEvent.getType(), 
																		 aggregatedEvent.getPrimitiveEvents());
				newAggregatedEvent.addPrimitiveEvent(bufferedEvent);
				newAggregatedEvents.add(newAggregatedEvent);
			}
			result.addAll(newAggregatedEvents);
		}
		
		return result;
	}

	protected final CNFCondition filterCondition;

	public BufferPreprocessor(Condition condition) {
		filterCondition = new CNFCondition();
		stripPreprocessorConditions((CNFCondition)condition);
	}
	
	public boolean isTrivial() {
		return filterCondition.getAtomicConditions().isEmpty();
	}
	
	public List<Event> preprocessEvent(LazyInstance instance, Event event, boolean isLastPreprocessor) {
		List<Event> events = new ArrayList<Event>(1);
		events.add(event);
		return preprocessEvents(instance, events, isLastPreprocessor);
	}
	
	public abstract List<Event> preprocessEvents(LazyInstance instance, List<Event> events, boolean isLastPreprocessor);
	protected abstract void stripPreprocessorConditions(CNFCondition condition);
}
