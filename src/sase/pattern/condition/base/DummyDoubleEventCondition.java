package sase.pattern.condition.base;

import sase.base.Event;
import sase.base.EventType;

public class DummyDoubleEventCondition extends DoubleEventCondition {

	private double trueProbability;
	private int timeTillDone;
	
	// trueProbability should be between 0 and 1
	// timeTilLDone should be in milliseconds
	public DummyDoubleEventCondition(EventType firstType, EventType secondType, double trueProbability, int timeTillDone) {
		super(firstType, secondType);
		this.trueProbability = trueProbability;
		this.timeTillDone = timeTillDone;
	}

	@Override
	protected boolean verifyDoubleEvent(Event firstEvent, Event secondEvent) {
//		try {
//			Thread.sleep(timeTillDone);
//		} catch (InterruptedException e) {
////			e.printStackTrace();
//			// Not sure if I should do anything here
//		}
//		return (firstEvent.getSequenceNumber()%40 * secondEvent.getSequenceNumber() %40) /100.0 < trueProbability;
		return (firstEvent.getSequenceNumber()%10.0) /10.0 < trueProbability;
//		return Math.random() < trueProbability;
	}

}
