package sase.specification.workload;

import java.util.ArrayList;
import java.util.List;

import sase.pattern.workload.WorkloadManagerTypes;

public class WorkloadSpecification {

	protected final List<PatternSpecification> patternSpecifications;
	protected final Long maxTimeWindow;
	protected final List<String> eventNames;
	protected final WorkloadManagerTypes managerType;
	
	public WorkloadSpecification(List<PatternSpecification> patternSpecifications, WorkloadManagerTypes managerType) {
		this.patternSpecifications = patternSpecifications;
		this.managerType = managerType;
		Long maxTimeWindow = (long)0;
		eventNames = new ArrayList<String>();
		for (PatternSpecification patternSpecification : patternSpecifications) {
			Long currentTimeWindow = patternSpecification.getTimeWindow();
			if (currentTimeWindow == null) {
				this.maxTimeWindow = null;
				return;
			}
			if (currentTimeWindow > maxTimeWindow) {
				maxTimeWindow = currentTimeWindow;
			}
			for (String eventName : patternSpecification.getEventNames()) {
				if (!eventNames.contains(eventName)) {
					eventNames.add(eventName);
				}
			}
		}
		this.maxTimeWindow = maxTimeWindow;
	}

	public List<PatternSpecification> getPatternSpecifications() {
		return patternSpecifications;
	}

	public Long getMaxTimeWindow() {
		return maxTimeWindow;
	}

	public String getShortDescription() {
		return String.format("Workload of size %d", patternSpecifications.size());
	}
	
	@Override
	public String toString() {
		String result = getShortDescription() + ":\n";
		for (PatternSpecification patternSpecification : patternSpecifications) {
			result += patternSpecification.toString() + "\n";
		}
		return result;
	}

	public List<String> getEventNames() {
		return eventNames;
	}

	public WorkloadManagerTypes getManagerType() {
		return managerType;
	}

}
