package sase.input;

import sase.base.EventType;
import sase.specification.SimulationSpecification;
import sase.specification.workload.WorkloadSpecification;

public abstract class EventTypesConverter {
	
	protected final WorkloadSpecification workloadSpecification;
	
	public EventTypesConverter(SimulationSpecification simulationSpecification) {
		workloadSpecification = simulationSpecification.getWorkloadSpecification();
	}

	public abstract EventType convertToKnownEventType(String[] rawEvent);
}
