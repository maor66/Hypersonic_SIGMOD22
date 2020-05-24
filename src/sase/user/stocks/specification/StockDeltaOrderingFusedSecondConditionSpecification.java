package sase.user.stocks.specification;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.ConditionSpecification;
import sase.user.stocks.condition.FusedStockDeltaOrderingCondition;

public class StockDeltaOrderingFusedSecondConditionSpecification extends StockDeltaOrderingConditionSpecification {
    public StockDeltaOrderingFusedSecondConditionSpecification(String firstEventName, String secondEventName) {
        super(firstEventName, secondEventName);
    }

    @Override
    public String toString() {
        return String.format("Fused second type delta comparison between %s and %s", firstEventName, secondEventName);
    }
    @Override
    protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
                                                         Double conditionSelectivity) {
        return new FusedStockDeltaOrderingCondition(firstType, secondType, selectivity, false);
    }
}
