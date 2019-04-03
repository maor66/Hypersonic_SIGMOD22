package sase.evaluation.data_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.EvaluationMechanismFactory;
import sase.evaluation.EvaluationPlanCreator;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.evaluation.data_parallel.DataParallelEvaluationMechanism.EvaluationInput;
import sase.evaluation.data_parallel.DataParallelEvaluationMechanism.ParallelThread;
import sase.evaluation.nfa.NFA;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.pattern.Pattern;
import sase.simulator.Environment;
import sase.specification.evaluation.EvaluationSpecification;
import sase.specification.evaluation.LazyNFAEvaluationSpecification;
import sase.specification.evaluation.ParallelEvaluationSpecification;
import sase.evaluation.nfa.eager.AND_SEQ_NFA;
import sase.evaluation.nfa.lazy.LazyChainNFA;

public abstract class DataParallelEvaluationMechanism implements IEvaluationMechanism, IEvaluationMechanismInfo {

	// abstract class to be inherited by Hirzel and RIP algorithms
	protected int num_of_threads;
	protected ParallelThread threads[];
	public Lock lock = new ReentrantLock();
	
	public static DataParallelEvaluationMechanism singleton = null;
	
	// Input for the parallel thread
	public class EvaluationInput {
		public Event event;
		public boolean canStartInstance;
		
		public EvaluationInput(Event event, boolean canStartInstance) {
			this.event = event;
			this.canStartInstance = canStartInstance;
		}
	}
	
	// class to run threads
	public class ParallelThread extends Thread {
		public IEvaluationMechanism machine;
		protected BlockingQueue<EvaluationInput> threadInput = new LinkedBlockingQueue<>();
		
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
		
		protected void processEvent(EvaluationInput evalInput, Event event, boolean canStartInstance) {
			event = evalInput.event;
			canStartInstance = evalInput.canStartInstance;
			// if yes, run processNewEvent of nfa
			lock.lock();
			List<Match> result = machine.validateTimeWindow(event.getTimestamp());
			if (result == null) {
				result = machine.processNewEvent(event, canStartInstance);
			} else {
				List<Match> resProcessNewEvent = machine.processNewEvent(event, canStartInstance);
				if (resProcessNewEvent != null)
					result.addAll(resProcessNewEvent);
			}
			
			// add result of processNewEvent to out queue
			if (result != null) {
				threadOutput.addAll(result);
			}
			lock.unlock();
		}
		
		public void cancel() {
			interrupt();
		}
	}
	
	// output queue for all threads
	protected BlockingQueue<Match> threadOutput = new LinkedBlockingQueue<>();
	
	private void SetUpThreads(Pattern pattern, EvaluationPlan evaluationPlan, EvaluationSpecification internalSpecification) {
		for (int i = 0; i < num_of_threads; ++i) {
			threads[i] = new ParallelThread();
			switch (internalSpecification.type) {
				case EAGER:
					threads[i].machine = new AND_SEQ_NFA(pattern);
					break;
				case LAZY_CHAIN:
					threads[i].machine = new LazyChainNFA(pattern, evaluationPlan, 
							((LazyNFAEvaluationSpecification)internalSpecification).negationType);
					break;
				case TREE:
					threads[i].machine = new TreeEvaluationMechanism(pattern, evaluationPlan);
					break;
				case LAZY_TREE:
				case MULTI_PATTERN_TREE:
				case MULTI_PATTERN_MULTI_TREE:
				default:
					throw new RuntimeException("Illegal specification for NFA/Tree");
			}
		}
	}
	
	public DataParallelEvaluationMechanism(Pattern pattern, ParallelEvaluationSpecification specification, EvaluationPlan evaluationPlan) {
		// TODO: do something with pattern and internal evaluation specification to create n threads 
		num_of_threads = specification.num_of_threeads;
		threads = new ParallelThread[num_of_threads];
		// Build evaluation mechanism for internal nfa from specification
		EvaluationSpecification internalSpecification = specification.internalSpecification;
		SetUpThreads(pattern, evaluationPlan, internalSpecification);
		DataParallelEvaluationMechanism.singleton = this;
	}
	
	public static void killAllThreads() {
		if (singleton == null) {
			return;
		}
		int i = 0;
		for (Thread thread : singleton.threads) {
			System.out.println("-I- Killing thread " + i++);
			((ParallelThread)thread).cancel();
		}
		singleton = null;
	}
}
