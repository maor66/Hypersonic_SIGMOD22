package sase.user.sensors;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.DoubleEventConditionSpecification;

public class SensorActivityChangeConditionSpecification  extends DoubleEventConditionSpecification {
    public SensorActivityChangeConditionSpecification(String firstEventName, String secondEventName, Double selectivity) {
        super(firstEventName, secondEventName, selectivity);
    }

    public SensorActivityChangeConditionSpecification(String firstEventName, String secondEventName) {
        super(firstEventName, secondEventName, null);
    }
    @Override
    public String toString() {
        return String.format("Activity change comparison between %s and %s", firstEventName, secondEventName);
    }
    @Override
    protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType, Double conditionSelectivity) {
        return new SensorActivityChangeCondition(firstType, secondType, selectivity);
}
}
