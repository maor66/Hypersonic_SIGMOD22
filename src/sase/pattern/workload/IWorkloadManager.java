package sase.pattern.workload;

import java.util.List;

import sase.base.EventType;
import sase.multi.MultiPlan;
import sase.pattern.Pattern;

public interface IWorkloadManager {

	public List<Pattern> getCurrentWorkload();
	public List<EventType> getAllEventTypes();
	public Long getMaxTimeWindow();
	public boolean tryAlterWorkload(MultiPlan currentMultiPlan);
}
