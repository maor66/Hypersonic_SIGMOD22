package sase.user.speedd.fraud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.EventTypesManager;

public class CreditCardFraudEventTypesManager extends EventTypesManager {

	public static final String timestampAttributeName = "Timestamp";
	public static final String creditCardIDAttributeName = "ID";
	public static final String transactionSumAttributeName = "Transaction Sum";
	public static final String isFraudAttributeName = "Fraud";
	
	public static final int timestampAttributeIndex = 0;
	public static final int creditCardIDAttributeIndex = 1;
	public static final int transactionSumAttributeIndex = 2;
	public static final int isFraudAttributeIndex = 3;

	private static final int maxAttributeIndex = isFraudAttributeIndex;
	
	public static final int internalTimestampAttributeIndex = 0;
	public static final int internalCreditCardIDAttributeIndex = 4;
	public static final int internalTransactionSumAttributeIndex = 3;
	public static final int internalIsFraudAttributeIndex = 26;
	
	public static EventType verySmallTransactionNoFraudEventType;
	public static EventType smallTransactionNoFraudEventType;
	public static EventType mediumTransactionNoFraudEventType;
	public static EventType largeTransactionNoFraudEventType;
	public static EventType veryLargeTransactionNoFraudEventType;
	public static EventType verySmallTransactionFraudEventType;
	public static EventType smallTransactionFraudEventType;
	public static EventType mediumTransactionFraudEventType;
	public static EventType largeTransactionFraudEventType;
	public static EventType veryLargeTransactionFraudEventType;

	public static final String verySmallTransactionNoFraudEventTypeName = "VerySmallTransactionNoFraud";
	public static final String smallTransactionNoFraudEventTypeName = "SmallTransactionNoFraud";
	public static final String mediumTransactionNoFraudEventTypeName = "MediumTransactionNoFraud";
	public static final String largeTransactionNoFraudEventTypeName = "LargeTransactionNoFraud";
	public static final String veryLargeTransactionNoFraudEventTypeName = "VeryLargeTransactionNoFraud";
	public static final String verySmallTransactionFraudEventTypeName = "VerySmallTransactionFraud";
	public static final String smallTransactionFraudEventTypeName = "SmallTransactionFraud";
	public static final String mediumTransactionFraudEventTypeName = "MediumTransactionFraud";
	public static final String largeTransactionFraudEventTypeName = "LargeTransactionFraud";
	public static final String veryLargeTransactionFraudEventTypeName = "VeryLargeTransactionFraud";
	
	public CreditCardFraudEventTypesManager() {
	}

	@Override
	public String getEventLabel(Event event) {
		return (String)event.getAttributeValue(creditCardIDAttributeIndex);
	}

	@Override
	public Long getEventTimestamp(Event event) {
		return (Long)event.getAttributeValue(timestampAttributeIndex);
	}

