package sase.specification.workload;

import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.creation.PatternTypes;
import sase.specification.condition.ConditionSpecification;

import java.util.ArrayList;
import java.util.List;

public class ParallelPatternSpecification extends PatternSpecification {
	// Max : This class is unnecessary and I removed its use. Will need to remove it from code completely in the future
    private List<Integer> inputBufferThreadsPerState = new ArrayList<>();
    private List<Integer> matchBufferThreadsPerState = new ArrayList<>();
    public ParallelPatternSpecification(String name, PatternTypes type, Long timeWindow, String[][][] structure, String[] negatedEventNames, String[] iteratedEventNames, ConditionSpecification[] conditions, SlaVerifierTypes verifierType, int [][] threadsPerState) {
        super(name, type, timeWindow, structure, negatedEventNames, iteratedEventNames, conditions, verifierType);
        initializeThreadLists(threadsPerState);
    }

    public ParallelPatternSpecification(String name, PatternTypes type, Long timeWindow, String[][][] structure, ConditionSpecification[] conditions, SlaVerifierTypes verifierType,int [][] threadsPerState) {
        super(name, type, timeWindow, structure, conditions, verifierType);
        initializeThreadLists(threadsPerState);
    }

    private void initializeThreadLists(int [][] threadsPerState)
    {
        inputBufferThreadsPerState = getListOfThreadsFromArray(threadsPerState, 0);
        matchBufferThreadsPerState = getListOfThreadsFromArray(threadsPerState, 1);
    }

    private List<Integer> getListOfThreadsFromArray(int [][] threadsPerState, int internalIndex) {
        List<Integer> threads = new ArrayList<>();
        for (int i = 0; i < threadsPerState.length; i++) {
            threads.add(threadsPerState[i][internalIndex]);
        }
        return threads;
    }

    public List<Integer> getInputBufferThreadsPerState() {
        return inputBufferThreadsPerState;
    }

    public List<Integer> getMatchBufferThreadsPerState() {
        return matchBufferThreadsPerState;
    }
}
