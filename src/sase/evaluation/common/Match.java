package sase.evaluation.common;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sase.base.Event;
import sase.base.EventType;

public class Match {
	private final List<Event> primitiveEvents;
	private final long detectionLatency;
	
	public Match(List<Event> primitiveEvents, long latestEventTimestamp) {
		this.primitiveEvents = primitiveEvents;
		this.detectionLatency = System.currentTimeMillis() - latestEventTimestamp;
	}
	
	public List<Event> getPrimitiveEvents() {
		return primitiveEvents;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Match))
			return false;
		Match otherMatch = (Match)other;
		for (Event event : primitiveEvents) {
			if (!(otherMatch.primitiveEvents.contains(event))) {
				return false;
			}
		}
		for (Event event : otherMatch.primitiveEvents) {
			if (!(primitiveEvents.contains(event))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(primitiveEvents);
    }
	
	public long getDetectionLatency() {
		return detectionLatency;
	}

	@Override
	public String toString() {
		String result = "[";
		for (int i = 0; i < primitiveEvents.size(); ++i) {
			result += primitiveEvents.get(i);
			if (i < primitiveEvents.size() - 1) {
				result += ";";
			}
		}
		result += "]";
		return result;
	}

	public Match createNewPartialMatchWithEvent(List<EventType> fullEvaluationOrder, Event event) {
		//TODO: latency measurement is probably wrong
		//TODO: creates partial match by evaluation/frequency order and not by sequence order. not sure if ok
		return new Match(Stream.concat(primitiveEvents.stream(),List.of(event).stream()).collect(Collectors.toList()), event.getSystemTimestamp()); //Combining two lists)
	}

    public long getEarliestEvent() {
		long earliestTime = Integer.MAX_VALUE;
		for (Event event : primitiveEvents){
			earliestTime = (earliestTime < event.getTimestamp()) ? event.getTimestamp() :earliestTime;
		}
		return earliestTime;
    }
}