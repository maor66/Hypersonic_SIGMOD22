package sase.evaluation.nfa.parallel;

public interface Worker extends Runnable {
    Thread thread = Thread.currentThread();

    void resetGroupFinish();

    long size();
}
