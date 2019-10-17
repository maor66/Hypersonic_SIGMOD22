package sase.config;

import sase.base.EventSelectionStrategies;
import sase.config.path.LinuxPathConfig;
import sase.config.path.PathConfig;
import sase.config.path.WindowsPathConfig;
import sase.input.EventProducerTypes;
import sase.input.EventTypesConverterTypes;
import sase.pattern.EventTypesManager;
import sase.user.stocks.StockEventTypesManager;

@SuppressWarnings("unused")
public class MainConfig {
	/* An OS-dependent module for accessing local paths. */
	private static final PathConfig pathConfig = new WindowsPathConfig();
	
	/* A flag indicating whether code testing is currently in process. */
	public static final boolean debugMode = false;

	public static final boolean statisticsDebugMode = false;

	public static final boolean parallelDebugMode = true;


	/* Settings for creating and preprocessing primitive events arriving on the input stream. */
	public static final EventProducerTypes eventProducerType = EventProducerTypes.FILE_BASED;
	public static final boolean isSyntheticInputEnabled = (eventProducerType == EventProducerTypes.SYNTHETIC);
	public static final EventTypesConverterTypes eventTypesConverterType = EventTypesConverterTypes.STOCK_BY_COMPANY;
	
	/* Settings for receiving events from the input stream. */
	// Maor: This two settings determine if the input is taken from a directory or a file
	public static String[] inputDirsPaths = {
//		pathConfig.firstInputDirectoryPath,
	};
	public static String[] inputFilesPaths = {
		pathConfig.firstInputFilePath,
	};
	public static final int eventsPerRead = 0;
	
	/* Settings for creating and managing stock event types. */
	public static final int historyLength = 20;
	public static final String companyToRegionDirectoryPath = pathConfig.companyToRegionDirectoryPath;
	
	/* Settings for creating and managing tram congestion event types. */
	public static final int lightCongestionThreshold = 3;
	public static final int mediumCongestionThreshold = 4;
	public static final int severeCongestionThreshold = 6;
	public static final int heavyCongestionThreshold = 8;
	
	/* Settings for creating and managing statistics. */
	public static String outputFilePath = pathConfig.outputFilePath;

	public static final boolean periodicallyReportStatistics = false;
	public static final int statisticsReportPeriod = 1000;
	
	/* Settings for modifying the original events stream to produce synthetic data. */
	public static final String rarestEventTypeName = null;
	public static final boolean enableFullDynamicMode = false;
	public static final String recognizedEventTypeNames[] = {
			StockEventTypesManager.highTechCompanyEventTypeName,
			StockEventTypesManager.financeCompanyEventTypeName,
			StockEventTypesManager.megaLargeCompanyEventTypeName
		};
	public static final int eventRatesChangeFrequency = 10000;
	
	/* A flag indicating whether event rate measurement is to be enabled. */
	public static final boolean eventRateMeasurementMode = false;
	
	/* Condition selectivity measurement settings. */
	public static final boolean conditionSelectivityMeasurementMode = false; //Maor: indicates whether to use selectivity file or calculate them
	public static final String selectivityEstimatorsFilePath = pathConfig.selectivityEstimatorsFilePath;
	
	/* Maximal allowed execution time for a single simulation. */
	public static final Long maxExecutionTime = (long)(30*60*1000);
	
	/* Enable/disable to use simulation history to avoid repeated runs. */
	public static final boolean useSimulationHistory = false;
	
	/* Enable to run experiments on plan construction phase only. */
	public static final boolean planConstructionOnly = false;
	
	/* Runtime statistics monitoring settings. */
	public static final boolean isArrivalRateMonitoringAllowed = false;
	public static final boolean isSelectivityMonitoringAllowed = false;
	public static final Double adaptationTrialsIntervalToTimeWindowRatio = null; //2.0;//null to disable adaptation
	
	/* Event selection strategy used during the current run. */
	public static final EventSelectionStrategies selectionStrategy = EventSelectionStrategies.SKIP_TILL_ANY;
	
	/* Enable/disable debug prints of the generated evaluation structures. */
	public static final boolean printStructureSummary = false;
}
