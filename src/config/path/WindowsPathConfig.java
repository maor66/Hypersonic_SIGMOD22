package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		testInputFilePath = "";
		firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Data\\Processed-1\\0802\\NASDAQ_20080204_1.txt";
		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
		firstInputDirectoryPath = "C:\\Education\\PhD\\Experiments\\Data\\Processed-1\\0802";
		outputFilePath = "C:\\Education\\PhD\\Experiments\\Results\\results_conjunction7.csv";
		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
	}
}
