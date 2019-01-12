package sase.evaluation.nfa.parallel;

public abstract class BufferWorker implements Runnable {
    ThreadContainers dataStorage;
    public ThreadContainers getDataStorage() {
        return dataStorage;
    }

}
