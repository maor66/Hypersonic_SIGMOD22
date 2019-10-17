package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;

public class ParallelMatchBuffer extends ParallelBuffer {

    public ParallelMatchBuffer(long timeWindow) {
        super(timeWindow);
    }

    @Override
    protected int actuallyRemove(long removingCriteriaTimeStamp) {
        int removedItems = 0;
        for (int i = 0; i < buffer.size(); i++) {
            if (buffer.get(i).getEarliestTimestamp() + timeWindow < removingCriteriaTimeStamp) {
                if (i >= nextElementToRead) {
                    throw new RuntimeException("MB - Removing before read");
                }
                buffer.remove(i);
                removedItems++;
            }
        }
        return removedItems;
    }
}
