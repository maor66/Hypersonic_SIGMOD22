package sase.evaluation.data_parallel;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.data_parallel.DataParallelEvaluationMechanism.EvaluationInput;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.pattern.condition.base.AtomicCondition;
import sase.simulator.Environment;
import sase.specification.evaluation.ParallelEvaluationSpecification;
import sase.specification.evaluation.RIPEvaluationSpecification;
import sase.statistics.Statistics;

public final class RIPEvaluationMechanism extends DataParallelEvaluationMechanism {

	private long eventCount = 0;
	private long eventsPerThread = 0;
	private List<Event> events;
	private int currThread = 0;
	private int duplicatedEvents=0;

	private class WindowTooBigException extends Exception {
		public WindowTooBigException(String message) {
			super(message);
		}
	}

	public RIPEvaluationMechanism(Pattern pattern, RIPEvaluationSpecification specification, EvaluationPlan evaluationPlan) {
		super(pattern, specification, evaluationPlan);
		eventsPerThread = specification.eventsPerThread;
		events = new ArrayList<Event>();
	}

	private void CheckWindowLegal(long timeStampOfWindowEnd, Event event, long windowSize) throws WindowTooBigException {
		if (timeStampOfWindowEnd - events.get(0).getTimestamp() < windowSize) {
			// Window is too big!
			throw new WindowTooBigException("Window size input was too big.");
		}
	}

	@Override
	protected void scheduleEvent(EvaluationInput evaluationInput) {
		// Calculate the thread we should send next event to
		int newCurrThread = (int)((eventCount++ / eventsPerThread) % numOfThreads);
		// Add events to a list. After finished with one thread check which
		// intersecting events should be sent to the next thread
		events.add(evaluationInput.event.clone());
		// If last event came, check its timestamp and recalculate which events we should send to new thread
		if (newCurrThread != currThread) {
			long timeStampOfWindowEnd = evaluationInput.event.getTimestamp();
			// Make sure the input was correct - check that windowSize isn't accidentally bigger
			// than the whole window
			try {
				CheckWindowLegal(timeStampOfWindowEnd, evaluationInput.event, timeWindow);
			} catch (WindowTooBigException e) {
				e.printStackTrace();
			}
			for (Event storedEvent : events) {
				long difference = timeStampOfWindowEnd - storedEvent.getTimestamp();
				if (difference < timeWindow) {
					// Send event to next thread
					threads[newCurrThread].threadInput.add(new EvaluationInput(storedEvent, evaluationInput.canStartInstance));
					// This addition counts as new event for RIP thread event count
					++eventCount;
					duplicatedEvents++;
				}
			}
			events.clear();
		}
		currThread = newCurrThread;
		threads[currThread].threadInput.add(new EvaluationInput(evaluationInput.event, evaluationInput.canStartInstance));
		Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.numberOfSynchronizationActions);
	}
}
