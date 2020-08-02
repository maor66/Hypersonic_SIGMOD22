package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.pattern.condition.base.DoubleEventCondition;
import sase.simulator.Environment;
import sase.statistics.Statistics;
import sase.user.stocks.StockEventTypesManager;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 * Represents a condition on a correlation rate between appearances of two given primitive events.
 */
public class StockCorrelationCondition extends DoubleEventCondition {
	
	private final double minCorrelation;
	
	public StockCorrelationCondition(EventType firstType, EventType secondType, 
									 double minCorrelation, Double selectivity) {
		super(firstType, secondType, selectivity);
		this.minCorrelation = minCorrelation;
	}
	
	public StockCorrelationCondition(EventType firstType, EventType secondType, double minCorrelation) {
		this(firstType, secondType, minCorrelation, null);
	}

	private double[] getStockHistory(Event event) {
		double[] result = new double[event.getAttributes().length - 2];
		for (int i = 2; i < event.getAttributes().length; ++i) {
			result[i-2] = (Double)event.getAttributeValue(i);
		}
		return result;
	}
	
	private String getCompanyName(Event event) {
		return (String)event.getAttributeValue(StockEventTypesManager.labelAttributeIndex);
	}
	
	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		if (getCompanyName(firstEvent) == getCompanyName(secondEvent))
			return false;
		
		double[] firstEventHistory = getStockHistory(firstEvent);
		double[] secondEventHistory = getStockHistory(secondEvent);
		double correlation = new PearsonsCorrelation().correlation(firstEventHistory,
															   	   secondEventHistory);
//		Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.correlationComputations);
		return minCorrelation > 0 ? correlation > minCorrelation : correlation < minCorrelation;
	}
	
	@Override
	public String toString() {
		String baseString = String.format("Correlation of %.1f between %s and %s ", 
				minCorrelation, eventTypes.get(0).getName(), eventTypes.get(1).getName());
		Double currentSelectivity = getSelectivity();
		if (currentSelectivity != null) {
			return baseString + String.format("(selectivity:%f)", currentSelectivity);
		}
		return baseString + "(selectivity unknown)";
	}

	@Override
	protected String getConditionKey() {
		return String.format("%s:%s:%.1f", firstType.getName(), secondType.getName(), minCorrelation);
	}
}
