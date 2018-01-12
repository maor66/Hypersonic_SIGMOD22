package config.path;

public class LinuxPathConfig extends PathConfig {
	
	public LinuxPathConfig() {
		companyToRegionDirectoryPath = "/home/ilyak/regions";
		testInputFilePath = "";
		//firstInputFilePath = "/home/ilyak/data/0802/NASDAQ_20080201_1.txt";
		firstInputFilePath = "/home/ilyak/data/traffic.csv";
		firstInputDirectoryPath = "/home/ilyak/data/trams";
		outputFilePath = "/home/ilyak/results/synthetic.csv";
		selectivityEstimatorsFilePath = "/home/ilyak/selectivity.ser";
	}
}
