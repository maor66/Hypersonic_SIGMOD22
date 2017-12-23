package config;

import java.util.HashMap;

import user.stocks.StockEventTypesManager;
import user.trams.TramEventTypesManager;

public class EventRateConfig {

	public static final HashMap<String, Integer> eventRate = createEventRateHashMap();
	
	private static HashMap<String, Integer> createEventRateHashMap() {
		HashMap<String, Integer> eventRateHashMap = new HashMap<String, Integer>();

		//stocks - selected companies
		//TODO: those are dummy values, only added for convenience - find out the real ones
		eventRateHashMap.put(StockEventTypesManager.microsoftEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.yahooEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.googleEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.appleEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.ciscoEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.intelEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.illuminaEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.etradeEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.broadcomEventTypeName, 100);
		eventRateHashMap.put(StockEventTypesManager.rambusEventTypeName, 100);
		
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
