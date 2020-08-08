package sase.user.stocks.specification;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.ConditionSpecification;
import sase.user.stocks.condition.StockDeltaOrderingFusedInnerCondition;

import java.util.List;

public class StockDeltaOrderingFusedInnerConditionSpecification extends ConditionSpecification {
    private final String eventTypeName;

    public StockDeltaOrderingFusedInnerConditionSpecification(String typename) {
        eventTypeName = typename;
    }

    @Override
    public List<AtomicCondition> createConditions() {
        EventType type = EventTypesManager.getInstance().getTypeByName(eventTypeName);
        return List.of(new StockDeltaOrderingFusedInnerCondition(type));
    }

    @Override
    public String getShortDescription() {
        return "Specification of delta stock ordering for " + eventTypeName + " which is a fused type ";
    }
}
