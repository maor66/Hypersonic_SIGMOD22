package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		testInputFilePath = "";
		firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080205_1.txt";
//				firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_short.txt";

		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
		firstInputDirectoryPath = "C:\\Users\\Maor\\Documents\\0802less";
		outputFilePath = "C:\\Users\\Maor\\Documents\\lazyCEPlogs\\NASDAQ_20080201_1out"+System.currentTimeMillis()+".csv";
//		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
		selectivityEstimatorsFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_1sel.ser";
	}
}
