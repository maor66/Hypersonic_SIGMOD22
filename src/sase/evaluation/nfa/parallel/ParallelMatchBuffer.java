package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.evaluation.common.Match;

import java.util.ListIterator;

public class ParallelMatchBuffer extends ParallelBuffer {


    public ParallelMatchBuffer(long timeWindow, int totalNumberOfWorkers) {
        super(timeWindow, totalNumberOfWorkers);
    }

    @Override
    protected void actuallyRemove(long removingCriteriaTimeStamp, int workerIndex) {
//        buffer.removeIf(element -> element.getEarliestTimestamp() + timeWindow   < removingCriteriaTimeStamp);
//        int removedItems = 0;
        ListIterator<ContainsEvent> iterator = buffer.listIterator(getStartingIteratorIndex(workerIndex));
        int finishIndex = getStartingIteratorIndex(workerIndex + 1);

        while (iterator.hasNext() && iterator.nextIndex() < finishIndex) {
            Match match = (Match) iterator.next();
            if (match.getEarliestTimestamp() + timeWindow < removingCriteriaTimeStamp) {
////                if (iterator.nextIndex() >= nextElementToRead) {
////                    System.out.println("MB removing: next " + nextElementToRead + " Removed items " + removedItems + " ts " +removingCriteriaTimeStamp + " event "  + match);
////                    return removedItems;                }
//                removedItems++;
                iterator.remove();
            }
        }
//        System.out.println("Buffer size " + iterator.nextIndex());
    }
}
