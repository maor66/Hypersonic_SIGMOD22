package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.pattern.condition.base.SingleEventCondition;
import sase.user.stocks.StockEventTypesManager;

public class StockDeltaOrderingFusedInnerCondition extends SingleEventCondition {
    public StockDeltaOrderingFusedInnerCondition(EventType eventType) {
        super(eventType);
        if (!MainConfig.isSelectivityMonitoringAllowed &&
            !MainConfig.conditionSelectivityMeasurementMode) {
            setSelectivityByEstimate();
        }
    }

    protected Double calculateDeltaByIndex(Event event, int firstStockMeasurementIndex) {
        Double firstValue = (Double)event.getAttributeValue(firstStockMeasurementIndex);
        Double secondValue = (Double)event.getAttributeValue(firstStockMeasurementIndex + 1);
        return Math.abs(firstValue - secondValue);
    }

    @Override
    protected boolean verifySingleEvent(Event event) {
        return calculateDeltaByIndex(event, StockEventTypesManager.firstStockMeasurementIndex) < calculateDeltaByIndex(event, StockEventTypesManager.SecondTypeFusedFirstStockMeasurementIndex);
    }


    @Override
    protected String getConditionKey() {
        return String.format("%s", type);
    }
}
