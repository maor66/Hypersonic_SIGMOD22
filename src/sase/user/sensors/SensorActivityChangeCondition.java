package sase.user.sensors;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.DoubleEventCondition;

import static sase.user.sensors.SensorEventTypeManager.activityChangeIndex;

public class SensorActivityChangeCondition extends DoubleEventCondition {
    public SensorActivityChangeCondition(EventType firstType, EventType secondType, Double selectivity) {
        super(firstType, secondType, selectivity);
    }

    public SensorActivityChangeCondition(EventType firstType, EventType secondType) {
        super(firstType, secondType);
    }

    @Override
    public String toString() {
        return String.format("Comparison of activity change of %s and %s",
                eventTypes.get(0).getName(), eventTypes.get(1).getName());
    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SensorActivityChangeCondition)) {
            return false;
        }
        SensorActivityChangeCondition condition = (SensorActivityChangeCondition)other;
        return (firstType == condition.firstType && secondType == condition.secondType);
    }

    @Override
    protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
        return (Double) firstEvent.getAttributeValue(activityChangeIndex) < (Double) secondEvent.getAttributeValue(activityChangeIndex);
    }

    public boolean verifySpecificCondition(Event firstEvent, Event secondEvent) {
        return verifyDoubleEvent(firstEvent, secondEvent);
    }
}
