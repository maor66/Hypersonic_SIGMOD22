package sase.statistics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import sase.config.MainConfig;

public class ConditionSelectivityCollector {
	
	private static ConditionSelectivityCollector instance = null;
	private static final double defaultSelectivity = 0.5;
	//NOTE: should be null unless you intentionally want some (unknown) conditions to have a synthetic, static selectivity!
	private static final Double selectivityOfUnknownCondition = 1.0;
	
	public static ConditionSelectivityCollector getInstance() {
		if (instance == null) {
			instance = new ConditionSelectivityCollector();
		}
		return instance;
	}

	private class SelectivityCounters {
		public int in = 0;
		public int out = 0;
		
		public double getSelectivityEstimate() {
			return (out == 0) ? defaultSelectivity : ((double)out) / in;
		}
	}
	
	private HashMap<String, SelectivityCounters> counters;
	private HashMap<String, Double> selectivityEstimates = null;
	
	protected ConditionSelectivityCollector() {
		counters = new HashMap<String, ConditionSelectivityCollector.SelectivityCounters>();
		if (!MainConfig.conditionSelectivityMeasurementMode) {
			deserializeSelectivityEstimates();
		}
	}
	
	public void recordConditionEvaluation(String key, boolean success) {
		SelectivityCounters countersForKey = counters.get(key);
		if (countersForKey == null) {
			countersForKey = new SelectivityCounters();
		}
		++countersForKey.in;
		if (success) {
			++countersForKey.out;
		}
		counters.put(key, countersForKey);
	}
	
	public Double getSelectivityEstimate(String key) {
		if (selectivityEstimates == null) {
			return null;
		}
		Double estimate = selectivityEstimates.get(key);
		if (estimate != null) {
			return estimate;
		}
		String[] transformedKeys = getKeyTransformations(key);
		if (transformedKeys == null) {
			return null;
		}
		for (String transformedKey : transformedKeys) {
			Double newEstimate = selectivityEstimates.get(transformedKey);
			if (newEstimate != null) {
				//TODO: the returned value is dependent on whether the condition is symmetric, whether it is anti-symmetric
				//and also on other statistical properties
				return 1 - newEstimate;
			}
		}
		return selectivityOfUnknownCondition;
	}
	
	private String[] getKeyTransformations(String key) {
		//for now, only attempt to swap the first and the second sub-identifiers, separated by colon
		String[] keyParts = key.split(":", 3);
		String invertedKey;
		if (keyParts.length < 2) {
			return null;
		}
		invertedKey = (keyParts.length == 2) ? (keyParts[1] + ":" + keyParts[0]) :
											   (keyParts[1] + ":" + keyParts[0] + ":" + keyParts[2]);
		return new String[]{ invertedKey };
	}

	private void calculateSelectivityEstimates() {
		selectivityEstimates = new HashMap<String, Double>();
		for (String key : counters.keySet()) {
			selectivityEstimates.put(key, counters.get(key).getSelectivityEstimate());
		}
	}
	
	public void serializeSelectivityEstimates() {
		calculateSelectivityEstimates();
		try {
			FileOutputStream fos = new FileOutputStream(MainConfig.selectivityEstimatorsFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(selectivityEstimates);
			oos.close();
			fos.close();
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
		for (String estimateKey : selectivityEstimates.keySet()) {
			System.out.println(String.format("%s:%f", estimateKey, selectivityEstimates.get(estimateKey)));
		}
		for (String estimateKey : counters.keySet()) {
			if (counters.get(estimateKey).out == 0) {
				System.out.println(estimateKey);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deserializeSelectivityEstimates() {
		try {
			FileInputStream fis = new FileInputStream(MainConfig.selectivityEstimatorsFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			selectivityEstimates = (HashMap<String, Double>) ois.readObject();
			ois.close();
			fis.close();
//			selectivityEstimates.put("BIDU:AAPL",0.3);
//			selectivityEstimates.put("AAPL:YHOO",0.7);
//			selectivityEstimates.put("YHOO:MSFT",0.2);
//			selectivityEstimates.put("MSFT:GOOG",0.8);
		}
		catch(IOException ioe) {
			//we assume no file is available
			selectivityEstimates = new HashMap<String, Double>();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
//		for (String estimateKey : selectivityEstimates.keySet()) {
//			System.out.println(String.format("%s:%f", estimateKey, selectivityEstimates.get(estimateKey)));
//		}
	}

}
