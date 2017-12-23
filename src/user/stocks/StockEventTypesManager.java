package sase.user.stocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.pattern.EventTypesManager;

public class StockEventTypesManager extends EventTypesManager {

	public static final String labelAttributeName = "CompanyName";
	public static final String timestampAttributeName = "Timestamp";
	public static final int labelAttributeIndex = 0;
	public static final int timestampAttributeIndex = 1;
	public static final int firstStockMeasurementIndex = 2;
	public static final int numberOfStockMeasurementIndices = 20;

	//stock patterns by industry related names
	public static final String highTechCompanyEventTypeName = "HighTechCompany";
	public static final String financeCompanyEventTypeName = "FinanceCompany";
	public static final String megaLargeCompanyEventTypeName = "MegaLargeCompany";
	
	//stock patterns by industry related types
	public static EventType highTechCompanyEventType;
	public static EventType financeCompanyEventType;
	public static EventType megaLargeCompanyEventType;

	//stock patterns by region related names
	public static final String africanCompanyEventTypeName = "AfricanCompany";
	public static final String asianCompanyEventTypeName = "AsianCompany";
	public static final String australianCompanyEventTypeName = "AustralianCompany";
	public static final String northAmericanCompanyEventTypeName = "NorthAmericanCompany";
	public static final String centralAmericanCompanyEventTypeName = "CentralAmericanCompany";
	public static final String southAmericanCompanyEventTypeName = "SouthAmericanCompany";
	public static final String europeanCompanyEventTypeName = "EuropeanCompany";
	public static final String middleEasternCompanyEventTypeName = "MiddleEasternCompany";
	
	public static final String[] regionEventTypeNames = {
		africanCompanyEventTypeName,
		asianCompanyEventTypeName,
		australianCompanyEventTypeName,
		northAmericanCompanyEventTypeName,
		centralAmericanCompanyEventTypeName,
		southAmericanCompanyEventTypeName,
		europeanCompanyEventTypeName,
		middleEasternCompanyEventTypeName,
	};
	
	public static final String[] companiesByRegionEventTypeNames = new String[] {
		africanCompanyEventTypeName,
		asianCompanyEventTypeName,
		australianCompanyEventTypeName,
		northAmericanCompanyEventTypeName,
		centralAmericanCompanyEventTypeName,
		southAmericanCompanyEventTypeName,
		europeanCompanyEventTypeName,
		middleEasternCompanyEventTypeName
	};
	
	//stock patterns by region related types
	public static EventType africanCompanyEventType;
	public static EventType asianCompanyEventType;
	public static EventType australianCompanyEventType;
	public static EventType northAmericanCompanyEventType;
	public static EventType centralAmericanCompanyEventType;
	public static EventType southAmericanCompanyEventType;
	public static EventType europeanCompanyEventType;
	public static EventType middleEasternCompanyEventType;
	
	
	//types directly corresponding to specific companies
	public static final String microsoftEventTypeName = "MSFT";
	public static final String yahooEventTypeName = "YHOO";
	public static final String googleEventTypeName = "GOOG";
	public static final String appleEventTypeName = "AAPL";
	public static final String ciscoEventTypeName = "CSCO";
	public static final String intelEventTypeName = "INTC";
	public static final String illuminaEventTypeName = "ILMN";
	public static final String etradeEventTypeName = "ETFC";
	public static final String broadcomEventTypeName = "BRCM";
	public static final String rambusEventTypeName = "RMBS";
	
	public static final String[] companiesEventTypeNames = new String[] {
		microsoftEventTypeName,
		yahooEventTypeName,
		googleEventTypeName,
		appleEventTypeName,
		ciscoEventTypeName,
		intelEventTypeName,
		illuminaEventTypeName,
		etradeEventTypeName,
		broadcomEventTypeName,
		rambusEventTypeName
	};

	public static EventType microsoftEventType;
	public static EventType yahooEventType;
	public static EventType googleEventType;
	public static EventType appleEventType;
	public static EventType ciscoEventType;
	public static EventType intelEventType;
	public static EventType illuminaEventType;
	public static EventType etradeEventType;
	public static EventType broadcomEventType;
	public static EventType rambusEventType;
	
	public StockEventTypesManager() {
	}
	
	@Override
	protected HashMap<String, EventType> createNameToTypeHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		
		result.put(highTechCompanyEventTypeName, highTechCompanyEventType);
		result.put(financeCompanyEventTypeName, financeCompanyEventType);
		result.put(megaLargeCompanyEventTypeName, megaLargeCompanyEventType);
		
		result.put(africanCompanyEventTypeName, africanCompanyEventType);
		result.put(asianCompanyEventTypeName, asianCompanyEventType);
		result.put(australianCompanyEventTypeName, australianCompanyEventType);
		result.put(northAmericanCompanyEventTypeName, northAmericanCompanyEventType);
		result.put(centralAmericanCompanyEventTypeName, centralAmericanCompanyEventType);
		result.put(southAmericanCompanyEventTypeName, southAmericanCompanyEventType);
		result.put(europeanCompanyEventTypeName, europeanCompanyEventType);
		result.put(middleEasternCompanyEventTypeName, middleEasternCompanyEventType);
		
		result.put(microsoftEventTypeName, microsoftEventType);
		result.put(yahooEventTypeName, yahooEventType);
		result.put(googleEventTypeName, googleEventType);
		result.put(appleEventTypeName, appleEventType);
		result.put(ciscoEventTypeName, ciscoEventType);
		result.put(intelEventTypeName, intelEventType);
		result.put(illuminaEventTypeName, illuminaEventType);
		result.put(etradeEventTypeName, etradeEventType);
		result.put(broadcomEventTypeName, broadcomEventType);
		result.put(rambusEventTypeName, rambusEventType);
		
