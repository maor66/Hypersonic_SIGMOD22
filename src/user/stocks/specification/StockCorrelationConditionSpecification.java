package user.stocks.specification;
import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.EventTypesManager;
import pattern.condition.base.AtomicCondition;
import specification.ConditionSpecification;
import user.stocks.condition.StockCorrelationCondition;

public class StockCorrelationConditionSpecification extends ConditionSpecification {

	private final String firstEventName;
	private final String secondEventName;
	private final double correlationLimit;
	private final Double selectivity;
	
	public StockCorrelationConditionSpecification(String firstEventName,
												  String secondEventName,
												  double correlationLimit,
												  Double selectivity) {
		this.firstEventName = firstEventName;
		this.secondEventName = secondEventName;
		this.correlationLimit = correlationLimit;
		this.selectivity = selectivity;
	}
	
	public StockCorrelationConditionSpecification(String firstEventName,
			  									  String secondEventName,
			  									  double correlationLimit) {
		this(firstEventName, secondEventName, correlationLimit, null);
	}

	public String getFirstEventName() {
		return firstEventName;
	}

	public String getSecondEventName() {
		return secondEventName;
	}

	public double getCorrelationLimit() {
		return correlationLimit;
	}

	public double getSelectivity() {
		return selectivity;
	}
	
	@Override
	public List<AtomicCondition> createConditions() {
		EventType firstType = EventTypesManager.getInstance().getTypeByName(firstEventName);
		EventType secondType = EventTypesManager.getInstance().getTypeByName(secondEventName);
		StockCorrelationCondition condition = new StockCorrelationCondition(firstType, secondType, correlationLimit, selectivity);
		List<AtomicCondition> conditions = new ArrayList<AtomicCondition>();
		conditions.add(condition);
		return conditions;
	}
	
	@Override
	public String toString() {
		String baseString = String.format("Correlation of %f between %s and %s ", 
			 	 						  correlationLimit, firstEventName, secondEventName);
		if (selectivity != null) {
			return baseString + String.format("(selectivity:%f)", selectivity);
		}
		return baseString + "(selectivity unknown)";
	}

	@Override
	public String getShortDescription() {
		String firstShortName = EventTypesManager.getInstance().getShortNameByLongName(firstEventName);
		String secondShortName = EventTypesManager.getInstance().getShortNameByLongName(secondEventName);
		return String.format("%s:%s:%.1f", firstShortName, secondShortName, correlationLimit);
	}
}
