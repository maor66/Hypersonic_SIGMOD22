package adaptive.estimation;

import java.util.HashMap;
import evaluation.IEvaluationMechanismInfo;
import evaluation.common.State;
import pattern.condition.base.AtomicCondition;
import simulator.Environment;

public class SlidingWindowSelectivityEstimator {

	private static final double defaultSelectivity = 1.0;
	
	private HashMap<AtomicCondition, ExponentialHistogramsCounter> estimators;
	private final long windowSize;
	private final double maxError;

	public SlidingWindowSelectivityEstimator(long windowSize, double maxError) {
		estimators = new HashMap<AtomicCondition, ExponentialHistogramsCounter>();
		this.windowSize = windowSize;
		this.maxError = maxError;
	}
	
	public void registerConditionVerification(AtomicCondition condition, boolean result) {
		if (!estimators.containsKey(condition)) {
			estimators.put(condition, new ExponentialHistogramsCounter(calculateWindowSizeForCondition(condition), maxError));
		}
		estimators.get(condition).addElement(result);
	}
	
	private long calculateWindowSizeForCondition(AtomicCondition condition) {
		return windowSize * getConditionReachProbability(condition).longValue();
	}
	
	private Double getConditionReachProbability(AtomicCondition condition) {
		IEvaluationMechanismInfo evaluationMechanismInfo = Environment.getEnvironment().getEvaluationMechanismInfo();
		if (evaluationMechanismInfo == null) {
			return null;
		}
		State state = evaluationMechanismInfo.getStateByAtomicCondition(condition);
		if (state == null) {
			return null;
		}
		return evaluationMechanismInfo.getStateReachProbability(state);
	}
	
	public double getSelectivityEstimate(AtomicCondition condition) {
		ExponentialHistogramsCounter counter = estimators.get(condition);
		if (counter == null || counter.getNumberOfRecordedElements() < counter.getWindowSize()) {
			return defaultSelectivity;
		}
		double result = counter.getRateEstimate();
		if (result < 0) {
			return 0;
		}
		if (result > 1) {
			return 1;
		}
		return result;
	}
	
	public void recalculateWindowSizes() {
		for (AtomicCondition condition : estimators.keySet()) {
			ExponentialHistogramsCounter counter = estimators.get(condition);
			counter.setWindowSize(calculateWindowSizeForCondition(condition));
		}
	}

}
