package pattern.condition.base;

import java.util.List;

import base.Event;
import base.EventType;
import config.MainConfig;
import pattern.condition.Condition;
import simulator.Environment;
import statistics.ConditionSelectivityCollector;

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
		Event firstEvent = null;
		Event secondEvent = null;
		if (this instanceof DoubleEventCondition) {
			EventType firstEventType = ((DoubleEventCondition)this).getLeftEventType();
			firstEvent = getEventByType(events, firstEventType);
			EventType secondEventType = ((DoubleEventCondition)this).getRightEventType();
			secondEvent = getEventByType(events, secondEventType);
			Boolean success = 
				Environment.getEnvironment().getPredicateResultsCache().getConditionEvaluationResult(this,
																									 firstEvent,
																									 secondEvent);
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
																							  firstEvent,
																							  secondEvent,
																							  success);
		}
		return success;
	}
	
	private Event getEventByType(List<Event> events, EventType eventType) {
		for (Event event : events) {
			if (event.getType() == eventType) {
				return event;
			}
		}
		return null;
	}
	
	protected abstract boolean actuallyVerify(List<Event> events);
	
}
