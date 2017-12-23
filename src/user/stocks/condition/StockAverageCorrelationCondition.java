package user.stocks.condition;

import pattern.condition.iteration.AggregatedExternalCondition;
import user.stocks.AverageStocksVectorAggregator;

public class StockAverageCorrelationCondition extends AggregatedExternalCondition {

	public StockAverageCorrelationCondition(StockCorrelationCondition correlationCondition) {
		super(correlationCondition.getLeftEventType(), correlationCondition.getRightEventType(), 
			  new AverageStocksVectorAggregator(), correlationCondition);
	}

}
