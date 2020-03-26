package sase.pattern.condition.base;

import java.util.List;
import java.util.concurrent.locks.StampedLock;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;

/**
 * Represents a condition which involves attributes of a pair of primitive events.
 */
public abstract class DoubleEventCondition extends AtomicCondition {
	
	protected final EventType firstType;
	protected final EventType secondType;
	
	public DoubleEventCondition(EventType firstType, EventType secondType, Double selectivity) {
		super(selectivity);
		this.firstType = firstType;
		this.secondType = secondType;
		eventTypes.add(firstType);
		eventTypes.add(secondType);
		if (selectivity == null && 
			!MainConfig.isSelectivityMonitoringAllowed && 
			!MainConfig.conditionSelectivityMeasurementMode) {
				setSelectivityByEstimate();
		}
	}
	
	public DoubleEventCondition(EventType firstType, EventType secondType) {
		this(firstType, secondType, null);
	}
	public static String condPrint = "";
	public static StampedLock lock = new StampedLock();
	@Override
	protected boolean actuallyVerify(List<Event> events) {
		Event firstEvent = null;
		Event secondEvent = null;
		for (Event event : events) {
			if (event.getType() == eventTypes.get(0))
				firstEvent = event;
			else if (event.getType() == eventTypes.get(1))
				secondEvent = event;

			if (firstEvent != null && secondEvent != null) {
//				if (firstEvent.getSequenceNumber() > secondEvent.getSequenceNumber()) {
//					System.out.println(firstEvent+" ,,, "+ secondEvent);
//					try{
//						throw new Exception();
//					} catch (Exception e) {
//						e.printStackTrace();
//						System.out.println(e.getStackTrace()[4]);
//					}
//				}
//				long stamp = lock.writeLock();
//				condPrint+=("Comparing: "+ firstEvent.toString() + " , " +secondEvent.toString()+"\n");
//				lock.unlockWrite(stamp);
				if (firstEvent.getSequenceNumber() < secondEvent.getSequenceNumber()) {
//					Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.parallelComputations);
//					Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.computations);
				}
//				for (long j=0; j < 10; j++) {
//				for (long i=0;i < (long)(21999); i++);
				boolean b = verifyDoubleEvent(firstEvent, secondEvent);
//				System.out.println(("Comparing: "+ firstEvent.toString() + " , " +secondEvent.toString()+"result is " + b));
				return b;
			}
		}
		return false;
	}
	
	public EventType getLeftEventType() {
		return eventTypes.get(0);
	}
	
	public EventType getRightEventType() {
		return eventTypes.get(1);
	}

	@Override
	protected String getConditionKey() {
		return String.format("%s:%s", firstType.getName(), secondType.getName());
	}
	
	public abstract boolean verifyDoubleEvent(Event firstEvent, Event secondEvent);
}
