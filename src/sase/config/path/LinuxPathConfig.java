package sase.config.path;

public class LinuxPathConfig extends PathConfig {
	
	public LinuxPathConfig() {
		companyToRegionDirectoryPath = "/home/ilyak/regions";
		testInputFilePath = "";
//		firstInputFilePath = "/home/yankovitch/parallelCEP/data/NASDAQ_short.txt";
		firstInputFilePath = "/home/yankovitch/parallelCEP/data/NASDAQ_20080201_1.txt";
		//firstInputFilePath = "/home/ilyak/data/traffic.csv";
		firstInputDirectoryPath = "/home/yankovitch/parallelCEP/data/0802";
		outputFilePath = "/home/yankovitch/parallelCEP/lazyCEPlogs/NASDAQ_20080201_1out"+System.currentTimeMillis()+".csv";
		selectivityEstimatorsFilePath = "/home/yankovitch/parallelCEP/NASDAQ_20080201_1sel.ser";
	}
}
