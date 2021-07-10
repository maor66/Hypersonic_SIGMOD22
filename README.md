# HYPERSONIC #

## Setup Instructions

1. Set main class as `sase.simulator.Simulator`
2. Add the library dependencies in the `dependencies` folder
3. Choose which data set to run in `src/sase/config/MainConfig.java`
	3. Set the `dataInUse` field to `DatasetInUse.STOCKS` or `DatasetInUse.SENSORS` (lines 14-15)
4. Set configuration to Windows or Linux based on your system in  `src/sase/config/MainConfig.java`
	4.     The `pathConfig` field (line 20) needs to be set to `WindowsPathConfig` or `LinuxPathConfig` (for Windows and Linux, respectively)

	4. The rest of the configuration will now relate to `src/sase/config/path/WindowsPathConfig.java` or to` src/sase/config/path/LinuxPathConfig.java`, based on your selection in the previous section.
	4.  Set input file path in `firstInputFilePath` or `firstInputDirectoryPath` for a directory full of input files (without additional files that are not intended as input)
		4. If using a directory -  uncomment `inputDirsPaths` in `src/sase/config/MainConfig.java` (line 51) and comment value of `inputFilesPaths` (line 54)
	4. Set the output file path in the `outputFilePath`  field.




## Datasets

We used two datasets for testing HYPERSONIC performance, a sample of an input file  (.CSV) for each data set is included in this repository.
#### Stocks
As stated in the paper, this dataset represents information about NASDAQ stocks.

We included a single file in the root of the repository (NASDAQ_20080204_1.txt), which contains a minute-by-minute updates on stocks in a single day, augmented with pre-processing that adds an array of 20 last recorded stock prices.
 We used a full month as a dataset, which is downloadable from https://eoddata.com

#### Sensors
This dataset represents information collected from sensors in smart homes, the sample file is (csh114.ann.features.csv).
 The dataset was used without any augmentation.
  The full dataset is downloadable from https://archive.ics.uci.edu/ml/datasets/Human+Activity+Recognition+from+Continuous+Ambient+Sensor+Data

## Patterns
The simulation can accept any number of patterns to be detected and runs a different simulation run (which can contain more than one system/algorithm, see below) for each pattern separately.
#### Stocks
Set `stockByCompanyPatternSpecifications` in `src/sase/config/PatternConfig.java` with the patterns that you wish to detect.

The simplest way to build a pattern is to use one of the many patterns already defined in this file (`basicPatternSEQ6Correlation` for example) and simply change the types of the pattern (all supported types are in `sase/user/stocks/StockEventTypesManager.java`)
The current configuration is:
```
	public static final PatternSpecification[] stockByCompanyPatternSpecifications = {

						basicPatternSEQ6Correlation.createIdenticalSpecificationWithDifferentWindow(65),
			basicPatternSEQ6Correlation.createIdenticalSpecificationWithDifferentWindow(80),
			basicPatternSEQ6Correlation.createIdenticalSpecificationWithDifferentWindow(95),
			basicPatternSEQ6Correlation.createIdenticalSpecificationWithDifferentWindow(110),

	};
```
It would run 4 patterns that are of the same structure, but with a different time window. To run different patterns a possible configuration would be:
```
	public static final PatternSpecification[] stockByCompanyPatternSpecifications = {

			basicPatternSEQ5.createIdenticalSpecificationWithDifferentWindow(1000),
			basicPatternSEQ6Correlation.createIdenticalSpecificationWithDifferentWindow(100),
			basicPatternSEQ8var1.createIdenticalSpecificationWithDifferentWindow(100)

	};
```
Which runs 3 different patterns (with the same time window - 100)

#### Sensors
Set `sensorPatternSpecifications` in `src/sase/config/PatternConfig.java` and it works the same as the sensor patterns, with a different build.

An example for a pattern is found at `basicPatternSensorSEQ3`.


## Simulations
After running the simulations, an output file with the results will be created at the defined location (see Setup Instructions)
Most of the fields in the created .CSV are irrelevant and used for debugging, but "Processing Time (ms)", "Peak Memory Consumption" and "Average Latency" are the metrics that we are interested in.

To set which simulations are going to be run, set the `evaluationSpecifications` array in `sase/config/SimulationConfig.java` .

Each array entry is one simulation that would run on all patterns configured in the previous section. These runs are independent and does not occur simultaneously.
##### HYPERSONIC
To run HYPERSONIC, add:
```
 		new ParallelLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
				CostModelTypes.THROUGHPUT_LATENCY,
				0.0, 24, 0.5),
```
Only change the 4th parameter which is the number of execution units (threads) used for this run. Other parameters are used for future work and should are not relevant for this paper.

##### RIP
To run RIP:
```
		new RIPEvaluationSpecification(EvaluationMechanismTypes.RIP_CHAIN_NFA, new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
				CostModelTypes.THROUGHPUT_LATENCY,
				0.0), 24, 0)
				```
Only change the 4th parameter which is the number of execution units (threads) used for this run .Other parameters are used for future work and should are not relevant for this paper.

##### LLSF
To run LLSF:
```
		new ParallelSplitDuplicateNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
				CostModelTypes.THROUGHPUT_LATENCY,
				0.0, 24, 0.5),
```
Only change the 4th parameter which is the number of execution units (threads) used for this run. Other parameters are used for future work and should are not relevant for this paper.

##### Sequential
```
		new CostBasedLazyNFAEvaluationSpecification(OrderingAlgorithmTypes.TRIVIAL,
				CostModelTypes.THROUGHPUT_LATENCY,
				0.0),
```
Do not change any parameter. Other parameters are used for future work and should are not relevant for this paper.