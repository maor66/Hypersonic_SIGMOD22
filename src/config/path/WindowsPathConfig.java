package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		testInputFilePath = "";
		firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_3.txt";
		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
		firstInputDirectoryPath = "C:\\Users\\Maor\\Documents\\CEPinputnasdaq";
		outputFilePath = "C:\\Users\\Maor\\Documents\\test_results.csv";
		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
	}
}
