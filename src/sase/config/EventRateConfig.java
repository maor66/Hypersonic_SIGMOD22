package sase.config;

import java.util.HashMap;

import sase.user.sensors.SensorEventTypeManager;
import sase.user.stocks.StockEventTypesManager;
import sase.user.trams.TramEventTypesManager;

public class EventRateConfig {

    public static final HashMap<String, Double> eventRate = createEventRateHashMap();
    private static final double tempHack = 1.0;

    private static HashMap<String, Double> createEventRateHashMap() {
        HashMap<String, Double> eventRateHashMap = new HashMap<String, Double>();

        //stocks - selected companies
        eventRateHashMap.put(StockEventTypesManager.microsoftEventTypeName, 8.695 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.yahooEventTypeName, 8.422 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.googleEventTypeName, 8.482 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.appleEventTypeName, 8.975 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.ciscoEventTypeName, 8.424 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.intelEventTypeName, 8.424 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.biduEventTypeName, 8.456 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.etfcEventTypeName, 8.426 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.fslrEventTypeName, 8.259 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.nvdaEventTypeName, 8.151 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.amznEventTypeName, 8.141 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.siriEventTypeName, 8.118 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.brcmEventTypeName, 8.046 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.amatEventTypeName, 8.041 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.croxEventTypeName, 8.008 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.drysEventTypeName, 8.008 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.qcomEventTypeName, 7.968 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.spwrEventTypeName, 7.931 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.sndkEventTypeName, 7.930 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.amgnEventTypeName, 7.916 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.ebayEventTypeName, 7.903 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.xtexEventTypeName, 2.023 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.xtlbEventTypeName, 1.131 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.qtwwEventTypeName, 2.300 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.cbrxEventTypeName, 2.611 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.aepiEventTypeName, 0.995 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.rprxEventTypeName, 2.027 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.abcbEventTypeName, 2.038 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.prcpEventTypeName, 2.054 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.evepEventTypeName, 2.070 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.mosyEventTypeName, 2.070 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.bkccEventTypeName, 2.074 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.bsetEventTypeName, 2.077 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.sconEventTypeName, 2.083 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.fsrvEventTypeName, 2.100 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.wsfsEventTypeName, 2.114 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.redfEventTypeName, 2.123 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.ccixEventTypeName, 2.127 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.cweiEventTypeName, 2.128 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.chdnEventTypeName, 2.147 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.stsiEventTypeName, 2.169 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.mrgeEventTypeName, 2.181 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.townEventTypeName, 0.045 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.mndoEventTypeName, 0.546 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.chtpEventTypeName, 0.121 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.nymxEventTypeName, 0.554 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.pcyoEventTypeName, 0.075 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.nlstEventTypeName, 0.261 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.nbbcEventTypeName, 0.197 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.clwtEventTypeName, 0.199 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.pmfgEventTypeName, 0.199 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.arowEventTypeName, 0.200 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.isigEventTypeName, 0.206 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.qbakEventTypeName, 0.208 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.nrimEventTypeName, 0.208 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.joezEventTypeName, 0.221 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.adrdEventTypeName, 0.222 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.ffnwEventTypeName, 0.235 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.symxEventTypeName, 0.239 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.adruEventTypeName, 0.248 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.ptekEventTypeName, 0.251 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.kirkEventTypeName, 0.252 * tempHack);
        eventRateHashMap.put(StockEventTypesManager.nathEventTypeName, 0.254 * tempHack);

        StockEventTypesManager.getInstance().getAllFusedTypeNames().forEach(fusedEventTypeName ->
                eventRateHashMap.put(fusedEventTypeName,
                        eventRateHashMap.get(fusedEventTypeName.substring(0,4)) * eventRateHashMap.get(fusedEventTypeName.substring(4))));
        //Works only if both names are 4 chars long - somewhat lazy but event rate will probably won't be used as simulating fusion will be done only with eager

        //stocks - regions
        eventRateHashMap.put(StockEventTypesManager.africanCompanyEventTypeName, 20.0);
        eventRateHashMap.put(StockEventTypesManager.asianCompanyEventTypeName, 507.0);
        eventRateHashMap.put(StockEventTypesManager.australianCompanyEventTypeName, 1.0);
        eventRateHashMap.put(StockEventTypesManager.centralAmericanCompanyEventTypeName, 188.0);
        eventRateHashMap.put(StockEventTypesManager.europeanCompanyEventTypeName, 368.0);
        eventRateHashMap.put(StockEventTypesManager.middleEasternCompanyEventTypeName, 163.0);
        eventRateHashMap.put(StockEventTypesManager.northAmericanCompanyEventTypeName, 13643.0);
        eventRateHashMap.put(StockEventTypesManager.southAmericanCompanyEventTypeName, 48.0);

        //trams
        eventRateHashMap.put(TramEventTypesManager.normalTrafficEventTypeName, 100.0);
        eventRateHashMap.put(TramEventTypesManager.lightCongestionEventTypeName, 100.0);
        eventRateHashMap.put(TramEventTypesManager.mediumCongestionEventTypeName, 100.0);
        eventRateHashMap.put(TramEventTypesManager.severeCongestionEventTypeName, 100.0);
        eventRateHashMap.put(TramEventTypesManager.heavyCongestionEventTypeName, 100.0);

        // Sensors
        eventRateHashMap.put(SensorEventTypeManager.sleepEventTypeName, 9536.0   );
        eventRateHashMap.put(SensorEventTypeManager.otherActivityEventTypeName, 68288.0);
        eventRateHashMap.put(SensorEventTypeManager.toiletEventTypeName, 4530.0   );
        eventRateHashMap.put(SensorEventTypeManager.bedToiletTransitionEventTypeName, 354.0   );
        eventRateHashMap.put(SensorEventTypeManager.personalHygieneEventTypeName, 3815.0   );
        eventRateHashMap.put(SensorEventTypeManager.entertainGuestsEventTypeName, 11091.0   );
        eventRateHashMap.put(SensorEventTypeManager.leaveHomeEventTypeName, 711.0   );
        eventRateHashMap.put(SensorEventTypeManager.enterHomeEventTypeName, 665.0   );
        eventRateHashMap.put(SensorEventTypeManager.stepOutEventTypeName, 715.0   );
        eventRateHashMap.put(SensorEventTypeManager.phoneEventTypeName, 768.0   );
        eventRateHashMap.put(SensorEventTypeManager.sleepOutOfBedEventTypeName, 1991.0   );
        eventRateHashMap.put(SensorEventTypeManager.workEventTypeName, 100.0);
        eventRateHashMap.put(SensorEventTypeManager.workAtTableEventTypeName, 2455.0   );
        eventRateHashMap.put(SensorEventTypeManager.cookDinnerEventTypeName, 12561.0);
        eventRateHashMap.put(SensorEventTypeManager.eatDinnerEventTypeName, 567.0   );
        eventRateHashMap.put(SensorEventTypeManager.watchTVEventTypeName, 2298.0   );
        eventRateHashMap.put(SensorEventTypeManager.groomEventTypeName, 31590.   );
        eventRateHashMap.put(SensorEventTypeManager.dressEventTypeName, 5580.0   );
        eventRateHashMap.put(SensorEventTypeManager.readEventTypeName, 1134.0   );
        eventRateHashMap.put(SensorEventTypeManager.workOnComputerEventTypeName, 5136.0   );
        eventRateHashMap.put(SensorEventTypeManager.washDinnerDishesEventTypeName, 2989.0   );
        eventRateHashMap.put(SensorEventTypeManager.washDishesEventTypeName, 10880.0   );
        eventRateHashMap.put(SensorEventTypeManager.cookBreakfastEventTypeName, 16633.0);
        eventRateHashMap.put(SensorEventTypeManager.drinkEventTypeName, 6131.0   );
        eventRateHashMap.put(SensorEventTypeManager.eatBreakfastEventTypeName, 1358.0   );
        eventRateHashMap.put(SensorEventTypeManager.cookLunchEventTypeName, 3314.0   );
        eventRateHashMap.put(SensorEventTypeManager.batheEventTypeName, 141.0   );
        eventRateHashMap.put(SensorEventTypeManager.washBreakfastDishedEventTypeName, 4985.0   );
        eventRateHashMap.put(SensorEventTypeManager.RelaxEventTypeName, 885.0   );
        eventRateHashMap.put(SensorEventTypeManager.eveningMedsEventTypeName, 1307.0  );
        eventRateHashMap.put(SensorEventTypeManager.morningMedsEventTypeName, 1043.0   );
        eventRateHashMap.put(SensorEventTypeManager.washLunchDishesEventTypeName, 434.0   );
        eventRateHashMap.put(SensorEventTypeManager.cookEventTypeName, 5607.0   );
        eventRateHashMap.put(SensorEventTypeManager.eatLunchEventTypeName, 331.0   );
        eventRateHashMap.put(SensorEventTypeManager.eatEventTypeName, 246.0   );

        return eventRateHashMap;
    };
}
