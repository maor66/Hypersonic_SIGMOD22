package user.stocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.Attribute;
import base.Datatype;
import base.Event;
import base.EventType;
import config.MainConfig;
import pattern.EventTypesManager;

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
	
	//stock patterns by region related types
	public static EventType africanCompanyEventType;
	public static EventType asianCompanyEventType;
	public static EventType australianCompanyEventType;
	public static EventType northAmericanCompanyEventType;
	public static EventType centralAmericanCompanyEventType;
	public static EventType southAmericanCompanyEventType;
	public static EventType europeanCompanyEventType;
	public static EventType middleEasternCompanyEventType;
	
	
	//types directly corresponding to specific large companies
	public static final String microsoftEventTypeName = "MSFT";
	public static final String yahooEventTypeName = "YHOO";
	public static final String googleEventTypeName = "GOOG";
	public static final String appleEventTypeName = "AAPL";
	public static final String ciscoEventTypeName = "CSCO";
	public static final String intelEventTypeName = "INTC";
	
	public static final String[] largeCompaniesEventTypeNames = new String[] {
		microsoftEventTypeName,
		yahooEventTypeName,
		googleEventTypeName,
		appleEventTypeName,
		ciscoEventTypeName,
		intelEventTypeName
	};

	public static EventType microsoftEventType;
	public static EventType yahooEventType;
	public static EventType googleEventType;
	public static EventType appleEventType;
	public static EventType ciscoEventType;
	public static EventType intelEventType;


	public static final String crosstechEventTypeName = "XTEX";
	public static final String xtlbioEventTypeName = "XTLB";
	public static final String quantumEventTypeName = "QTWW";
	public static final String juniperEventTypeName = "CBRX";
	public static final String aepindustriesEventTypeName = "AEPI";
	public static final String reprosEventTypeName = "RPRX";
	
	public static final String[] mediumCompaniesEventTypeNames = new String[] {
		crosstechEventTypeName,
		xtlbioEventTypeName,
		quantumEventTypeName,
		juniperEventTypeName,
		aepindustriesEventTypeName,
		reprosEventTypeName,
	};
	
	public static EventType crosstechEventType;
	public static EventType xtlbioEventType;
	public static EventType quantumEventType;
	public static EventType juniperEventType;
	public static EventType aepindustriesEventType;
	public static EventType reprosEventType;
	
	
	public static final String townebankEventTypeName = "TOWN";
	public static final String mindctiEventTypeName = "MNDO";
	public static final String chelseaEventTypeName = "CHTP";
	public static final String nymoxEventTypeName = "NYMX";
	public static final String purecycleEventTypeName = "PCYO";
	public static final String netlistEventTypeName = "NLST";
	
	public static final String[] smallCompaniesEventTypeNames = new String[] {
		townebankEventTypeName,
		mindctiEventTypeName,
		chelseaEventTypeName,
		nymoxEventTypeName,
		purecycleEventTypeName,
		netlistEventTypeName
	};
	
	public static EventType townebankEventType;
	public static EventType mindctiEventType;
	public static EventType chelseaEventType;
	public static EventType nymoxEventType;
	public static EventType purecycleEventType;
	public static EventType netlistEventType;
	
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
		result.put(crosstechEventTypeName, crosstechEventType);
		result.put(xtlbioEventTypeName, xtlbioEventType);
		result.put(quantumEventTypeName, quantumEventType);
		result.put(juniperEventTypeName, juniperEventType);
		result.put(aepindustriesEventTypeName, aepindustriesEventType);
		result.put(reprosEventTypeName, reprosEventType);
		result.put(townebankEventTypeName, townebankEventType);
		result.put(mindctiEventTypeName, mindctiEventType);
		result.put(chelseaEventTypeName, chelseaEventType);
		result.put(nymoxEventTypeName, nymoxEventType);
		result.put(purecycleEventTypeName, purecycleEventType);
		result.put(netlistEventTypeName, netlistEventType);
				
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
		
		for (String name : largeCompaniesEventTypeNames) {
			result.put(name, name);
		}
		for (String name : mediumCompaniesEventTypeNames) {
			result.put(name, name);
		}
		for (String name : smallCompaniesEventTypeNames) {
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
		crosstechEventType = new EventType(crosstechEventTypeName, attributes);
		xtlbioEventType = new EventType(xtlbioEventTypeName, attributes);
		quantumEventType = new EventType(quantumEventTypeName, attributes);
		juniperEventType = new EventType(juniperEventTypeName, attributes);
		aepindustriesEventType = new EventType(aepindustriesEventTypeName, attributes);
		reprosEventType = new EventType(reprosEventTypeName, attributes);
		townebankEventType = new EventType(townebankEventTypeName, attributes);
		mindctiEventType = new EventType(mindctiEventTypeName, attributes);
		chelseaEventType = new EventType(chelseaEventTypeName, attributes);
		nymoxEventType = new EventType(nymoxEventTypeName, attributes);
		purecycleEventType = new EventType(purecycleEventTypeName, attributes);
		netlistEventType = new EventType(netlistEventTypeName, attributes);
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
		result.add(crosstechEventType);
		result.add(xtlbioEventType);
		result.add(quantumEventType);
		result.add(juniperEventType);
		result.add(aepindustriesEventType);
		result.add(reprosEventType);
		result.add(townebankEventType);
		result.add(mindctiEventType);
		result.add(chelseaEventType);
		result.add(nymoxEventType);
		result.add(purecycleEventType);
		result.add(netlistEventType);
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
		result.add(crosstechEventTypeName);
		result.add(xtlbioEventTypeName);
		result.add(quantumEventTypeName);
		result.add(juniperEventTypeName);
		result.add(aepindustriesEventTypeName);
		result.add(reprosEventTypeName);
		result.add(townebankEventTypeName);
		result.add(mindctiEventTypeName);
		result.add(chelseaEventTypeName);
		result.add(nymoxEventTypeName);
		result.add(purecycleEventTypeName);
		result.add(netlistEventTypeName);
		return result;
	}
}
