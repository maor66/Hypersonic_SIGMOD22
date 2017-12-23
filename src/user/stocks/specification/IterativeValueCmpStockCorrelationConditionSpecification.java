package user.stocks.specification;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import pattern.EventTypesManager;
import pattern.condition.base.AtomicCondition;
import pattern.condition.iteration.FirstValueExternalCondition;
import pattern.condition.iteration.eager.IteratedIncrementalDoubleEventCondition;
import specification.ConditionSpecification;
import user.stocks.condition.StockFirstValueCmpCondition;
import user.stocks.condition.StockFirstValueCmpCondition.ComparisonOperation;

public class IterativeValueCmpStockCorrelationConditionSpecification extends ConditionSpecification {
	
	private final String precedingEventName;
	private final String iterativeEventName;
	private final String succeedingEventName;

	public IterativeValueCmpStockCorrelationConditionSpecification(String precedingEventName,
																  		String iterativeEventName,
																  		String succeedingEventName) {
		this.precedingEventName = precedingEventName;
		this.iterativeEventName = iterativeEventName;
		this.succeedingEventName = succeedingEventName;
	}

	public String getPrecedingEventName() {
		return precedingEventName;
	}

	public String getIterativeEventName() {
		return iterativeEventName;
	}

	public String getSucceedingEventName() {
		return succeedingEventName;
	}
	
	public List<AtomicCondition> createConditions() {
		EventType precedingType = EventTypesManager.getInstance().getTypeByName(precedingEventName);
		EventType iterativeType = EventTypesManager.getInstance().getTypeByName(iterativeEventName);
		EventType succeedingType = EventTypesManager.getInstance().getTypeByName(succeedingEventName);
		StockFirstValueCmpCondition nestedTriggeringEqualityCondition = 
									new StockFirstValueCmpCondition(precedingType, iterativeType,
																	ComparisonOperation.EQUALS);
		FirstValueExternalCondition triggeringEqualityCondition = 
									new FirstValueExternalCondition(nestedTriggeringEqualityCondition);
		StockFirstValueCmpCondition nestedIterativeEqualityCondition = 
									new StockFirstValueCmpCondition(iterativeType, iterativeType,
																	ComparisonOperation.EQUALS);
		IteratedIncrementalDoubleEventCondition iterativeEqualityCondition = 
									new IteratedIncrementalDoubleEventCondition(nestedIterativeEqualityCondition);
		StockFirstValueCmpCondition externalEqualityCondition = 
									new StockFirstValueCmpCondition(precedingType, succeedingType,
																	ComparisonOperation.EQUALS);
		
		List<AtomicCondition> result = new ArrayList<AtomicCondition>();
		result.add(triggeringEqualityCondition);
		result.add(iterativeEqualityCondition);
		result.add(externalEqualityCondition);
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("%s => (%s)* => %s", precedingEventName, iterativeEventName, succeedingEventName);
	}

	@Override
	public String getShortDescription() {
		return "Unsupported";
	}

}
