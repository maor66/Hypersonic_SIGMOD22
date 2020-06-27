package sase.config.path;

import sase.config.MainConfig;

public class LinuxPathConfig extends PathConfig {
	
	public LinuxPathConfig() {
		companyToRegionDirectoryPath = "/home/ilyak/regions";
		testInputFilePath = "";
//		firstInputFilePath = "/home/yankovitch/parallelCEP/data/NASDAQ_short.txt";
		firstInputFilePath = "/home/yankovitch/parallelCEP/data/NASDAQ_20080201_1.txt";
		//firstInputFilePath = "/home/ilyak/data/traffic.csv";
		firstInputDirectoryPath = "/home/yankovitch/parallelCEP/data/0802";
		outputFilePath = "/home/yankovitch/parallelCEP/lazyCEPlogs/NASDAQ_20080201_1out"+System.currentTimeMillis() + MainConfig.experimentName +".csv";
//		selectivityEstimatorsFilePath = "/home/yankovitch/parallelCEP/NASDAQ_20080201_1sel.ser";
//		selectivityEstimatorsFilePath = "/home/yankovitch/parallelCEP/NASDAQ_20080201_1selSEQ6.ser";
		selectivityEstimatorsFilePath = "/home/yankovitch/parallelCEP/NASDAQ_20080201_1selSEQ6Exp.ser";
//		selectivityEstimatorsFilePath = "/home/yankovitch/parallelCEP/NASDAQ_20080201_1selSEQ4actual.ser";
//		selectivityEstimatorsFilePath = "/home/yankovitch/parallelCEP/NASDAQ_20080201_1selSEQ4.ser";
		systemPythonPath = "/usr/bin/python3.8";

	}
}
