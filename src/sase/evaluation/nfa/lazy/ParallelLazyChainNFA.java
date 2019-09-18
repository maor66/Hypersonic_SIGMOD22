package sase.evaluation.nfa.lazy;

import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
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
import sase.simulator.Environment;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;
import sase.statistics.Statistics;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
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
    protected Map<TypedNFAState, Integer> stateToIBThreads = new HashMap<>();
    protected Map<TypedNFAState, Integer> stateToMBThreads = new HashMap<>();
    private BlockingQueue<Match> secondStateInputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Match> completeMatchOutputQueue;
    protected int numOfThreads;
    private CostModelTypes costModelType;
    private Double tlr;
    private List<EventType> eventTypes;
    private Pattern pattern;
    private double inputMatchThreadRatio;
    private List<Future<ThreadContainers.ParallelStatistics>> threadStatistics = new ArrayList<>();
    private long mainThreadIdleTime = 0;

    private class PrintMatchTimerTask extends TimerTask {

        Set<Match> matches;
        PrintMatchTimerTask(Set<Match> matches) {
            this.matches = matches;
        }
        @Override
        public void run() {
            System.out.println("Match: " + matches.size());
        }
    }
    
    private class NotEnoughThreadsException extends RuntimeException {
    	public NotEnoughThreadsException(String message) {
    		super(message);
    	}
    }

    public ParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, ParallelLazyNFAEvaluationSpecification specification) {
        super(pattern, evaluationPlan, specification.negationType);
        this.numOfThreads = specification.numOfThreads;
        this.costModelType = specification.costModelType;
        tlr = specification.throughputToLatencyRatio;
        this.eventTypes = (List<EventType>) pattern.getEventTypes();
        this.pattern = pattern;
        this.inputMatchThreadRatio = specification.inputMatchThreadRatio;
        System.out.println("Processors: " + Runtime.getRuntime().availableProcessors());
    }
    static boolean flag = true;
    @Override
    public List<Match> processNewEvent(Event event, boolean canStartInstance) {

        if (flag)
        {
            System.out.println("Starting at "+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            flag = false;
        }
            long time = System.nanoTime();

        TypedNFAState eventState = getStateByEventType(getWorkerAndInitialState(), event);
        if (null == eventState) {
            // The event has irrelevent type (not in the query)
            return null;
        }

        try {
            if (eventState.isInitial()) {
                LinkedBlockingQueue<Match> transferQueue = (LinkedBlockingQueue<Match>) secondStateInputQueue;
                transferQueue.put(new Match(Event.asList(event), System.currentTimeMillis()));
//                secondStateInputQueue.put(new Match(Event.asList(event), System.currentTimeMillis()));
            }
            else {
                LinkedBlockingQueue<Event> transferQueue = (LinkedBlockingQueue<Event>) eventInputQueues.get(eventState);
                transferQueue.put(event);
                Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.parallelBufferInsertions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainThreadIdleTime+= System.nanoTime() - time;

        return null;
    }

    @Override
    public List<Match> getLastMatches() {

        if (MainConfig.parallelDebugMode) {
            System.out.println("Starting poison pill at "  + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            System.out.println("Main thread idle time is "+ mainThreadIdleTime/1000000);
        }
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
        if (MainConfig.parallelDebugMode) {
            System.out.println("Finished poison pill at "  + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        }

        Set<Match> matches = new HashSet<>();
        int numberOfEndingMatches = 0;
        Timer printTimer = new Timer();
        if (MainConfig.parallelDebugMode) {
            printTimer.scheduleAtFixedRate(new PrintMatchTimerTask(matches),0, 20*1000);
        }
        while (true) {
            try {
                Match m = completeMatchOutputQueue.take();
                if (m.isLastInput()) {
                    if (MainConfig.parallelDebugMode) {
                        System.out.println("Recieved posion pill at "  + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                    }
                    numberOfEndingMatches++;
//                    System.out.println("Finisher " + numberOfEndingMatches);
                    TypedNFAState lastWorkerState = getWorkerStates().get(getWorkerStates().size() - 1);
                    if (numberOfEndingMatches == stateToIBThreads.get(lastWorkerState) + stateToMBThreads.get(lastWorkerState)) {
                        break;
                    }
                }
                else {
                    matches.add(m);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HashMap<Thread, HashMap<String, Long>> parallelStatistics = Environment.getEnvironment().getStatisticsManager().getParallelStatistics();
        for (Map.Entry<Thread, HashMap<String, Long>> entry : parallelStatistics.entrySet()) {
            System.out.println("Thread "+ entry.getKey() + " has "+ entry.getValue());
            for (Map.Entry <String, Long> e: entry.getValue().entrySet()) {
                Environment.getEnvironment().getStatisticsManager().updateDiscreteStatistic(e.getKey(), e.getValue());
            }
        }
        if (MainConfig.parallelDebugMode) {
            printTimer.cancel();
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

    protected void initializeThreads() {
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
                stateIBworkers.add(new InputBufferWorker(state, this.evaluationOrder, this.fullCondition.getEventTypes(),1, stateToMBThreads.getOrDefault(getNextWorkerStateOrNullIfLast(state), 1))); //TODO: not necessary the correct sequence order, have to validate it somehow
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
                threadStatistics.add(executor.submit(worker));
            }
            for (MatchBufferWorker worker: MBWorkers.get(state)) {
                threadStatistics.add(executor.submit(worker));
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
    protected List<TypedNFAState> getWorkerStates() {
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

    private void threadNumCalculation(int minThreadsPerState, List<Integer> inputBufferThreadsPerState,
    		List<Integer> matchBufferThreadsPerState, int numOfThreadsForState) {
    	int numOfInputBufferThreads = (int) (numOfThreadsForState * inputMatchThreadRatio);
    	// num of input buffer threads can't be 0
    	numOfInputBufferThreads = Math.max(numOfInputBufferThreads, 1);
    	inputBufferThreadsPerState.add(numOfInputBufferThreads);
		matchBufferThreadsPerState.add(numOfThreadsForState - numOfInputBufferThreads);
    }
    
    private void balanceThreads(List<TypedNFAState> nfaStates, List<Double> costOfStates, double totalCost,
    		List<Integer> inputBufferThreadsPerState, List<Integer> matchBufferThreadsPerState) {
    	int count = 0;
    	int threadsLeft = numOfThreads;
    	int minThreadsPerState = 2;
    	for (TypedNFAState state : nfaStates) {
    		int numOfThreadsForState = (int)(costOfStates.get(count++) / totalCost * numOfThreads);
			// We need at least 2 threads. One for input and one for match buffer
    		numOfThreadsForState = Math.max(numOfThreadsForState, minThreadsPerState);
    		threadNumCalculation(minThreadsPerState, inputBufferThreadsPerState, matchBufferThreadsPerState, numOfThreadsForState);
    		threadsLeft -= numOfThreadsForState;
    	}
    	
    	if (threadsLeft == 0) {
    		return;
    	}
    	
    	if (threadsLeft < 0) {
    		// Impossible. Do stupid thread balancing
    		System.out.println("Not enough threads for smart balancing. Dividing equally");
    		inputBufferThreadsPerState.clear();
    		matchBufferThreadsPerState.clear();
    		threadsLeft = numOfThreads;
    		for (TypedNFAState state : nfaStates) {
    			int numOfThreadsForInput = (int) (numOfThreads / nfaStates.size() * inputMatchThreadRatio);
    			numOfThreadsForInput = Math.max(numOfThreadsForInput, 1);
    			inputBufferThreadsPerState.add(numOfThreadsForInput);
    			int numOfThreadsForMatch = numOfThreads / nfaStates.size() - numOfThreadsForInput;
    			numOfThreadsForInput = Math.max(numOfThreadsForMatch, 1);
    			matchBufferThreadsPerState.add(numOfThreadsForMatch);
    			threadsLeft -= numOfThreadsForInput + numOfThreadsForMatch;
        	}
    		if (threadsLeft > 0) {
    			int inputThreads = (int)Math.ceil(threadsLeft / 2);
    			inputBufferThreadsPerState.set(inputBufferThreadsPerState.size() - 1,
    					inputBufferThreadsPerState.get(inputBufferThreadsPerState.size() - 1) + inputThreads);
    			matchBufferThreadsPerState.set(inputBufferThreadsPerState.size() - 1,
    					matchBufferThreadsPerState.get(inputBufferThreadsPerState.size() - 1) + threadsLeft - inputThreads);
    		}
    		return;
    	}
    	
    	Environment.getEnvironment().getStatisticsManager().updateDiscreteStatistic(Statistics.isSmartBalancing, 1);
    	
    	// Need to divide the rest of the threads to states by ratio
    	count = nfaStates.size();
    	while (threadsLeft > 0) {
    		int numOfThreadsForState = (int)(Math.ceil(costOfStates.get(--count) / totalCost * threadsLeft));
    		if (numOfThreadsForState == 1) {
    			inputBufferThreadsPerState.add(numOfThreadsForState);
    		} else {
    			threadNumCalculation(minThreadsPerState, inputBufferThreadsPerState, matchBufferThreadsPerState, numOfThreadsForState);
    		}
    		threadsLeft -= numOfThreadsForState;
    	}
    }
    
    public void initallizeThreadAllocation() {
    	// Check if we have enough threads to work with
    	List<TypedNFAState> nfaStates = getWorkerStates();
    	if (nfaStates.size() * 2 > numOfThreads) {
    		String res = String.format("Not enough threads passed. Need at least %d threads", nfaStates.size() * 2);
    		throw new NotEnoughThreadsException(res);
    	}
    	// MAX : fixing this to calculate num of threads without getting it as input    	
    	ICostModel costModel = CostModelFactory.createCostModel(costModelType, new Object[] { eventTypes, tlr});
    	// Cost of all the automata
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
    		}
    		// This is sum of costs till count's state. Need to calculate
			Double sumCosts = costModel.getOrderCost(pattern, eventTypesSoFar);
			costOfStates.add(sumCosts - sumOfLastState);
			sumOfLastState = sumCosts;
    	}
    	double totalCost = 0;
    	for (int i = 0; i < costOfStates.size(); ++i) {
    		totalCost += costOfStates.get(i);
    	}
    	// Calculate num of threads per state based on costs
    	List<Integer> inputBufferThreadsPerState = new ArrayList<>();
    	List<Integer> matchBufferThreadsPerState = new ArrayList<>();
    	
    	balanceThreads(nfaStates, costOfStates, totalCost, inputBufferThreadsPerState, matchBufferThreadsPerState);
    	
        int listIndex = 0;
        for (TypedNFAState state : nfaStates) {
            stateToIBThreads.put(state, inputBufferThreadsPerState.get(listIndex));
            stateToMBThreads.put(state, matchBufferThreadsPerState.get(listIndex++));
        }
        initializeThreads();
    }
}
