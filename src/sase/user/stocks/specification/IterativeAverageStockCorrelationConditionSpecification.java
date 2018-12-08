package sase.user.stocks.specification;

import java.util.ArrayList;
import java.util.List;

import sase.base.EventType;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.condition.ConditionSpecification;
import sase.user.stocks.condition.StockAverageCorrelationCondition;
import sase.user.stocks.condition.StockCorrelationCondition;
import sase.user.stocks.condition.StockSameCompanyCondition;

public class IterativeAverageStockCorrelationConditionSpecification extends ConditionSpecification {
	
	private final String triggeringEventName;
	private final String iterativeEventName;
	private final String averagingEventName;
	private final double correlationLimit;

	public IterativeAverageStockCorrelationConditionSpecification(String triggeringEventName,
																  String iterativeEventName,
																  String averagingEventName,
																  double correlationLimit) {
		this.triggeringEventName = triggeringEventName;
		this.iterativeEventName = iterativeEventName;
		this.averagingEventName = averagingEventName;
		this.correlationLimit = correlationLimit;
	}

	public String getTriggeringEventName() {
		return triggeringEventName;
	}

	public String getIterativeEventName() {
		return iterativeEventName;
	}

	public String getAveragingEventName() {
		return averagingEventName;
	}

	public double getCorrelationLimit() {
		return correlationLimit;
	}
	
	public List<AtomicCondition> createConditions() {
		EventType triggeringType = EventTypesManager.getInstance().getTypeByName(triggeringEventName);
		EventType iterativeType = EventTypesManager.getInstance().getTypeByName(iterativeEventName);
		EventType averagingType = EventTypesManager.getInstance().getTypeByName(averagingEventName);
		/*StockCorrelationCondition correlationForFilteringCondition = 
									new StockCorrelationCondition(triggeringType, iterativeType, correlationLimit);
		StockCorrelationIterationTriggerCondition triggerThresholdCondition = 
									new StockCorrelationIterationTriggerCondition(correlationForFilteringCondition);*/
		StockSameCompanyCondition sameCompanyConditionCondition = 
									new StockSameCompanyCondition(iterativeType);
		StockCorrelationCondition correlationForAverageCondition = 
									new StockCorrelationCondition(iterativeType, averagingType, correlationLimit);
		StockAverageCorrelationCondition averageThresholdCondition = 
									new StockAverageCorrelationCondition(correlationForAverageCondition);
		//required as a temporary 'hack' to avoid the 'unfiltered' triggering-averaging combinations in Lazy NFA - 
		//to be solved once predicates come into play
		StockCorrelationCondition externalCorrelationCondition = 
				new StockCorrelationCondition(triggeringType, averagingType, correlationLimit);
		
		List<AtomicCondition> result = new ArrayList<AtomicCondition>();
		//result.add(triggerThresholdCondition);
		result.add(sameCompanyConditionCondition);
		result.add(averageThresholdCondition);
		result.add(externalCorrelationCondition);
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("%s => (%s)* => %s with correlation %f", 
							 triggeringEventName, iterativeEventName, averagingEventName, correlationLimit);
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}

}
