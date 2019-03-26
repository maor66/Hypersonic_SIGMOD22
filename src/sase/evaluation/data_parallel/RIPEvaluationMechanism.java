package sase.evaluation.data_parallel;

import java.util.List;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.evaluation.ParallelEvaluationSpecification;
import sase.specification.evaluation.RIPEvaluationSpecification;

public final class RIPEvaluationMechanism extends DataParallelEvaluationMechanism {
	
	// Please help me got so I can do this shit...
	
	public RIPEvaluationMechanism(Pattern pattern, RIPEvaluationSpecification specification, EvaluationPlan evaluationPlan) {
		super(pattern, specification, evaluationPlan);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		
		return null;
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void completeCreation(List<Pattern> patterns) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Match> getLastMatches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStructureSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeConflictingInstances(List<Match> matches) {
		// TODO Auto-generated method stub

	}

	@Override
	public State getStateByAtomicCondition(AtomicCondition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getStateReachProbability(State state) {
		// TODO Auto-generated method stub
		return 0;
	}
}
