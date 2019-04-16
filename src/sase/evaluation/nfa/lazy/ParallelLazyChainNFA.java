package sase.evaluation.nfa.lazy;

import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;
import sase.evaluation.nfa.lazy.order.cost.CostModelFactory;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.evaluation.nfa.parallel.InputBufferWorker;
import sase.evaluation.nfa.parallel.MatchBufferWorker;
import sase.evaluation.nfa.parallel.ThreadContainers;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ParallelLazyChainNFA extends LazyChainNFA {

    //TODO: Features - (2) Add support for different types of patterns (AND/OR/NEG)
    //TODO: Features - (3) Think if there are useless IB/MB workers, maybe in each state only new events or only new rPM can create matches due to timing constraints.
    //TODO: Features - (4) send events/rPM as batches to decrease synchronization actions
    //TODO: Features - (5) calculate number of events/rPM sent between states(and main thread) as a statistic
    //TODO: Features - (6) Use actual getSlice in MBW -  Implemented but it is possible that this affects the performance negatively, should consider removing
    //TODO: Features - (7) Remove commented-out code
    //TODO: Article - (1) Add scoping parameters use
    //TODO: Article - (2) Write about removing technique
    //TODO: Article - (3) Add optimization of first state (forward only)
    //TODO: Article - (4) Add implementation detail about finishing input with different amount of threads ("poison pill")
    //TODO: Article - (5) Should add something about minimum required threads?

    private ExecutorService executor;
    private Map<NFAState, List<InputBufferWorker>> IBWorkers;
    private Map<NFAState, List<MatchBufferWorker>> MBWorkers;
    private Map<TypedNFAState, BlockingQueue<Event>> eventInputQueues;
    private Map<TypedNFAState, Integer> stateToIBThreads = new HashMap<>();
    private Map<TypedNFAState, Integer> stateToMBThreads = new HashMap<>();
    private BlockingQueue<Match> secondStateInputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Match> completeMatchOutputQueue;
    private int numOfThreads;
    private CostModelTypes costModelType;
    private Double tlr;
    private List<EventType> eventTypes;
    private Pattern pattern;

    public ParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, ParallelLazyNFAEvaluationSpecification specification) {
        super(pattern, evaluationPlan, specification.negationType);
        this.numOfThreads = specification.numOfThreads;
        this.costModelType = specification.costModelType;
        tlr = specification.throughputToLatencyRatio;
        this.eventTypes = (List<EventType>) pattern.getEventTypes();
        this.pattern = pattern;
    }

    @Override
    public List<Match> processNewEvent(Event event, boolean canStartInstance) {

        TypedNFAState eventState = getStateByEventType(getWorkerAndInitialState(), event);
        if (null == eventState) {
            // The event has irrelevent type (not in the query)
            return null;
        }
        try {
            if (eventState.isInitial()) {
                LinkedBlockingQueue<Match> transferQueue = (LinkedBlockingQueue<Match>) secondStateInputQueue;
                transferQueue.put(new Match(Event.asList(event), System.currentTimeMillis()));
                secondStateInputQueue.put(new Match(Event.asList(event), System.currentTimeMillis()));
            }
            else {
                LinkedBlockingQueue<Event> transferQueue = (LinkedBlockingQueue<Event>) eventInputQueues.get(eventState);
                transferQueue.put(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Match> getLastMatches() {

            try {
                //Put finisher input ("poison pill") in each IB thread
                for (Map.Entry<TypedNFAState, BlockingQueue<Event>> entry : eventInputQueues.entrySet()) {
                    for (int i = 0; i < stateToIBThreads.get(entry.getKey()); i++) {
                        entry.getValue().put(new Event());
                    }
                }
                // Put finisher input to all MB threads in the first worker thread
                for (int i = 0; i < stateToMBThreads.get(getWorkerStates().get(0)); i++) {
                    secondStateInputQueue.put(new Match());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        Set<Match> matches = new HashSet<>();
        int numberOfEndingMatches = 0;
        while (true) {
            try {
                Match m = completeMatchOutputQueue.take();
                if (m.isLastInput()) {
                    numberOfEndingMatches++;
//                    System.out.println("Finisher " + numberOfEndingMatches);
                    TypedNFAState lastWorkerState = getWorkerStates().get(getWorkerStates().size() - 1);
                    if (numberOfEndingMatches == stateToIBThreads.get(lastWorkerState) + stateToMBThreads.get(lastWorkerState)) {
                        break;
                    }
                }
                else {
                    matches.add(m);
//                    System.out.println("Match" + matches.size() + ":" + m);
                }
//                System.out.println("Match" + matches.size() + ":" + matches.get(matches.size()-1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdownNow();
        return new ArrayList<>(matches);
    }
    
    @Override
    public void completeCreation(List<Pattern> patterns) {
    	super.completeCreation(patterns);
    	initallizeThreadAllocation();
    }

    private TypedNFAState getNextWorkerStateOrNullIfLast(TypedNFAState state)
    {
        int indexInWorkerStates = getWorkerStates().indexOf(state);
        return (indexInWorkerStates == getWorkerStates().size() - 1) ? null : getWorkerStates().get(indexInWorkerStates + 1);
    }

    private void initializeThreads() {
        IBWorkers = new HashMap<>();
        MBWorkers = new HashMap<>();
        eventInputQueues = new HashMap<>();
        executor = Executors.newFixedThreadPool((getTotalNumberOfThreads())); //TODO: maybe workstealingpool?

        int prevTotalThreads = 1;
        for (TypedNFAState state : getWorkerStates()) {
            // Construct the workers first so it will be possible to add them as parameters in the ThreadContainer c'tor. Probably should've used a better design pattern (builder?)
            List<InputBufferWorker> stateIBworkers = new ArrayList<>();
            List<MatchBufferWorker> stateMBworkers = new ArrayList<>();
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                stateIBworkers.add(new InputBufferWorker(state, this.evaluationOrder, this.supportedEventTypes,1, stateToMBThreads.getOrDefault(getNextWorkerStateOrNullIfLast(state), 1))); //TODO: not necessary the correct sequence order, have to validate it somehow
            }
            for (int i = 0; i < stateToMBThreads.get(state); i++) {
                stateMBworkers.add(new MatchBufferWorker(state, prevTotalThreads, stateToMBThreads.getOrDefault(getNextWorkerStateOrNullIfLast(state),1)));
            }
            prevTotalThreads = stateToIBThreads.get(state) + stateToMBThreads.get(state);
            IBWorkers.put(state, stateIBworkers);
            MBWorkers.put(state, stateMBworkers);
        }

        BlockingQueue<Event> inputQueue;
        BlockingQueue<Match> outputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Match> MBinputQueue = secondStateInputQueue;
        for (TypedNFAState state : getWorkerStates()) {
            //TODO: check that iterating on states by the correct order
            inputQueue = new LinkedBlockingQueue<>();
            eventInputQueues.put(state, inputQueue);
            outputQueue = new LinkedBlockingQueue<>();
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                //Every worker has an "equal" ThreadContainer but they must be different since each worker should have a unique sub-list
                ThreadContainers IBthreadData = new ThreadContainers(inputQueue,
                        MBWorkers.get(state),
                        outputQueue,
                        state.getEventType(),
                        timeWindow);
                IBWorkers.get(state).get(i).initializeDataStorage(IBthreadData);
            }
            for (int i = 0; i <stateToMBThreads.get(state); i++) {
                ThreadContainers MBthreadData = new ThreadContainers(MBinputQueue,
                        IBWorkers.get(state),
                        outputQueue,
                        state.getEventType(),
                        timeWindow);
                MBWorkers.get(state).get(i).initializeDataStorage(MBthreadData);
            }
            MBinputQueue = outputQueue;
        }
        completeMatchOutputQueue = outputQueue; // The final output queue is that of the final state and the main thread should get the matches from it

        for (TypedNFAState state : getWorkerStates()) {
            for (InputBufferWorker worker : IBWorkers.get(state)) {
                executor.execute(worker);
            }
            for (MatchBufferWorker worker: MBWorkers.get(state)) {
                executor.execute(worker);
            }
        }
    }

    private int getTotalNumberOfThreads() {
        int threadsSum = 0;
        for (int threads : stateToIBThreads.values()) {
            threadsSum+=threads;
        }
        for (int threads : stateToMBThreads.values()) {
            threadsSum+=threads;
        }
        return threadsSum;
    }


    private List<TypedNFAState> getWorkerAndInitialState()
    {
        List<NFAState> workerStream = states.stream().filter(state -> (!state.isAccepting() && !state.isRejecting()))
                .collect(Collectors.toList());
        return (List<TypedNFAState>)(List<?>) workerStream;
    }
    private List<TypedNFAState> getWorkerStates() {
        //events on the initial state are going directly to the match buffer of the second state, so need for them
        List<NFAState> workerStream = states.stream().filter(state -> (!state.isAccepting() && !state.isRejecting() && !state.isInitial()))
                .collect(Collectors.toList());
        return (List<TypedNFAState>)(List<?>) workerStream;
    }

    private TypedNFAState getStateByEventType(List<TypedNFAState> states, Event event) {
        for (TypedNFAState s : states) {
            if (s.getEventType().getName().equals(event.getType().getName())) {
                return s;
            }
        }
        return null;
    }

    public void initallizeThreadAllocation() {
    	// MAX : fixing this to calculate num of threads without getting it as input    	
    	ICostModel costModel = CostModelFactory.createCostModel(costModelType, new Object[] { eventTypes, tlr});
    	// Cost of all the automata
    	List<TypedNFAState> nfaStates = getWorkerStates();
    	List<Double> costOfStates = new ArrayList<>();
    	Double sumOfLastState = null;
		List<EventType> eventTypesSoFar = new ArrayList<>();
    	for (TypedNFAState state : nfaStates) {
			eventTypesSoFar.add(state.getEventType());
    		if (costOfStates.isEmpty()) {
    			Double firstCost = costModel.getOrderCost(pattern, eventTypesSoFar); 
    			costOfStates.add(firstCost);
    			sumOfLastState = firstCost;
    			continue;
    		} else {
    			// This is sum of costs till count's state. Need to calculate
    			Double sumCosts = costModel.getOrderCost(pattern, eventTypesSoFar);
    			costOfStates.add(sumCosts - sumOfLastState);
    			sumOfLastState = sumCosts;
    		}
    	}
    	double totalCost = 0;
    	for (int i = 0; i < costOfStates.size(); ++i) {
    		totalCost += costOfStates.get(i);
    	}
    	// Calculate num of threads per state based on costs
    	List<Integer> inputBufferThreadsPerState = new ArrayList<>();
    	List<Integer> matchBufferThreadsPerState = new ArrayList<>();
    	int count = 0;
    	int threadsLeft = numOfThreads;
    	for (TypedNFAState state : nfaStates) {
    		int numOfThreadsForState = (int)(costOfStates.get(count++) / totalCost * numOfThreads);
    		if (numOfThreadsForState < 2) {
    			// We need at least 2 threads. One for input and one for match buffer
    			numOfThreadsForState = 2;
    		}
    		inputBufferThreadsPerState.add(numOfThreadsForState / 2);
    		matchBufferThreadsPerState.add(numOfThreadsForState - numOfThreadsForState / 2);
    		threadsLeft -= numOfThreadsForState;
    	}
    	
    	if (threadsLeft != 0) {
    		// Need to divide the rest of the threads between the buffers
    		// I don't know what is the best way to do this so I will just add them somehow till Maor decides otherwise
    		// Usually this number should be below zero because we gave free threads to first states
    		inputBufferThreadsPerState.set(inputBufferThreadsPerState.size() - 1, inputBufferThreadsPerState.get(inputBufferThreadsPerState.size() - 1) + threadsLeft / 2);
    		matchBufferThreadsPerState.set(matchBufferThreadsPerState.size() - 1, matchBufferThreadsPerState.get(inputBufferThreadsPerState.size() - 1) + (threadsLeft - threadsLeft / 2));
    		// This should not happen but there could be a situation where we somehow reached 0 or negative threads.
    		// Might need a smarter algorithm for fixing this or for thread allocation in general
    	}
    	
        int listIndex = 0;
        for (TypedNFAState state : nfaStates) {
            stateToIBThreads.put(state, inputBufferThreadsPerState.get(listIndex));
            stateToMBThreads.put(state, matchBufferThreadsPerState.get(listIndex++));
        }
        initializeThreads();
    }
}
