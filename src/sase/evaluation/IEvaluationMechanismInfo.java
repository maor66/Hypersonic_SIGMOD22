package sase.evaluation;

import sase.evaluation.common.State;
import sase.pattern.condition.base.AtomicCondition;

public interface IEvaluationMechanismInfo {

	public State getStateByAtomicCondition(AtomicCondition condition);
	public double getStateReachProbability(State state);
}
