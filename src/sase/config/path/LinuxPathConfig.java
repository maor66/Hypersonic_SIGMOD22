package sase.config.path;

public class LinuxPathConfig extends PathConfig {
	
	public LinuxPathConfig() {
		companyToRegionDirectoryPath = "/home/ilyak/regions";
		testInputFilePath = "";
		firstInputFilePath = "/home/yankovich/parallelCEP/data/NASDAQ_20080201_1.txt";
		//firstInputFilePath = "/home/ilyak/data/traffic.csv";
		firstInputDirectoryPath = "/home/yankovich/parallelCEP/data";
		outputFilePath = "/home/yankovich/parallelCEP/lazyCEPlogs/NASDAQ_20080201_1out"+System.currentTimeMillis()+".csv";
		selectivityEstimatorsFilePath = "/home/yankovich/parallelCEP/NASDAQ_20080201_1sel.ser";
	}
}
