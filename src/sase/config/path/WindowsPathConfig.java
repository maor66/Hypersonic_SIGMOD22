package sase.config.path;

import sase.config.MainConfig;

public class WindowsPathConfig extends PathConfig {

	public WindowsPathConfig() {
//		companyToRegionDirectoryPath = "C:\\Education\\PhD\\Experiments\\Metadata\\Regions";
		companyToRegionDirectoryPath = "C:\\CEP\\Metadata\\Regions";
		testInputFilePath = "";
//		firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080205_1.txt";
		firstInputFilePath = (MainConfig.datasetInUse == MainConfig.DatasetInUse.STOCKS)  ?
				"C:\\Users\\Maor\\Documents\\NASDAQ_20080205_1_short150.txt" :
				"C:\\Users\\Maor\\Documents\\csh124.ann.features.csv" ;

//				firstInputFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_short.txt";
//		firstInputFilePath = "C:\\CEP\\0802\\NASDAQ_20080205_1.txt";

		//firstInputFilePath = "C:\\Education\\PhD\\Experiments\\Aarhus Traffic\\traffic.csv";
		firstInputDirectoryPath = "C:\\Users\\Maor\\Documents\\0802";
//		firstInputDirectoryPath = "C:\\CEP\\0802";
		outputFilePath = "C:\\Users\\Maor\\Documents\\lazyCEPlogs\\NASDAQ_20080201_1out"+System.currentTimeMillis()+".csv";
//		outputFilePath = "C:\\\\CEP\\\\0802\\lazyCEPlogs\\NASDAQ_20080201_1out.csv";
//		selectivityEstimatorsFilePath = "C:\\Education\\PhD\\Experiments\\Metadata\\selectivity.ser";
		selectivityEstimatorsFilePath = "C:\\Users\\Maor\\Documents\\NASDAQ_20080201_1sel_dummy.ser";
		systemPythonPath = "C:\\Users\\Maor\\AppData\\Local\\Programs\\Python\\Python38\\python.exe";
	}
}
