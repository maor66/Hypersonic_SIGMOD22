package user.speedd.fraud;

import base.Event;
import base.EventType;
import pattern.condition.base.DoubleEventCondition;

public class SameCreditCardIDCondition extends DoubleEventCondition {

	public SameCreditCardIDCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(firstType, secondType, selectivity);
	}

	public SameCreditCardIDCondition(EventType firstType, EventType secondType) {
		super(firstType, secondType);
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
		String firstID = (String) firstEvent.getAttributeValue(CreditCardFraudEventTypesManager.creditCardIDAttributeIndex);
		String secondID = (String) secondEvent.getAttributeValue(CreditCardFraudEventTypesManager.creditCardIDAttributeIndex);
		return (firstID.equals(secondID));
	}

	@Override
	protected String getConditionKey() {
		return "Unsupported";
	}

}
