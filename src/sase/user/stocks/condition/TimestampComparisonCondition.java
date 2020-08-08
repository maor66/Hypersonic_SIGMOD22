package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.user.stocks.StockEventTypesManager;

public class TimestampComparisonCondition extends DoubleEventCondition {

    private final boolean isSecondTypeFused;

    public TimestampComparisonCondition(EventType firstType, EventType secondType, Double conditionSelectivity, boolean isSecondTypeFused) {
        super(firstType, secondType, conditionSelectivity);
        this.isSecondTypeFused = isSecondTypeFused;

    }

    @Override
    protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
        long secondEventTimestamp = isSecondTypeFused ?
                Long.valueOf(secondEvent.getAttributeValue(StockEventTypesManager.fusedFirstTypeTimestampIndex).toString()) :
                (long) secondEvent.getAttributeValue(StockEventTypesManager.timestampAttributeIndex);
        return secondEventTimestamp >= (Long) firstEvent.getAttributeValue(StockEventTypesManager.timestampAttributeIndex);
    }
}
