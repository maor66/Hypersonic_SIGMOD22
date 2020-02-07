package sase.evaluation.data_parallel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import sase.base.Event;
import sase.config.MainConfig;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.nfa.NFA;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.pattern.Pattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.simulator.Environment;
import sase.specification.evaluation.EvaluationSpecification;
import sase.specification.evaluation.LazyNFAEvaluationSpecification;
import sase.specification.evaluation.ParallelEvaluationSpecification;
import sase.statistics.Statistics;
import sase.evaluation.nfa.eager.AND_SEQ_NFA;
import sase.evaluation.nfa.lazy.LazyChainNFA;

//abstract class to be inherited by Hirzel and RIP algorithms
public abstract class DataParallelEvaluationMechanism implements IEvaluationMechanism, IEvaluationMechanismInfo {

	private static final long timeToWaitForThreads = 10;
	
	protected int numOfThreads;
	protected final long timeWindow;
	protected ParallelThread[] threads;
	private final AtomicBoolean isInputFinished = new AtomicBoolean(false);
	private final AtomicBoolean isGroupInputFinished = new AtomicBoolean(false);
	private final AtomicInteger numberOfFinishedThreads = new AtomicInteger(0);



	public static DataParallelEvaluationMechanism singleton = null;
	
	// Input for the parallel thread
	protected class EvaluationInput {
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
		protected BlockingQueue<EvaluationInput> threadInput = new LinkedTransferQueue<>();
//		volatile long maxSize = 0;
		long machineTime = 0;
		private int handledEvents= 0;
		boolean isFinishedWithOwnGroupInput = false;

		public void run() {
			// Specific Hirzel code to run
			EvaluationInput evalInput = null;
			while (!interrupted()) {
				try {
					evalInput = threadInput.poll(3, TimeUnit.MILLISECONDS);
					if (evalInput == null) {
						if (MainConfig.latencyCalculation && isGroupInputFinished.get() && !isFinishedWithOwnGroupInput) {
							numberOfFinishedThreads.getAndIncrement();
							isFinishedWithOwnGroupInput = true;
						}
						if (isInputFinished.get()) {
							System.out.println(this.toString() + " machine time:  " + machineTime/1000000 + " handled events " + handledEvents);
							return;
						}
						continue;
					}
					processEvent(evalInput);
				} catch (InterruptedException e) {
					return;
				}
			}
			return;
		}
		public void resetGroupFinish()
		{
			isFinishedWithOwnGroupInput = false;
		}

		protected void processEvent(EvaluationInput evalInput) {
			Event event = evalInput.event;
			boolean canStartInstance = evalInput.canStartInstance;
			// if yes, run processNewEvent of nfa
			List<Match> result = null;
//			synchronized(machine) {
				result = machine.validateTimeWindow(event.getTimestamp());
				if (result == null) {
					result = machine.processNewEvent(event, canStartInstance);
					System.out.println("wrong");
				} else {
					handledEvents++;
//					try {
//						Thread.sleep(20);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					long time = System.nanoTime();
					List<Match> resProcessNewEvent = machine.processNewEvent(event, canStartInstance);
					machineTime += System.nanoTime() -time;
					if (resProcessNewEvent != null)
						result.addAll(resProcessNewEvent);
				}
//			}
			// add result of processNewEvent to out queue
			if (result != null) {
				Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.numberOfSynchronizationActions);
				threadOutput.addAll(result);
			}
//			maxSize = Math.max(maxSize, machine.size());
		}
		
		public void cancel() {
			interrupt();
		}
	}
	
	// output queue for all threads
	protected BlockingQueue<Match> threadOutput = new LinkedTransferQueue<>();
	
	private void SetUpThreads(Pattern pattern, EvaluationPlan evaluationPlan, EvaluationSpecification internalSpecification) {
		for (int i = 0; i < numOfThreads; ++i) {
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
		numOfThreads = specification.numOfThreads;
		threads = new ParallelThread[numOfThreads];
		// Build evaluation mechanism for internal nfa from specification
		EvaluationSpecification internalSpecification = specification.internalSpecification;
		SetUpThreads(pattern, evaluationPlan, internalSpecification);
		DataParallelEvaluationMechanism.singleton = this;
		timeWindow = pattern.getTimeWindow();

		// Create threads
		for (int i = 0; i < numOfThreads; ++i) {
			threads[i].start();
		}
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
	
	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		return new ArrayList<Match>();
	}

	@Override
	public void completeCreation(List<Pattern> patterns) {
		for (int i = 0; i < numOfThreads; ++i) {
			threads[i].machine.completeCreation(patterns);
		}
	}

	@Override
	public List<Match> getLastMatches() {
//		while (haveUnprocessedEvents()) {
//			try {
//				Thread.sleep(timeToWaitForThreads);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		isInputFinished.set(true);
		System.out.println("Waiting for threads" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Threads finished at" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		//need to receive and process matches that were created while we waited
		List<Match> matches = new ArrayList<Match>();
		threadOutput.drainTo(matches);
		for (Match match : matches) {
			Environment.getEnvironment().getStatisticsManager().updateFractionalStatistic(Statistics.averageLatency,
					match.getDetectionLatency());
			Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.matches);
		}
		
		List<Match> res = new ArrayList<Match>();
		for (int i = 0; i < numOfThreads; ++i) {
//			synchronized(threads[i].machine) {
				res.addAll(threads[i].machine.getLastMatches());
//			}
		}
		return res;
	}

	private boolean haveUnprocessedEvents() {
		for (ParallelThread parallelThread : threads) {
			if (!parallelThread.threadInput.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long size() {
		long size = 0;
		for (ParallelThread parallelThread : threads) {
//			size += parallelThread.maxSize;
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
		for (int i = 0; i < numOfThreads; ++i) {
//			synchronized(threads[i].machine) {
				threads[i].machine.removeConflictingInstances(matches);
//			}
		}
	}

	@Override
	public State getStateByAtomicCondition(AtomicCondition condition) {
		return null;
	}

	@Override
	public double getStateReachProbability(State state) {
		return 0;
	}
	
	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		if (!(((NFA)(threads[0].machine)).supportedEventTypes.contains(event.getType()))) {
			// irrelevant event
			return null;
		}
		scheduleEvent(new EvaluationInput(event, canStartInstance));
		List<Match> result = new ArrayList<Match>();
		threadOutput.drainTo(result);
		return result;
	}

	@Override
	public ArrayList<Match> waitForGroupToFinish()
	{
		isGroupInputFinished.set(true);
		ArrayList<Match> matches = new ArrayList<Match>();
		while (true)
		{
			try {
				Match m  = threadOutput.poll(3, TimeUnit.MILLISECONDS);
				if (m == null) {
					if (numberOfFinishedThreads.get() == numOfThreads) {
						break;
					}
				}
				else {
					matches.add(m);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		threadOutput.drainTo(matches); // instead - do a while (true) with polling each element
		//Should calc latency here?
		isGroupInputFinished.set(false);
		for (ParallelThread  t  : threads)
{
	t.resetGroupFinish();
}
		numberOfFinishedThreads.set(0);
		//Do nothing
		return matches;
	}

	protected abstract void scheduleEvent(EvaluationInput evaluationInput);
}
