package user.speedd.fraud;

import base.EventType;
import input.EventTypesConverter;
import specification.SimulationSpecification;

public class CreditCardFraudEventTypesConverter extends EventTypesConverter {

	private static final Double veryLargeSumThreshold = 100.0;
	private static final Double largeSumThreshold = 80.0;
	private static final Double mediumSumThreshold = 60.0;
	private static final Double smallSumThreshold = 40.0;

	public CreditCardFraudEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}
	
	private EventType getNoFraudEventTypeByTransactionSum(Double transactionSum) {
		int numberOfEventTypes = patternSpecification.getNumberOfEventTypes();
		if (transactionSum >= veryLargeSumThreshold) {
			return CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventType;
		}
		if (transactionSum >= largeSumThreshold) {
			return numberOfEventTypes < 5 ? (numberOfEventTypes < 4 ? 
														CreditCardFraudEventTypesManager.smallTransactionNoFraudEventType : 
														CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventType) : 
											CreditCardFraudEventTypesManager.largeTransactionNoFraudEventType;
		}
		if (transactionSum >= mediumSumThreshold) {
			return numberOfEventTypes < 4 ? CreditCardFraudEventTypesManager.smallTransactionNoFraudEventType : 
											CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventType;
		}
		if (transactionSum >= smallSumThreshold) {
			return CreditCardFraudEventTypesManager.smallTransactionNoFraudEventType;
		}
		return CreditCardFraudEventTypesManager.verySmallTransactionNoFraudEventType;
	}

	private EventType convertFraudToNoFraudEventType(EventType eventType) {
		if (eventType == CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventType) {
			return CreditCardFraudEventTypesManager.veryLargeTransactionFraudEventType;
		}
		if (eventType == CreditCardFraudEventTypesManager.largeTransactionNoFraudEventType) {
			return CreditCardFraudEventTypesManager.largeTransactionFraudEventType;
		}
		if (eventType == CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventType) {
			return CreditCardFraudEventTypesManager.mediumTransactionFraudEventType;
		}
		if (eventType == CreditCardFraudEventTypesManager.smallTransactionNoFraudEventType) {
			return CreditCardFraudEventTypesManager.smallTransactionFraudEventType;
		}
		return CreditCardFraudEventTypesManager.verySmallTransactionFraudEventType;
	}
	
	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		Double transactionSum = Double.parseDouble(rawEvent[CreditCardFraudEventTypesManager.internalTransactionSumAttributeIndex]);
		Integer fraudIndex = Integer.parseInt(rawEvent[CreditCardFraudEventTypesManager.internalIsFraudAttributeIndex]);
		boolean isFraud = (fraudIndex > 0);
		EventType eventType = getNoFraudEventTypeByTransactionSum(transactionSum);
		return isFraud ? convertFraudToNoFraudEventType(eventType) : eventType;
	}

}
