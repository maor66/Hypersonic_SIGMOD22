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
			IEvaluationMechanism machine = threads[i].machine;
			threads[i] = new ParallelThread() {
				@Override
				public void run() {
					// Specific Hirzel code to run
					EvaluationInput eval_input;
					Event event = null;
					boolean canStartInstance = false;
					List<Match> result;
					while (!interrupted()) {
						eval_input = thread_input.poll();
						if (eval_input == null) continue;
						event = eval_input.event;
						canStartInstance = eval_input.canStartInstance;
						// if yes, run processNewEvent of nfa
						lock.lock();
						result = machine.processNewEvent(event, canStartInstance);
						lock.unlock();
						// add result of processNewEvent to out queue
						if (result != null) {
							thread_output.addAll(result);
						}
					}
					return;
				}
			};
			threads[i].machine = machine;
			threads[i].start();
		}
	}
	
	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {	
		lock.lock();
		// Find the relevant attribute value
		String attribute_value = (String)event.getAttributeValue(attribute).toString();
		// Select a thread id by attribute value
		
		int id = attribute_value.hashCode() % num_of_threads;
		
		// MAX : THIS CODE IS FOR THE HIRZEL TEST CASE!
//		int id = attribute_value.hashCode() % (num_of_threads - 1);
//		
//		if (attribute_value.equals("GOOG") || attribute_value.equals("AAPL") || attribute_value.equals("MSFT") ) {
//			id = num_of_threads - 1;
//		}
		
		// Add to thread blocking queue
		((ParallelThread)threads[id]).thread_input.add(new EvaluationInput(event, canStartInstance));
		// get all results from out queue and return them as a list of matches
		List<Match> result = new ArrayList<>();
		thread_output.drainTo(result);
		lock.unlock();
		return result;
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		// TODO: No idea what this does...
		List res = new ArrayList<Match>();
		for (int i = 0; i < num_of_threads; ++i) {
			lock.lock();
			res.addAll(threads[i].machine.validateTimeWindow(currentTime));
			lock.unlock();
		}
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
			res.addAll(threads[i].machine.getLastMatches());
		}
		return res;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		long size = 0;
		for (int i = 0; i < num_of_threads; ++i) {
			size += threads[i].machine.size();
		}
		return size;
	}

	@Override
	public String getStructureSummary() {
		// TODO Auto-generated method stub
		return "";
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
