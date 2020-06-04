package sase.user.stocks.specification;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.ConditionSpecification;
import sase.specification.condition.DoubleEventConditionSpecification;
import sase.user.stocks.condition.TimestampComparisonCondition;

import static sase.input.producers.FileBasedEventProducer.STOCK_TYPE_NAME_LENGTH;

public class TimestampFusedComparisonConditionSpecification extends DoubleEventConditionSpecification {
    public TimestampFusedComparisonConditionSpecification(String firstEventName, String secondEventName) {
        super(firstEventName, secondEventName, null);
    }

    @Override
    public String toString() {
        return String.format("Timestamp comparison between %s and %s", firstEventName, secondEventName);
    }

    @Override
    protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType, Double conditionSelectivity) {
        if (firstEventName.length() != STOCK_TYPE_NAME_LENGTH) {
            throw new RuntimeException("This specification should only be used with event types that are earlier than the fused event type");
        }
        return new TimestampComparisonCondition(firstType, secondType, conditionSelectivity, secondEventName.length() != STOCK_TYPE_NAME_LENGTH);
    }
}
