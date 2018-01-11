package sase.config;

import java.util.HashMap;

import user.stocks.StockEventTypesManager;
import user.trams.TramEventTypesManager;

public class EventRateConfig {

	public static final HashMap<String, Integer> eventRate = createEventRateHashMap();
	
	private static HashMap<String, Integer> createEventRateHashMap() {
		HashMap<String, Integer> eventRateHashMap = new HashMap<String, Integer>();

		//stocks - selected companies
		//TODO: these values are only valid for one input file - find out the global ones
		eventRateHashMap.put(StockEventTypesManager.microsoftEventTypeName, 450);
		eventRateHashMap.put(StockEventTypesManager.yahooEventTypeName, 441);
		eventRateHashMap.put(StockEventTypesManager.googleEventTypeName, 433);
		eventRateHashMap.put(StockEventTypesManager.appleEventTypeName, 429);
		eventRateHashMap.put(StockEventTypesManager.ciscoEventTypeName, 424);
		eventRateHashMap.put(StockEventTypesManager.intelEventTypeName, 421);
		eventRateHashMap.put(StockEventTypesManager.crosstechEventTypeName, 89);
		eventRateHashMap.put(StockEventTypesManager.xtlbioEventTypeName, 90);
		eventRateHashMap.put(StockEventTypesManager.quantumEventTypeName, 90);
		eventRateHashMap.put(StockEventTypesManager.juniperEventTypeName, 91);
		eventRateHashMap.put(StockEventTypesManager.aepindustriesEventTypeName, 91);
		eventRateHashMap.put(StockEventTypesManager.reprosEventTypeName, 92);
		eventRateHashMap.put(StockEventTypesManager.townebankEventTypeName, 17);
		eventRateHashMap.put(StockEventTypesManager.mindctiEventTypeName, 18);
		eventRateHashMap.put(StockEventTypesManager.chelseaEventTypeName, 18);
		eventRateHashMap.put(StockEventTypesManager.nymoxEventTypeName, 18);
		eventRateHashMap.put(StockEventTypesManager.purecycleEventTypeName, 18);
		eventRateHashMap.put(StockEventTypesManager.netlistEventTypeName, 19);
		
		//stocks - regions
		eventRateHashMap.put(StockEventTypesManager.africanCompanyEventTypeName, 20);
		eventRateHashMap.put(StockEventTypesManager.asianCompanyEventTypeName, 507);
		eventRateHashMap.put(StockEventTypesManager.australianCompanyEventTypeName, 1);
		eventRateHashMap.put(StockEventTypesManager.centralAmericanCompanyEventTypeName, 188);
		eventRateHashMap.put(StockEventTypesManager.europeanCompanyEventTypeName, 368);
		eventRateHashMap.put(StockEventTypesManager.middleEasternCompanyEventTypeName, 163);
		eventRateHashMap.put(StockEventTypesManager.northAmericanCompanyEventTypeName, 13643);
		eventRateHashMap.put(StockEventTypesManager.southAmericanCompanyEventTypeName, 48);
		
		//trams
		eventRateHashMap.put(TramEventTypesManager.normalTrafficEventTypeName, 100);
		eventRateHashMap.put(TramEventTypesManager.lightCongestionEventTypeName, 100);
		eventRateHashMap.put(TramEventTypesManager.mediumCongestionEventTypeName, 100);
		eventRateHashMap.put(TramEventTypesManager.severeCongestionEventTypeName, 100);
		eventRateHashMap.put(TramEventTypesManager.heavyCongestionEventTypeName, 100);
		
		return eventRateHashMap;
	};
}
