package sase.user.stocks.condition;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.data_parallel.DataParallelEvaluationMechanism;
import sase.pattern.condition.base.DoubleEventCondition;

public class StockSameCompanyNameFirstLetterCondition extends DoubleEventCondition {

	public StockSameCompanyNameFirstLetterCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}
	
	public StockSameCompanyNameFirstLetterCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		String firstName = firstEvent.getAttributeValue("CompanyName").toString();
		String secondName = secondEvent.getAttributeValue("CompanyName").toString();
		String firstLetter = firstName.substring(0, 1);
		String secondLetter = secondName.substring(0, 1);
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return firstLetter.equals(secondLetter);
	}
	
	@Override
	public String toString() {
		return String.format("Comparison of name values of %s and %s", 
							 eventTypes.get(0).getName(), eventTypes.get(1).getName());
	}

}
