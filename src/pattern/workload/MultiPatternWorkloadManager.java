package sase.pattern.workload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sase.base.EventType;
import sase.multi.MultiPlan;
import sase.pattern.Pattern;

public class MultiPatternWorkloadManager implements IWorkloadManager {

	protected List<Pattern> patterns;
	
	public MultiPatternWorkloadManager(List<Pattern> patterns) {
		this.patterns = patterns;
	}

	@Override
	public List<Pattern> getCurrentWorkload() {
		return patterns;
	}
	
	@Override
	public List<EventType> getAllEventTypes() {
		Set<EventType> allEventTypes = new HashSet<EventType>();
		for (Pattern pattern : patterns) {
			allEventTypes.addAll(pattern.getEventTypes());
		}
		return new ArrayList<EventType>(allEventTypes);
	}
	
	@Override
	public Long getMaxTimeWindow() {
		Long result = (long) 0;
		for (Pattern pattern : patterns) {
			if (pattern.getTimeWindow() > result) {
				result = pattern.getTimeWindow();
			}
		}
		return result;
	}

	@Override
	public boolean tryAlterWorkload(MultiPlan currentMultiPlan) {
		return false;
	}

}
