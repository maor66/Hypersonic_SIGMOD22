package sase.evaluation.data_parallel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import sase.base.Event;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.data_parallel.DataParallelEvaluationMechanism.EvaluationInput;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.specification.evaluation.HirzelEvaluationSpecification;
import sase.specification.evaluation.ParallelEvaluationSpecification;

public final class HirzelEvaluationMechanism extends DataParallelEvaluationMechanism {
	// For Hirzel algorithm implementation we need to choose an attribute!
	private String attribute;
	
	public HirzelEvaluationMechanism(Pattern pattern, HirzelEvaluationSpecification specification, EvaluationPlan evaluationPlan) {
		super(pattern, specification, evaluationPlan);
		this.attribute = specification.attribute;

		// Create threads
		for (int i = 0; i < num_of_threads; ++i) {
			threads[i].start();
		}
	}
	
	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		List<Match> result = new ArrayList<>();
		// Find the relevant attribute value
		String attributeValue = (String)event.getAttributeValue(attribute).toString();
		// Select a thread id by attribute value
		
//		int id = attributeValue.hashCode() % num_of_threads;
		
		// MAX : THIS CODE IS FOR THE HIRZEL TEST CASE!
		String firstLetter = attributeValue.substring(0, 1);
		int id = firstLetter.hashCode() % num_of_threads;
		
		// Add to thread blocking queue
		((ParallelThread)threads[id]).threadInput.add(new EvaluationInput(event, canStartInstance));
		// get all results from out queue and return them as a list of matches
		threadOutput.drainTo(result);
		return result;
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		List res = new ArrayList<Match>();
		return res;
	}

	@Override
	public void completeCreation(List<Pattern> patterns) {
		for (int i = 0; i < num_of_threads; ++i) {
			threads[i].machine.completeCreation(patterns);
		}
	}

	@Override
	public List<Match> getLastMatches() {
		// TODO Auto-generated method stub
		List res = new ArrayList<Match>();
		for (int i = 0; i < num_of_threads; ++i) {
			lock.lock();
			res.addAll(threads[i].machine.getLastMatches());
			lock.unlock();
		}
		return res;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		long size = 0;
		for (int i = 0; i < num_of_threads; ++i) {
			lock.lock();
			size += threads[i].machine.size();
			lock.unlock();
		}
		return size;
	}

	@Override
	public String getStructureSummary() {
		// Doesn't matter which to return
		return threads[0].machine.getStructureSummary();
	}

	@Override
	public void removeConflictingInstances(List<Match> matches) {
		// TODO Auto-generated method stub
		for (int i = 0; i < num_of_threads; ++i) {
			threads[i].machine.removeConflictingInstances(matches);
		}
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
