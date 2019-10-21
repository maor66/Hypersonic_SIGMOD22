package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;

import java.util.ListIterator;

public class ParallelInputBuffer extends ParallelBuffer {

    public ParallelInputBuffer(long timeWindow, int totalNumberOfWorkers) {
        super(timeWindow, totalNumberOfWorkers);
    }

    @Override
    protected void actuallyRemove(long removingCriteriaTimeStamp, int workerIndex) {
//        buffer.removeIf(element -> element.getEarliestTimestamp() + timeWindow   < removingCriteriaTimeStamp);

        ListIterator<ContainsEvent> iterator = buffer.listIterator(getStartingIteratorIndex(workerIndex));
        int finishIndex = getStartingIteratorIndex(workerIndex + 1);
        while (iterator.hasNext() && iterator.nextIndex() < finishIndex) {
            Event currEvent = (Event) iterator.next();
            if (currEvent.getTimestamp() + timeWindow >= removingCriteriaTimeStamp) {
                break;
            }
            iterator.remove();
        }
    }
}