		return result;
	}
	
	@Override
	protected HashMap<String, String> createLongNameToShortNameHash() {
		HashMap<String, String> result = new HashMap<String, String>();
		
		result.put(highTechCompanyEventTypeName, "");
		result.put(financeCompanyEventTypeName, "");
		result.put(megaLargeCompanyEventTypeName, "");
		
		result.put(africanCompanyEventTypeName, "AFR");
		result.put(asianCompanyEventTypeName, "AS");
		result.put(australianCompanyEventTypeName, "AU");
		result.put(northAmericanCompanyEventTypeName, "NA");
		result.put(centralAmericanCompanyEventTypeName, "CA");
		result.put(southAmericanCompanyEventTypeName, "SA");
		result.put(europeanCompanyEventTypeName, "EU");
		result.put(middleEasternCompanyEventTypeName, "ME");
		
		for (String name : companiesEventTypeNames) {
			result.put(name, name);
		}
		
		return result;
	}
	
	@Override
	protected void actuallyInitializeTypes() {
		Attribute[] attributes = new Attribute[MainConfig.historyLength + 2];
		attributes[0] = new Attribute(Datatype.TEXT, labelAttributeName);
		attributes[1] = new Attribute(Datatype.LONG, timestampAttributeName);
		for (int i = 2; i < attributes.length; ++i) {
			attributes[i] = new Attribute(Datatype.DOUBLE, String.format("StockPrice_%d", i-2));
		}
		
		highTechCompanyEventType = new EventType(highTechCompanyEventTypeName, attributes);
		financeCompanyEventType = new EventType(financeCompanyEventTypeName, attributes);
		megaLargeCompanyEventType = new EventType(megaLargeCompanyEventTypeName, attributes);

		africanCompanyEventType = new EventType(africanCompanyEventTypeName, attributes);
		asianCompanyEventType = new EventType(asianCompanyEventTypeName, attributes);
		australianCompanyEventType = new EventType(australianCompanyEventTypeName, attributes);
		northAmericanCompanyEventType = new EventType(northAmericanCompanyEventTypeName, attributes);
		centralAmericanCompanyEventType = new EventType(centralAmericanCompanyEventTypeName, attributes);
		southAmericanCompanyEventType = new EventType(southAmericanCompanyEventTypeName, attributes);
		europeanCompanyEventType = new EventType(europeanCompanyEventTypeName, attributes);
		middleEasternCompanyEventType = new EventType(middleEasternCompanyEventTypeName, attributes);

		microsoftEventType = new EventType(microsoftEventTypeName, attributes);
		yahooEventType = new EventType(yahooEventTypeName, attributes);
		googleEventType = new EventType(googleEventTypeName, attributes);
		appleEventType = new EventType(appleEventTypeName, attributes);
		ciscoEventType = new EventType(ciscoEventTypeName, attributes);
		intelEventType = new EventType(intelEventTypeName, attributes);
		illuminaEventType = new EventType(illuminaEventTypeName, attributes);
		etradeEventType = new EventType(etradeEventTypeName, attributes);
		broadcomEventType = new EventType(broadcomEventTypeName, attributes);
		rambusEventType = new EventType(rambusEventTypeName, attributes);
	}

	@Override
	public String getEventLabel(Event event) {
		return (String)event.getAttributeValue(labelAttributeIndex);
	}
	
	@Override
	public Long getEventTimestamp(Event event) {
		return (Long)event.getAttributeValue(timestampAttributeIndex);
	}
	
	@Override
	public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
		Object[] newPayload = new Object[payload.length];
		newPayload[labelAttributeIndex] = payload[labelAttributeIndex];
		newPayload[timestampAttributeIndex] = Long.valueOf((String)payload[timestampAttributeIndex]);
		for (int i = firstStockMeasurementIndex; i < payload.length; ++i) {
			if (payload[i] instanceof Double) {
				newPayload[i] = payload[i];
			}
			else {
				newPayload[i] = Double.valueOf((String)payload[i]);
			}
		}
		return newPayload;
	}
	
	@Override
	public long getAverageEventSize() {
		//stock identifier is of 4 characters on average (2 bytes each), timestamp is 8 bytes (long) and 
		//each measurement is a double (8 bytes)
		return 2*4 + 8 + 8 * numberOfStockMeasurementIndices;
	}
	
	@Override
	public List<EventType> getKnownEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		/*
		result.add(africanCompanyEventType);
		result.add(asianCompanyEventType);
		result.add(australianCompanyEventType);
		result.add(centralAmericanCompanyEventType);
		result.add(europeanCompanyEventType);
		result.add(middleEasternCompanyEventType);
		result.add(northAmericanCompanyEventType);
		result.add(southAmericanCompanyEventType);
		*/
		result.add(microsoftEventType);
		result.add(yahooEventType);
		result.add(googleEventType);
		result.add(appleEventType);
		result.add(ciscoEventType);
		result.add(intelEventType);
		result.add(illuminaEventType);
		result.add(etradeEventType);
		result.add(broadcomEventType);
		result.add(rambusEventType);
		return result;
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		List<String> result = new ArrayList<String>();
		result.add(microsoftEventTypeName);
		result.add(yahooEventTypeName);
		result.add(googleEventTypeName);
		result.add(appleEventTypeName);
		result.add(ciscoEventTypeName);
		result.add(intelEventTypeName);
		result.add(illuminaEventTypeName);
		result.add(etradeEventTypeName);
		result.add(broadcomEventTypeName);
		result.add(rambusEventTypeName);
		return result;
	}
}
