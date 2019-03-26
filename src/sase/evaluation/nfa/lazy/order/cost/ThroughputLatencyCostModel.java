package sase.evaluation.nfa.lazy.order.cost;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.config.EventRateConfig;
import sase.pattern.Pattern;

public class ThroughputLatencyCostModel extends ThroughputCostModel implements ICostModel {
	
	private final Double throughputToLatencyRatio;
	private final List<EventType> totalOrderOfEventTypes;
	private final EventType lastEventType;
	
	public ThroughputLatencyCostModel(List<EventType> totalOrderOfEventTypes, Double throughputToLatencyRatio) {
		this.throughputToLatencyRatio = throughputToLatencyRatio;
		this.totalOrderOfEventTypes = totalOrderOfEventTypes;
		this.lastEventType = this.totalOrderOfEventTypes.get(this.totalOrderOfEventTypes.size() - 1);
	}
	
	private List<EventType> getMissingTypes(List<EventType> eventTypes) {
		List<EventType> result = new ArrayList<EventType>();
		for (EventType eventType : totalOrderOfEventTypes) {
			if (!eventTypes.contains(eventType)) {
				result.add(eventType);
			}
		}
		return result;
	}

	@Override
	public Double getOrderCost(Pattern pattern, List<EventType> order) {
		Double costByThroughputModel = super.getOrderCost(pattern, order);
		if (costByThroughputModel == null) {
			return null;
		}
		int indexOfLastType = order.indexOf(lastEventType);
		if (indexOfLastType == -1 || throughputToLatencyRatio == 0) {
			return costByThroughputModel;//no latency penalty here
		}
		Double latencyFromLastEventInstanceMatching = indexOfLastType == 0 ? 0.0 :
													  costsOfLastOrderStates.get(order.get(indexOfLastType - 1));
		List<EventType> delayedProcessedEventTypes;
		if (indexOfLastType == order.size() - 1) {
			delayedProcessedEventTypes = new ArrayList<EventType>();
		}
		else {
			delayedProcessedEventTypes = new ArrayList<EventType>(order.subList(indexOfLastType, order.size()));
		}
		delayedProcessedEventTypes.addAll(getMissingTypes(order));
		Double latencyFromInputBufferSearches = 0.0;
		for (EventType eventType : delayedProcessedEventTypes) {
			Double currentEventRate = EventRateConfig.eventRate.get(eventType.getName());
			if (currentEventRate == null) {
				return null;
			}
			latencyFromInputBufferSearches += currentEventRate;
		}
		
		Double costByLatencyModel = latencyFromLastEventInstanceMatching + latencyFromInputBufferSearches;
		return costByThroughputModel + throughputToLatencyRatio * costByLatencyModel;
	}
	
	public Double getRatio() {
		return throughputToLatencyRatio;
	}

}