	@Override
	public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
		Object[] newPayload = new Object[maxAttributeIndex+1];
		newPayload[timestampAttributeIndex] = Long.parseLong((String)payload[internalTimestampAttributeIndex]) / 1000000;
		newPayload[creditCardIDAttributeIndex] = payload[internalCreditCardIDAttributeIndex];
		newPayload[transactionSumAttributeIndex] = Double.parseDouble((String)payload[internalTransactionSumAttributeIndex]);
		newPayload[isFraudAttributeIndex] = Boolean.parseBoolean((String)payload[internalIsFraudAttributeIndex]);
		return newPayload;
	}

	@Override
	public long getAverageEventSize() {
		//long + string of constant length + double + boolean
		return 8 + 32 + 8 + 1;
	}

	@Override
	public List<EventType> getKnownEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(verySmallTransactionNoFraudEventType);
		result.add(smallTransactionNoFraudEventType);
		result.add(mediumTransactionNoFraudEventType);
		result.add(largeTransactionNoFraudEventType);
		result.add(veryLargeTransactionNoFraudEventType);
		result.add(verySmallTransactionFraudEventType);
		result.add(smallTransactionFraudEventType);
		result.add(mediumTransactionFraudEventType);
		result.add(largeTransactionFraudEventType);
		result.add(veryLargeTransactionFraudEventType);
		return result;
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		List<String> result = new ArrayList<String>();
		result.add(verySmallTransactionNoFraudEventTypeName);
		result.add(smallTransactionNoFraudEventTypeName);
		result.add(mediumTransactionNoFraudEventTypeName);
		result.add(largeTransactionNoFraudEventTypeName);
		result.add(veryLargeTransactionNoFraudEventTypeName);
		result.add(verySmallTransactionFraudEventTypeName);
		result.add(smallTransactionFraudEventTypeName);
		result.add(mediumTransactionFraudEventTypeName);
		result.add(largeTransactionFraudEventTypeName);
		result.add(veryLargeTransactionFraudEventTypeName);
		return result;
	}

	@Override
	protected void actuallyInitializeTypes() {
		Attribute[] attributes = new Attribute[maxAttributeIndex+1];
		attributes[timestampAttributeIndex] = new Attribute(Datatype.LONG, timestampAttributeName);
		attributes[creditCardIDAttributeIndex] = new Attribute(Datatype.TEXT, timestampAttributeName);
		attributes[transactionSumAttributeIndex] = new Attribute(Datatype.DOUBLE, timestampAttributeName);
		attributes[isFraudAttributeIndex] = new Attribute(Datatype.BOOLEAN, timestampAttributeName);

		verySmallTransactionNoFraudEventType = new EventType(verySmallTransactionNoFraudEventTypeName, attributes);
		smallTransactionNoFraudEventType = new EventType(smallTransactionNoFraudEventTypeName, attributes);
		mediumTransactionNoFraudEventType = new EventType(mediumTransactionNoFraudEventTypeName, attributes);
		largeTransactionNoFraudEventType = new EventType(largeTransactionNoFraudEventTypeName, attributes);
		veryLargeTransactionNoFraudEventType = new EventType(veryLargeTransactionNoFraudEventTypeName, attributes);
		
		verySmallTransactionFraudEventType = new EventType(verySmallTransactionFraudEventTypeName, attributes);
		smallTransactionFraudEventType = new EventType(smallTransactionFraudEventTypeName, attributes);
		mediumTransactionFraudEventType = new EventType(mediumTransactionFraudEventTypeName, attributes);
		largeTransactionFraudEventType = new EventType(largeTransactionFraudEventTypeName, attributes);
		veryLargeTransactionFraudEventType = new EventType(veryLargeTransactionFraudEventTypeName, attributes);
	}

	@Override
	protected HashMap<String, EventType> createNameToTypeHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		
		result.put(verySmallTransactionNoFraudEventTypeName, verySmallTransactionNoFraudEventType);
		result.put(smallTransactionNoFraudEventTypeName, smallTransactionNoFraudEventType);
		result.put(mediumTransactionNoFraudEventTypeName, mediumTransactionNoFraudEventType);
		result.put(largeTransactionNoFraudEventTypeName, largeTransactionNoFraudEventType);
		result.put(veryLargeTransactionNoFraudEventTypeName, veryLargeTransactionNoFraudEventType);

		result.put(verySmallTransactionFraudEventTypeName, verySmallTransactionFraudEventType);
		result.put(smallTransactionFraudEventTypeName, smallTransactionFraudEventType);
		result.put(mediumTransactionFraudEventTypeName, mediumTransactionFraudEventType);
		result.put(largeTransactionFraudEventTypeName, largeTransactionFraudEventType);
		result.put(veryLargeTransactionFraudEventTypeName, veryLargeTransactionFraudEventType);
		
		return result;
	}

	@Override
	protected HashMap<String, String> createLongNameToShortNameHash() {
		HashMap<String, String> result = new HashMap<String, String>();
		
		result.put(verySmallTransactionNoFraudEventTypeName, "SMALLEST|N");
		result.put(smallTransactionNoFraudEventTypeName, "SMALL|N");
		result.put(mediumTransactionNoFraudEventTypeName, "MEDIUM|N");
		result.put(largeTransactionNoFraudEventTypeName, "LARGE|N");
		result.put(veryLargeTransactionNoFraudEventTypeName, "LARGEST|N");
		result.put(verySmallTransactionFraudEventTypeName, "SMALLEST|Y");
		result.put(smallTransactionFraudEventTypeName, "SMALL|Y");
		result.put(mediumTransactionFraudEventTypeName, "MEDIUM|Y");
		result.put(largeTransactionFraudEventTypeName, "LARGE|Y");
		result.put(veryLargeTransactionFraudEventTypeName, "LARGEST|Y");	
		return result;
	}

	@Override
	public List<String> getAllFusedTypeNames() {
		throw new RuntimeException("Unimplemented");
	}

}
