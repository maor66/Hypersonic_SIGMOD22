package sase.user.stocks.specification;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.iteration.FirstValueExternalCondition;
import sase.specification.condition.ConditionSpecification;
import sase.user.stocks.condition.StockDeltaOrderingCondition;

import java.util.ArrayList;
import java.util.List;

public class IterativeStockDeltaOrderingConditionSpecification extends ConditionSpecification {
    private final String precedingEventName;
    private final String iterativeEventName;
    private final String succeedingEventName;

    public IterativeStockDeltaOrderingConditionSpecification(String precedingEventName,
                                                                   String iterativeEventName,
                                                                   String succeedingEventName) {
        this.precedingEventName = precedingEventName;
        this.iterativeEventName = iterativeEventName;
        this.succeedingEventName = succeedingEventName;
    }

    @Override
    public List<AtomicCondition> createConditions() {
        List<AtomicCondition> result = new ArrayList<AtomicCondition>();
        EventType precedingType = EventTypesManager.getInstance().getTypeByName(precedingEventName);
        EventType iterativeType = EventTypesManager.getInstance().getTypeByName(iterativeEventName);
        EventType succeedingType = EventTypesManager.getInstance().getTypeByName(succeedingEventName);
        result.add(new FirstValueExternalCondition(new StockDeltaOrderingCondition(precedingType,iterativeType)));
        result.add(new FirstValueExternalCondition(new StockDeltaOrderingCondition(iterativeType,succeedingType)));
        return result;

    }

    @Override
    public String getShortDescription() {
        return String.format("%s=>(%s)*=>%s", precedingEventName, iterativeEventName, succeedingEventName);
    }
}
