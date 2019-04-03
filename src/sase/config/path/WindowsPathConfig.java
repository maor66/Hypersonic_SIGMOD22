package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
//		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		companyToRegionDirectoryPath = "C:\\CEP\\Metadata\\Regions";
		testInputFilePath = "";
//		firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080205_1.txt";
//				firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_short.txt";
		firstInputFilePath = "C:\\CEP\\0802\\NASDAQ_20080205_1.txt";

		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
//		firstInputDirectoryPath = "C:\\Users\\Maor\\Documents\\0802";
		firstInputDirectoryPath = "C:\\CEP\\0802";
//		outputFilePath = "C:\\Users\\Maor\\Documents\\lazyCEPlogs\\NASDAQ_20080201_1out"+System.currentTimeMillis()+".csv";
		outputFilePath = "C:\\\\CEP\\\\0802\\lazyCEPlogs\\NASDAQ_20080201_1out.csv";
//		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
		selectivityEstimatorsFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_1sel.ser";
	}
}
