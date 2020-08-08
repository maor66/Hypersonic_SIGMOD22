package sase.user.stocks.specification;

import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.user.stocks.condition.FusedStockDeltaOrderingCondition;
import sase.user.stocks.condition.StockDeltaOrderingCondition;

public class StockDeltaOrderingFusedFirstConditionSpecification extends StockDeltaOrderingConditionSpecification {
    public StockDeltaOrderingFusedFirstConditionSpecification(String firstEventName, String secondEventName) {
        super(firstEventName, secondEventName);
    }

    @Override
    public String toString() {
        return String.format("Fused first type delta comparison between %s and %s", firstEventName, secondEventName);
    }
    @Override
    protected AtomicCondition createDoubleEventCondition(EventType firstType, EventType secondType,
                                                         Double conditionSelectivity) {
        return new FusedStockDeltaOrderingCondition(firstType, secondType, selectivity, true);
    }
}
