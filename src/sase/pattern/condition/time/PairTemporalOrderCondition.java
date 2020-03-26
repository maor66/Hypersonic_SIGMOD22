package sase.pattern.condition.time;

import java.util.Objects;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;

/**
 * Represents a condition enforcing a temporal order on a pair of events.
 */
public class PairTemporalOrderCondition extends DoubleEventCondition {

	public PairTemporalOrderCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
    public boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		return firstEvent.getSequenceNumber() < secondEvent.getSequenceNumber();
	}
	
	@Override
	public String toString() {
		return String.format("Temporal condition of %s => %s", 
				eventTypes.get(0), eventTypes.get(1));
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PairTemporalOrderCondition)) {
			return false;
		}
		PairTemporalOrderCondition otherCondition = (PairTemporalOrderCondition) other;
		return (getLeftEventType() == otherCondition.getLeftEventType() && 
				getRightEventType() == otherCondition.getRightEventType());	
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(getLeftEventType(), getRightEventType());
    }

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
	
	@Override
	public Double getSelectivity() {
		return 1.0;//prevent temporal conditions' calculated selectivities from inserting noise into plan evaluation results
	}
}
