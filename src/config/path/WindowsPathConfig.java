package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		testInputFilePath = "";
		firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Data\\Processed-1\\0802\\NASDAQ_20080201_1.txt";
		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
		firstInputDirectoryPath = "C:\\Education\\PhD\\Experiments\\Data\\Processed-2";
		outputFilePath = "C:\\Education\\PhD\\Experiments\\Results\\test_results.csv";
		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
	}
}
