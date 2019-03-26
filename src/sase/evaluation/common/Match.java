package sase.evaluation.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.simulator.Environment;
import sase.statistics.Statistics;

public class Match implements ContainsEvent{
	private final List<Event> primitiveEvents;
	private final long detectionLatency;
	private  boolean isLastInput = false;
	
	public Match(List<Event> primitiveEvents, long latestEventTimestamp) {
		if (primitiveEvents.size() != 1)
		{
			Environment.getEnvironment().getStatisticsManager().incrementDiscreteMemoryStatistic(Statistics.instanceCreations);
		}
		this.primitiveEvents = primitiveEvents;
		this.detectionLatency = System.currentTimeMillis() - latestEventTimestamp;
	}
	public Match()
	{
		isLastInput = true;
		detectionLatency = 0;
		primitiveEvents = null;
	}

	public List<Event> getPrimitiveEvents() {
		return primitiveEvents;
	}
	
	public static List<Match> asList(Match match) {
		List result = new ArrayList<Match>();
		result.add(match);
		return result;
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

	public Match createNewPartialMatchWithEvent(Event event) {
		//TODO: latency measurement is probably wrong
		//TODO: creates partial match by evaluation/frequency order and not by sequence order. not sure if ok
		if (primitiveEvents.size() > 5) {
			System.out.println("too much events");
		}
		return new Match(Stream.concat(primitiveEvents.stream(),Event.asList(event).stream()).collect(Collectors.toList()), event.getSystemTimestamp()); //Combining two lists)
	}

    public long getEarliestEvent() {
		long earliestTime = Long.MAX_VALUE;
		for (Event event : primitiveEvents){
			earliestTime = (earliestTime > event.getTimestamp()) ? event.getTimestamp() :earliestTime;
		}
		return earliestTime;
    }

    public Event getLatestEvent()
	{
		long latestTime = Integer.MIN_VALUE;
		Event e = null;
		for (Event event : primitiveEvents){
			if (latestTime < event.getTimestamp()) {
				latestTime = event.getTimestamp();
				e = event;
			}
		}
		return e;
	}

	public long getLatestEventTimestamp() {
		return getLatestEvent().getTimestamp();
	}

	@Override
	public long getTimestamp() {
		return getLatestEventTimestamp();
	}


	@Override
	public long getSequenceNumber() {
		return getLatestEvent().getSequenceNumber();
	}

	@Override
	public long getEarliestTimestamp() {
		return getEarliestEvent();
	}

    @Override
    public boolean isLastInput() {
        return isLastInput;
    }

}