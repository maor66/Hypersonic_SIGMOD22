package sase.user.stocks;

import java.util.ArrayList;
import java.util.Collection;
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
	public static final int SecondTypeFusedFirstStockMeasurementIndex = firstStockMeasurementIndex + numberOfStockMeasurementIndices;
	public static final int fusedFirstTypeTimestampIndex = SecondTypeFusedFirstStockMeasurementIndex + numberOfStockMeasurementIndices;


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
	public static final String biduEventTypeName = "BIDU";
	public static final String etfcEventTypeName = "ETFC";
	public static final String fslrEventTypeName = "FSLR";
	public static final String nvdaEventTypeName = "NVDA";
	public static final String amznEventTypeName = "AMZN";
	public static final String siriEventTypeName = "SIRI";
	public static final String brcmEventTypeName = "BRCM";
	public static final String amatEventTypeName = "AMAT";
	public static final String croxEventTypeName = "CROX";
	public static final String drysEventTypeName = "DRYS";
	public static final String qcomEventTypeName = "QCOM";
	public static final String spwrEventTypeName = "SPWR";
	public static final String sndkEventTypeName = "SNDK";
	public static final String amgnEventTypeName = "AMGN";
	public static final String ebayEventTypeName = "EBAY";
	
	public static final String[] largeCompaniesEventTypeNames = new String[] {
		microsoftEventTypeName,
		yahooEventTypeName,
		googleEventTypeName,
		appleEventTypeName,
		ciscoEventTypeName,
		intelEventTypeName,
		biduEventTypeName,
		etfcEventTypeName,
		fslrEventTypeName,
		nvdaEventTypeName,
		amznEventTypeName, 
		siriEventTypeName,
		brcmEventTypeName,
		amatEventTypeName,
		croxEventTypeName,
		drysEventTypeName,
		qcomEventTypeName,
		spwrEventTypeName,
		sndkEventTypeName,
		amgnEventTypeName,
		ebayEventTypeName,
	};

	public static EventType microsoftEventType;
	public static EventType yahooEventType;
	public static EventType googleEventType;
	public static EventType appleEventType;
	public static EventType ciscoEventType;
	public static EventType intelEventType;
	public static EventType biduEventType;
	public static EventType etfcEventType;
	public static EventType fslrEventType;
	public static EventType nvdaEventType;
	public static EventType amznEventType;
	public static EventType siriEventType;
	public static EventType brcmEventType;
	public static EventType amatEventType;
	public static EventType croxEventType;
	public static EventType drysEventType;
	public static EventType qcomEventType;
	public static EventType spwrEventType;
	public static EventType sndkEventType;
	public static EventType amgnEventType;
	public static EventType ebayEventType;


	public static final String xtexEventTypeName = "XTEX";
	public static final String xtlbEventTypeName = "XTLB";
	public static final String qtwwEventTypeName = "QTWW";
	public static final String cbrxEventTypeName = "CBRX";
	public static final String aepiEventTypeName = "AEPI";
	public static final String rprxEventTypeName = "RPRX";
	public static final String abcbEventTypeName = "ABCB";
	public static final String prcpEventTypeName = "PRCP";
	public static final String evepEventTypeName = "EVEP";
	public static final String mosyEventTypeName = "MOSY";
	public static final String bkccEventTypeName = "BKCC";
	public static final String bsetEventTypeName = "BSET";
	public static final String sconEventTypeName = "SCON";
	public static final String fsrvEventTypeName = "FSRV";
	public static final String wsfsEventTypeName = "WSFS";
	public static final String redfEventTypeName = "REDF";
	public static final String ccixEventTypeName = "CCIX";
	public static final String cweiEventTypeName = "CWEI";
	public static final String chdnEventTypeName = "CHDN";
	public static final String stsiEventTypeName = "STSI";
	public static final String mrgeEventTypeName = "MRGE";
	
	public static final String[] mediumCompaniesEventTypeNames = new String[] {
		xtexEventTypeName,
		xtlbEventTypeName,
		qtwwEventTypeName,
		cbrxEventTypeName,
		aepiEventTypeName,
		rprxEventTypeName,
		abcbEventTypeName,
		prcpEventTypeName,
		evepEventTypeName,
		mosyEventTypeName,
		bkccEventTypeName,
		bsetEventTypeName,
		sconEventTypeName,
		fsrvEventTypeName,
		wsfsEventTypeName,
		redfEventTypeName,
		ccixEventTypeName,
		cweiEventTypeName,
		chdnEventTypeName,
		stsiEventTypeName,
		mrgeEventTypeName,
	};
	
	public static EventType xtexEventType;
	public static EventType xtlbEventType;
	public static EventType qtwwEventType;
	public static EventType xbrzEventType;
	public static EventType aepiEventType;
	public static EventType rprxEventType;
	public static EventType abcbEventType;
	public static EventType prcpEventType;
	public static EventType evepEventType;
	public static EventType mosyEventType;
	public static EventType bkccEventType;
	public static EventType bsetEventType;
	public static EventType sconEventType;
	public static EventType fsrvEventType;
	public static EventType wsfsEventType;
	public static EventType redfEventType;
	public static EventType ccixEventType;
	public static EventType cweiEventType;
	public static EventType chdnEventType;
	public static EventType stsiEventType;
	public static EventType mrgeEventType;
	
	
	public static final String townEventTypeName = "TOWN";
	public static final String mndoEventTypeName = "MNDO";
	public static final String chtpEventTypeName = "CHTP";
	public static final String nymxEventTypeName = "NYMX";
	public static final String pcyoEventTypeName = "PCYO";
	public static final String nlstEventTypeName = "NLST";
	public static final String nbbcEventTypeName = "NBBC";
	public static final String clwtEventTypeName = "CLWT";
	public static final String pmfgEventTypeName = "PMFG";
	public static final String arowEventTypeName = "AROW";
	public static final String isigEventTypeName = "ISIG";
	public static final String qbakEventTypeName = "QBAK";
	public static final String nrimEventTypeName = "NRIM";
	public static final String joezEventTypeName = "JOEZ";
	public static final String adrdEventTypeName = "ADRD";
	public static final String ffnwEventTypeName = "FFNW";
	public static final String symxEventTypeName = "SYMX";
	public static final String adruEventTypeName = "ADRU";
	public static final String ptekEventTypeName = "PTEK";
	public static final String kirkEventTypeName = "KIRK";
	public static final String nathEventTypeName = "NATH";
	
	public static final String[] smallCompaniesEventTypeNames = new String[] {
		townEventTypeName,
		mndoEventTypeName,
		chtpEventTypeName,
		nymxEventTypeName,
		pcyoEventTypeName,
		nlstEventTypeName,
		nbbcEventTypeName,
		clwtEventTypeName,
		pmfgEventTypeName,
		arowEventTypeName,
		isigEventTypeName,
		qbakEventTypeName,
		nrimEventTypeName,
		joezEventTypeName,
		adrdEventTypeName,
		ffnwEventTypeName,
		symxEventTypeName,
		adruEventTypeName,
		ptekEventTypeName,
		kirkEventTypeName,
		nathEventTypeName,
	};
	
	public static EventType townEventType;
	public static EventType mndoEventType;
	public static EventType chtpEventType;
	public static EventType nymxEventType;
	public static EventType pcyoEventType;
	public static EventType nlstEventType;
	public static EventType nbbcEventType;
	public static EventType clwtEventType;
	public static EventType pmfgEventType;
	public static EventType arowEventType;
	public static EventType isigEventType;
	public static EventType qbakEventType;
	public static EventType nrimEventType;
	public static EventType joezEventType;
	public static EventType adrdEventType;
	public static EventType ffnwEventType;
	public static EventType symxEventType;
	public static EventType adruEventType;
	public static EventType ptekEventType;
	public static EventType kirkEventType;
	public static EventType nathEventType;
	public static List<EventType> fusedEventTypes = new ArrayList<>();

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
		result.put(biduEventTypeName, biduEventType);
		result.put(etfcEventTypeName, etfcEventType);
		result.put(fslrEventTypeName, fslrEventType);
		result.put(nvdaEventTypeName, nvdaEventType);
		result.put(amznEventTypeName, amznEventType);
		result.put(siriEventTypeName, siriEventType);
		result.put(brcmEventTypeName, brcmEventType);
		result.put(amatEventTypeName, amatEventType);
		result.put(croxEventTypeName, croxEventType);
		result.put(drysEventTypeName, drysEventType);
		result.put(qcomEventTypeName, qcomEventType);
		result.put(spwrEventTypeName, spwrEventType);
		result.put(sndkEventTypeName, sndkEventType);
		result.put(amgnEventTypeName, amgnEventType);
		result.put(ebayEventTypeName, ebayEventType);
		
		result.put(xtexEventTypeName, xtexEventType);
		result.put(xtlbEventTypeName, xtlbEventType);
		result.put(qtwwEventTypeName, qtwwEventType);
		result.put(cbrxEventTypeName, xbrzEventType);
		result.put(aepiEventTypeName, aepiEventType);
		result.put(rprxEventTypeName, rprxEventType);
		result.put(abcbEventTypeName, abcbEventType);
		result.put(prcpEventTypeName, prcpEventType);
		result.put(evepEventTypeName, evepEventType);
		result.put(mosyEventTypeName, mosyEventType);
		result.put(bkccEventTypeName, bkccEventType);
		result.put(bsetEventTypeName, bsetEventType);
		result.put(sconEventTypeName, sconEventType);
		result.put(fsrvEventTypeName, fsrvEventType);
		result.put(wsfsEventTypeName, wsfsEventType);
		result.put(redfEventTypeName, redfEventType);
		result.put(ccixEventTypeName, ccixEventType);
		result.put(cweiEventTypeName, cweiEventType);
		result.put(chdnEventTypeName, chdnEventType);
		result.put(stsiEventTypeName, stsiEventType);
		result.put(mrgeEventTypeName, mrgeEventType);
		
		result.put(townEventTypeName, townEventType);
		result.put(mndoEventTypeName, mndoEventType);
		result.put(chtpEventTypeName, chtpEventType);
		result.put(nymxEventTypeName, nymxEventType);
		result.put(pcyoEventTypeName, pcyoEventType);
		result.put(nlstEventTypeName, nlstEventType);
		result.put(nbbcEventTypeName, nbbcEventType);
		result.put(clwtEventTypeName, clwtEventType);
		result.put(pmfgEventTypeName, pmfgEventType);
		result.put(arowEventTypeName, arowEventType);
		result.put(isigEventTypeName, isigEventType);
		result.put(qbakEventTypeName, qbakEventType);
		result.put(nrimEventTypeName, nrimEventType);
		result.put(joezEventTypeName, joezEventType);
		result.put(adrdEventTypeName, adrdEventType);
		result.put(ffnwEventTypeName, ffnwEventType);
		result.put(symxEventTypeName, symxEventType);
		result.put(adruEventTypeName, adruEventType);
		result.put(ptekEventTypeName, ptekEventType);
		result.put(kirkEventTypeName, kirkEventType);
		result.put(nathEventTypeName, nathEventType);

		getAllFusedTypeNames().forEach(fusedEventTypeName -> {
			EventType fusedEventType = createNewFusedEvent(fusedEventTypeName);
			fusedEventTypes.add(fusedEventType);
			result.put(fusedEventTypeName, fusedEventType);
		});
		return result;
	}

	private List<EventType> getAllFusedTypes()
	{
//		ArrayList<EventType> eventTypes = new ArrayList<>();
//		getAllFusedTypeNames().forEach(fusedEventTypeName -> eventTypes.add(createNewFusedEvent(fusedEventTypeName)));
		return fusedEventTypes;
	}

	private EventType createNewFusedEvent(String fusedEventTypeName) {
		return new EventType(fusedEventTypeName, getFusedAttributes());
	}

	private Attribute[] getFusedAttributes() {
		Attribute[] attributes = new Attribute[MainConfig.fusedHistoryLength + 2 + 1];
		attributes[0] = new Attribute(Datatype.TEXT, labelAttributeName);
		attributes[1] = new Attribute(Datatype.LONG, timestampAttributeName);
		for (int i = 2; i < attributes.length; ++i) {
			attributes[i] = new Attribute(Datatype.DOUBLE, String.format("StockPrice_%d", i-2));
		}
		attributes[fusedFirstTypeTimestampIndex] = new Attribute(Datatype.LONG, "Fused " + timestampAttributeName);
		return attributes;
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
		biduEventType = new EventType(biduEventTypeName, attributes);
		etfcEventType = new EventType(etfcEventTypeName, attributes);
		fslrEventType = new EventType(fslrEventTypeName, attributes);
		nvdaEventType = new EventType(nvdaEventTypeName, attributes);
		amznEventType = new EventType(amznEventTypeName, attributes);
		siriEventType = new EventType(siriEventTypeName, attributes);
		brcmEventType = new EventType(brcmEventTypeName, attributes);
		amatEventType = new EventType(amatEventTypeName, attributes);
		croxEventType = new EventType(croxEventTypeName, attributes);
		drysEventType = new EventType(drysEventTypeName, attributes);
		qcomEventType = new EventType(qcomEventTypeName, attributes);
		spwrEventType = new EventType(spwrEventTypeName, attributes);
		sndkEventType = new EventType(sndkEventTypeName, attributes);
		amgnEventType = new EventType(amgnEventTypeName, attributes);
		ebayEventType = new EventType(ebayEventTypeName, attributes);
		
		xtexEventType = new EventType(xtexEventTypeName, attributes);
		xtlbEventType = new EventType(xtlbEventTypeName, attributes);
		qtwwEventType = new EventType(qtwwEventTypeName, attributes);
		xbrzEventType = new EventType(cbrxEventTypeName, attributes);
		aepiEventType = new EventType(aepiEventTypeName, attributes);
		rprxEventType = new EventType(rprxEventTypeName, attributes);
		abcbEventType = new EventType(abcbEventTypeName, attributes);
		prcpEventType = new EventType(prcpEventTypeName, attributes);
		evepEventType = new EventType(evepEventTypeName, attributes);
		mosyEventType = new EventType(mosyEventTypeName, attributes);
		bkccEventType = new EventType(bkccEventTypeName, attributes);
		bsetEventType = new EventType(bsetEventTypeName, attributes);
		sconEventType = new EventType(sconEventTypeName, attributes);
		fsrvEventType = new EventType(fsrvEventTypeName, attributes);
		wsfsEventType = new EventType(wsfsEventTypeName, attributes);
		redfEventType = new EventType(redfEventTypeName, attributes);
		ccixEventType = new EventType(ccixEventTypeName, attributes);
		cweiEventType = new EventType(cweiEventTypeName, attributes);
		chdnEventType = new EventType(chdnEventTypeName, attributes);
		stsiEventType = new EventType(stsiEventTypeName, attributes);
		mrgeEventType = new EventType(mrgeEventTypeName, attributes);

		townEventType = new EventType(townEventTypeName, attributes);
		mndoEventType = new EventType(mndoEventTypeName, attributes);
		chtpEventType = new EventType(chtpEventTypeName, attributes);
		nymxEventType = new EventType(nymxEventTypeName, attributes);
		pcyoEventType = new EventType(pcyoEventTypeName, attributes);
		nlstEventType = new EventType(nlstEventTypeName, attributes);
		nbbcEventType = new EventType(nbbcEventTypeName, attributes);
		clwtEventType = new EventType(clwtEventTypeName, attributes);
		pmfgEventType = new EventType(pmfgEventTypeName, attributes);
		arowEventType = new EventType(arowEventTypeName, attributes);
		isigEventType = new EventType(isigEventTypeName, attributes);
		qbakEventType = new EventType(qbakEventTypeName, attributes);
		nrimEventType = new EventType(nrimEventTypeName, attributes);
		joezEventType = new EventType(joezEventTypeName, attributes);
		adrdEventType = new EventType(adrdEventTypeName, attributes);
		ffnwEventType = new EventType(ffnwEventTypeName, attributes);
		symxEventType = new EventType(symxEventTypeName, attributes);
		adruEventType = new EventType(adruEventTypeName, attributes);
		ptekEventType = new EventType(ptekEventTypeName, attributes);
		kirkEventType = new EventType(kirkEventTypeName, attributes);
		nathEventType = new EventType(nathEventTypeName, attributes);
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
		if (payload.length == fusedFirstTypeTimestampIndex + 1) {
			newPayload[fusedFirstTypeTimestampIndex] = payload[fusedFirstTypeTimestampIndex];
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
		List<EventType> result = new ArrayList<EventType>(getLargeCompaniesEventTypes());
		result.addAll(getMediumCompaniesEventTypes());
		result.addAll(getSmallCompaniesEventTypes());
		result.addAll(getAllFusedTypes());
		return result;
	}

	public List<EventType> getLargeCompaniesEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(microsoftEventType);
		result.add(yahooEventType);
		result.add(googleEventType);
		result.add(appleEventType);
		result.add(ciscoEventType);
		result.add(intelEventType);
		result.add(biduEventType);
		result.add(etfcEventType);
		result.add(fslrEventType);
		result.add(nvdaEventType);
		result.add(amznEventType);
		result.add(siriEventType);
		result.add(brcmEventType);
		result.add(amatEventType);
		result.add(croxEventType);
		result.add(drysEventType);
		result.add(qcomEventType);
		result.add(spwrEventType);
		result.add(sndkEventType);
		result.add(amgnEventType);
		result.add(ebayEventType);
		return result;
	}
	
	public List<EventType> getMediumCompaniesEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(xtexEventType);
		result.add(xtlbEventType);
		result.add(qtwwEventType);
		result.add(xbrzEventType);
		result.add(aepiEventType);
		result.add(rprxEventType);
		result.add(abcbEventType);
		result.add(prcpEventType);
		result.add(evepEventType);
		result.add(mosyEventType);
		result.add(bkccEventType);
		result.add(bsetEventType);
		result.add(sconEventType);
		result.add(fsrvEventType);
		result.add(wsfsEventType);
		result.add(redfEventType);
		result.add(ccixEventType);
		result.add(cweiEventType);
		result.add(chdnEventType);
		result.add(stsiEventType);
		result.add(mrgeEventType);
		return result;
	}
	
	public List<EventType> getSmallCompaniesEventTypes() {
		List<EventType> result = new ArrayList<EventType>();
		result.add(townEventType);
		result.add(mndoEventType);
		result.add(chtpEventType);
		result.add(nymxEventType);
		result.add(pcyoEventType);
		result.add(nlstEventType);
		result.add(nbbcEventType);
		result.add(clwtEventType);
		result.add(pmfgEventType);
		result.add(arowEventType);
		result.add(isigEventType);
		result.add(qbakEventType);
		result.add(nrimEventType);
		result.add(joezEventType);
		result.add(adrdEventType);
		result.add(ffnwEventType);
		result.add(symxEventType);
		result.add(adruEventType);
		result.add(ptekEventType);
		result.add(kirkEventType);
		result.add(nathEventType);
		return result;
	}

	@Override
	public List<String> getKnownEventTypeNames() {
		List<String> regularNames = getAllRegularTypeNames();
		regularNames.addAll(getAllFusedTypeNames());
		return regularNames;
	}

	public List<String> getAllFusedTypeNames() {
		if (!MainConfig.isFusionSupported) {
			return new ArrayList<>();
		}
		List<String> fusedNames = new ArrayList<>();
		List<String> regularNames = getAllRegularTypeNames();
		for (int i = 0; i < regularNames.size(); i++) {
			for (int j = 0; j < regularNames.size(); j++) {
				fusedNames.add(regularNames.get(i) + regularNames.get(j));
			}
		}
		return fusedNames;
	}

	private List<String> getAllRegularTypeNames() {
		List<String> result = new ArrayList<String>();
		result.add(microsoftEventTypeName);
		result.add(yahooEventTypeName);
		result.add(googleEventTypeName);
		result.add(appleEventTypeName);
		result.add(ciscoEventTypeName);
		result.add(intelEventTypeName);
		result.add(biduEventTypeName);
		result.add(etfcEventTypeName);
		result.add(fslrEventTypeName);
		result.add(nvdaEventTypeName);
		result.add(amznEventTypeName);
		result.add(siriEventTypeName);
		result.add(brcmEventTypeName);
		result.add(amatEventTypeName);
		result.add(croxEventTypeName);
		result.add(drysEventTypeName);
		result.add(qcomEventTypeName);
		result.add(spwrEventTypeName);
		result.add(sndkEventTypeName);
		result.add(amgnEventTypeName);
		result.add(ebayEventTypeName);

		result.add(xtexEventTypeName);
		result.add(xtlbEventTypeName);
		result.add(qtwwEventTypeName);
		result.add(cbrxEventTypeName);
		result.add(aepiEventTypeName);
		result.add(rprxEventTypeName);
		result.add(abcbEventTypeName);
		result.add(prcpEventTypeName);
		result.add(evepEventTypeName);
		result.add(mosyEventTypeName);
		result.add(bkccEventTypeName);
		result.add(bsetEventTypeName);
		result.add(sconEventTypeName);
		result.add(fsrvEventTypeName);
		result.add(wsfsEventTypeName);
		result.add(redfEventTypeName);
		result.add(ccixEventTypeName);
		result.add(cweiEventTypeName);
		result.add(chdnEventTypeName);
		result.add(stsiEventTypeName);
		result.add(mrgeEventTypeName);

		result.add(townEventTypeName);
		result.add(mndoEventTypeName);
		result.add(chtpEventTypeName);
		result.add(nymxEventTypeName);
		result.add(pcyoEventTypeName);
		result.add(nlstEventTypeName);
		result.add(nbbcEventTypeName);
		result.add(clwtEventTypeName);
		result.add(pmfgEventTypeName);
		result.add(arowEventTypeName);
		result.add(isigEventTypeName);
		result.add(qbakEventTypeName);
		result.add(nrimEventTypeName);
		result.add(joezEventTypeName);
		result.add(adrdEventTypeName);
		result.add(ffnwEventTypeName);
		result.add(symxEventTypeName);
		result.add(adruEventTypeName);
		result.add(ptekEventTypeName);
		result.add(kirkEventTypeName);
		result.add(nathEventTypeName);
		return result;
	}
}
