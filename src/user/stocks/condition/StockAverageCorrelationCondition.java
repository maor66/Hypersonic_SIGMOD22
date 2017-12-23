package sase.user.stocks.condition;

import sase.pattern.condition.iteration.AggregatedExternalCondition;
import sase.user.stocks.AverageStocksVectorAggregator;

public class StockAverageCorrelationCondition extends AggregatedExternalCondition {

	public StockAverageCorrelationCondition(StockCorrelationCondition correlationCondition) {
		super(correlationCondition.getLeftEventType(), correlationCondition.getRightEventType(), 
			  new AverageStocksVectorAggregator(), correlationCondition);
	}

}
