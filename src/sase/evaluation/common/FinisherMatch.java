package sase.evaluation.common;

public class FinisherMatch extends Match {

    public FinisherMatch() {

    }

    @Override
    public long getEarliestTimestamp() {
        return Long.MAX_VALUE;
    }
}
