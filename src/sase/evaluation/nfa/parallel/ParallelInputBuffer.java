package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;

public class ParallelInputBuffer extends ParallelBuffer {
    public ParallelInputBuffer(long timeWindow) {
        super(timeWindow);
    }

    @Override
    protected int actuallyRemove(long removingCriteriaTimeStamp) {
        int removedItems = 0;
        ContainsEvent currEvent = buffer.get(0);
        while (currEvent.getTimestamp() + timeWindow   < removingCriteriaTimeStamp) {
            if (nextElementToRead <= removedItems) {
                System.out.println("next " + nextElementToRead + " Removed items " + removedItems + " ts " +removingCriteriaTimeStamp + " event " + currEvent);
                return removedItems;
            }
            buffer.remove(0);
            removedItems++;
            if (buffer.isEmpty()) {
                break;
            }
            currEvent = buffer.get(0);
        }
        return removedItems;
    }
}
