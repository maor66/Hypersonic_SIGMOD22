package statistics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import config.MainConfig;

public class ConditionSelectivityCollector {
	
	private static ConditionSelectivityCollector instance = null;
	private static final double defaultSelectivity = 0.5;
	
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
				return newEstimate;
			}
		}
		return null;
	}
	
	private String[] getKeyTransformations(String key) {
		//for now, only attempt to swap the first and the second sub-identifiers, separated by colon
		String[] keyParts = key.split(":", 3);
		if (keyParts.length < 3) {
			return null;
		}
		String invertedKey = keyParts[1] + ":" + keyParts[0] + ":" + keyParts[2];
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
	}
	
	@SuppressWarnings("unchecked")
	private void deserializeSelectivityEstimates() {
		try {
			FileInputStream fis = new FileInputStream(MainConfig.selectivityEstimatorsFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			selectivityEstimates = (HashMap<String, Double>) ois.readObject();
			ois.close();
			fis.close();
		}
		catch(IOException ioe) {
			//we assume no file is available
			selectivityEstimates = new HashMap<String, Double>();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
