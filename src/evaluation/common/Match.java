package sase.evaluation.common;

import java.util.List;
import java.util.Objects;

import sase.base.Event;

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
}