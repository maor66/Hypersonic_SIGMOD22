package sase.pattern.condition.base;

import java.util.List;

import sase.base.Event;
import sase.config.MainConfig;
import sase.pattern.condition.Condition;
import sase.simulator.Environment;
import sase.statistics.ConditionSelectivityCollector;

/**
 * Represents a condition which involves a single check of primitive event(s) attribute(s).
 */
public abstract class AtomicCondition extends Condition {

	public AtomicCondition() {
		super();
	}
	
	public AtomicCondition(Double selectivity) {
		super(selectivity);
	}
	
	@Override
	public Double getSelectivity() {
		if (MainConfig.isSelectivityMonitoringAllowed) {
			return Environment.getEnvironment().getSelectivityEstimator().getSelectivityEstimate(this);
		}
		return super.getSelectivity();
	}
	
	public boolean verify(List<Event> events) {
		if (this instanceof DoubleEventCondition) {
			Boolean success = 
				Environment.getEnvironment().getPredicateResultsCache().getConditionEvaluationResult(this,
																									 events.get(0),
																									 events.get(1));
			if (success != null) {
				return success;
			}
		}
		boolean success = actuallyVerify(events);
		if (MainConfig.conditionSelectivityMeasurementMode) {
			ConditionSelectivityCollector.getInstance().recordConditionEvaluation(getConditionKey(), success);
		}
		if (MainConfig.isSelectivityMonitoringAllowed) {
			Environment.getEnvironment().getSelectivityEstimator().registerConditionVerification(this, success);
		}
		if (this instanceof DoubleEventCondition) {
			Environment.getEnvironment().getPredicateResultsCache().recordConditionEvaluation(this,
																							  events.get(0),
																							  events.get(1),
																							  success);
		}
		return success;
	}
	
	protected abstract boolean actuallyVerify(List<Event> events);
	
}
