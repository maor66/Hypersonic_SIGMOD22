package sase.config.path;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		testInputFilePath = "";
		firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_1.txt";
		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
		firstInputDirectoryPath = "C:\\Users\\Maor\\Documents\\inputdir";
		outputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_1out.csv";
//		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
		selectivityEstimatorsFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_1sel.ser";
	}
}
