package sase.user.stocks.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.pattern.EventTypesManager;
import sase.specification.SimulationSpecification;
import sase.user.stocks.StockEventTypesManager;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.input.EventTypesConverter;

public class StocksByRegionEventTypesConverter extends EventTypesConverter {

	public StocksByRegionEventTypesConverter(SimulationSpecification simulationSpecification) {
		super(simulationSpecification);
	}

	@Override
	public EventType convertToKnownEventType(String[] rawEvent) {
		String eventCompanyLabel = rawEvent[0];
		return companyLabelToRegionEventType.get(eventCompanyLabel); 
	}
	
	private static final HashMap<String, EventType> companyLabelToRegionEventType = createNameToRegionHash();
	
	private static List<String> loadCompanyLabelsByRegion(String regionSpecificEventName) throws IOException {
		FileReader fileReader = new FileReader(String.format("%s%s%s.txt", 
															 MainConfig.companyToRegionDirectoryPath,
															 File.separator,
															 regionSpecificEventName));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> result = new ArrayList<String>();
        String companyLabel = null;
        try {
			while ((companyLabel = bufferedReader.readLine()) != null) {
				result.add(companyLabel);
			}
		}
        finally {
        	bufferedReader.close();
        }
		return result;
	}
	
	private static HashMap<String, EventType> createNameToRegionHash() {
		HashMap<String, EventType> result = new HashMap<String, EventType>();
		for (String regionName : StockEventTypesManager.companiesByRegionEventTypeNames) {
			List<String> companyLabels = null;
			try {
				companyLabels = loadCompanyLabelsByRegion(regionName);
			} catch (IOException e) {
				throw new RuntimeException(String.format("Unable to initialize companies of type %s", regionName));
			}
			EventType currentRegionEventType = EventTypesManager.getInstance().getTypeByName(regionName);
			for (String label : companyLabels) {
				result.put(label, currentRegionEventType);
			}
		}
		return result;
	}
}
