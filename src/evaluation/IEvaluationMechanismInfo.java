package evaluation;

import evaluation.common.State;
import pattern.condition.base.AtomicCondition;

public interface IEvaluationMechanismInfo {

	public State getStateByAtomicCondition(AtomicCondition condition);
	public double getStateReachProbability(State state);
}
