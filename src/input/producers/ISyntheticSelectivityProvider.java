package input.producers;

import base.Event;

public interface ISyntheticSelectivityProvider {

	public double getSelectivity(int conditionID, Event firstEvent, Event secondEvent);
	public long getConditionEvaluationDuration();
}
