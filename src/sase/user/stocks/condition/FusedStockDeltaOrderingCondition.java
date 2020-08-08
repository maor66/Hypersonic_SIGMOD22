package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.AtomicCondition;
import sase.user.stocks.StockEventTypesManager;

public class FusedStockDeltaOrderingCondition extends StockDeltaOrderingCondition {
    private final boolean isFirstTypeFused;

    public FusedStockDeltaOrderingCondition(EventType firstType, EventType secondType, Double selectivity, boolean isFirstTypeFused) {
        super(firstType, secondType, selectivity);
        this.isFirstTypeFused = isFirstTypeFused;
    }
    @Override
    protected Double calculateDelta(Event event) {
        int firstStockMeasurementIndexByType = (isFirstTypeFused || event.getType() == secondType) ?
                StockEventTypesManager.firstStockMeasurementIndex : StockEventTypesManager.SecondTypeFusedFirstStockMeasurementIndex;
        Double firstValue = (Double)event.getAttributeValue(firstStockMeasurementIndexByType);
        Double secondValue = (Double)event.getAttributeValue(firstStockMeasurementIndexByType + 1);
        return Math.abs(firstValue - secondValue);
    }
}
