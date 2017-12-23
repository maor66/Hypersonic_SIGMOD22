package pattern.condition.base;

import java.util.List;

import base.Event;

/**
 * Represents an empty condition which is always satisfied.
 */
public class TrivialCondition extends AtomicCondition {

	@Override
	protected boolean actuallyVerify(List<Event> events) {
		return true;
	}

	@Override
	public String toString() {
		return String.format("Trivial Condition");
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}
}
