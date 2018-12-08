package sase.pattern.workload;

import java.util.Arrays;
import java.util.List;

import sase.base.EventType;
import sase.multi.MultiPlan;
import sase.pattern.Pattern;

public class SinglePatternWorkloadManager implements IWorkloadManager {

	private Pattern pattern;
	
	public SinglePatternWorkloadManager(Pattern pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public List<Pattern> getCurrentWorkload() {
		return Arrays.asList(pattern);
	}

	@Override
	public List<EventType> getAllEventTypes() {
		return pattern.getEventTypes();
	}

	@Override
	public Long getMaxTimeWindow() {
		return pattern.getTimeWindow();
	}

	@Override
	public boolean tryAlterWorkload(MultiPlan currentMultiPlan) {
		return false;
	}

}
