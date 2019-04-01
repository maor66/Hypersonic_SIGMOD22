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
	
	public class HirzelThread extends ParallelThread {
		protected void processEvent(EvaluationInput evalInput, Event event, boolean canStartInstance) {
			event = evalInput.event;
			canStartInstance = evalInput.canStartInstance;
			// if yes, run processNewEvent of nfa
			threadLock.lock();
			List<Match> result = machine.validateTimeWindow(event.getTimestamp());
			if (result == null) {
				result = machine.processNewEvent(event, canStartInstance);
			} else {
				List<Match> resProcessNewEvent = machine.processNewEvent(event, canStartInstance);
				if (resProcessNewEvent != null)
					result.addAll(resProcessNewEvent);
			}
			
			threadLock.unlock();
			// add result of processNewEvent to out queue
			if (result != null) {
				threadOutput.addAll(result);
			}
		}
	}
	
	public HirzelEvaluationMechanism(Pattern pattern, HirzelEvaluationSpecification specification, EvaluationPlan evaluationPlan) {
		super(pattern, specification, evaluationPlan);
		this.attribute = specification.attribute;

		// Create threads
		for (int i = 0; i < num_of_threads; ++i) {
			IEvaluationMechanism machine = threads[i].machine;
			threads[i] = new HirzelThread() {		
				@Override
				public void run() {
					// Specific Hirzel code to run
					EvaluationInput evalInput = null;
					Event event = null;
					boolean canStartInstance = false;
					List<Match> result;
					while (!interrupted()) {
						try {
							evalInput = threadInput.take();
						} catch (InterruptedException e) {
							processEvent(evalInput, event, canStartInstance);
							return;
						} finally {
							processEvent(evalInput, event, canStartInstance);
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
		// Locks termporarily removed until fixed concurrency issues
		List res = new ArrayList<Match>();
//		for (int i = 0; i < num_of_threads; ++i) {
//			try {
//				res.addAll(threads[i].machine.validateTimeWindow(currentTime));	
//			} finally {
//			}
//		}
		return res;
	}

	@Override
	public void completeCreation(List<Pattern> patterns) {
		// Locks termporarily removed until fixed concurrency issues
		for (int i = 0; i < num_of_threads; ++i) {
			try {
				threads[i].machine.completeCreation(patterns);
			} finally {
			}
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
