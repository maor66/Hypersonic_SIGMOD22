package sase.evaluation.nfa.lazy;

import org.apache.commons.math3.analysis.function.Min;
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
import sase.evaluation.nfa.lazy.order.cost.ThroughputCostModel;
import sase.evaluation.nfa.parallel.*;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;
import sase.pattern.SimplePattern;
import sase.pattern.condition.Condition;
import sase.pattern.condition.base.CNFCondition;
import sase.simulator.Environment;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;
import sase.statistics.Statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private Map<TypedNFAState, ParallelInputBuffer> eventInputQueues;
    protected Map<TypedNFAState, Integer> stateToIBThreads = new HashMap<>();
    protected Map<TypedNFAState, Integer> stateToMBThreads = new HashMap<>();
    private ParallelMatchBuffer secondStateInputQueue;
    private ParallelMatchBuffer completeMatchOutputQueue;
    protected int numOfThreads;
    private CostModelTypes costModelType;
    private Double tlr;
    private List<EventType> eventTypes;
    private Pattern pattern;
    private double inputMatchThreadRatio;
    private List<Future<ThreadContainers.ParallelStatistics>> threadStatistics = new ArrayList<>();
    private long mainThreadIdleTime = 0;
    private CopyOnWriteArrayList<BufferWorker> finishedThreads = new CopyOnWriteArrayList<>();
    private AtomicBoolean isFinishedWithInput = new AtomicBoolean(false);

    private class PrintMatchTimerTask extends TimerTask {

        Set<Match> matches;
        PrintMatchTimerTask(Set<Match> matches) {
            this.matches = matches;
        }
        @Override
        public void run() {
            System.out.println("Match: " + matches.size()+ " Finished threads " + finishedThreads.size());
            finishedThreads.forEach(bufferWorker -> System.out.print(bufferWorker.thread.getName()+ ", "));

            getAllWorkers().forEach(worker -> System.out.print(worker.thread.getName() + " Thread state " + worker.thread.getState() + ", "));
            System.out.println("\n");

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
    
    @Override
    public List<Match> processNewEvent(Event event, boolean canStartInstance) {

        TypedNFAState eventState = getStateByEventType(getWorkerAndInitialState(), event);

        if (null == eventState) {
            // The event has irrelevent type (not in the query)
            return null;
        }

        try {
            if (eventState.isInitial()) {
                secondStateInputQueue.add(new Match(Event.asList(event), System.currentTimeMillis()));
            }
            else {
                ParallelInputBuffer buffer =  eventInputQueues.get(eventState);
                buffer.add(event);
                Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.parallelBufferInsertions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Match> getLastMatches() {

        isFinishedWithInput.set(true);
        if (MainConfig.parallelDebugMode) {
            System.out.println("Starting poison pill at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            System.out.println("Main thread idle time is " + mainThreadIdleTime / 1000000);
            System.out.println(("Actual thread used " + getAllWorkers().size()));
        }

        Set<Match> matches = new HashSet<>();
        Timer printTimer;
        if (MainConfig.parallelDebugMode) {
            printTimer = new Timer();
            printTimer.scheduleAtFixedRate(new PrintMatchTimerTask(matches), 0, 20 * 1000);
        }

        while (true) {
            Match m = null;
//                m = completeMatchOutputQueue.poll(1, TimeUnit.SECONDS);
                m = (Match) completeMatchOutputQueue.getElement();

            if (m == null) {
                if (finishedThreads.size() == this.getAllWorkers().size()) {
                    break;
                }
            }
            else {
                matches.add(m);
            }
        }

        if (MainConfig.statisticsDebugMode) {
            HashMap<Thread, HashMap<String, Long>> parallelStatistics = Environment.getEnvironment().getStatisticsManager().getParallelStatistics();
            for (Map.Entry<Thread, HashMap<String, Long>> entry : parallelStatistics.entrySet()) {
                System.out.println("Thread " + entry.getKey() + " has " + entry.getValue());
                for (Map.Entry<String, Long> e : entry.getValue().entrySet()) {
                    Environment.getEnvironment().getStatisticsManager().updateDiscreteStatistic(e.getKey(), e.getValue());
                }
            }
        }
        if (MainConfig.parallelDebugMode) {
            printTimer.cancel();
        }
        return new ArrayList<>(matches);
    }

    private List<? extends BufferWorker> getAllWorkers()
    {
        List <BufferWorker> allWorkers = MBWorkers.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        allWorkers.addAll(IBWorkers.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        return allWorkers;
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
        TypedNFAState previousState = null;
        for (TypedNFAState state : getWorkerStates()) {
            // Construct the workers first so it will be possible to add them as parameters in the ThreadContainer c'tor. Probably should've used a better design pattern (builder?)
            List<InputBufferWorker> stateIBworkers = new ArrayList<>();
            List<MatchBufferWorker> stateMBworkers = new ArrayList<>();
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                stateIBworkers.add(new InputBufferWorker(state, this.evaluationOrder, this.fullCondition.getEventTypes(),1,
                        stateToMBThreads.getOrDefault(getNextWorkerStateOrNullIfLast(state), 1), isFinishedWithInput, finishedThreads)); //TODO: not necessary the correct sequence order, have to validate it somehow
            }
            for (int i = 0; i < stateToMBThreads.get(state); i++) {
                List<BufferWorker> workersNeededToFinish;
                if (previousState == null) {
                    workersNeededToFinish = null;
                }
                else {
                    workersNeededToFinish = new ArrayList<>(IBWorkers.get(previousState));
                    workersNeededToFinish.addAll(MBWorkers.get(previousState));
                }
                stateMBworkers.add(new MatchBufferWorker(state, prevTotalThreads, stateToMBThreads.getOrDefault(getNextWorkerStateOrNullIfLast(state),1),
                        isFinishedWithInput, finishedThreads, workersNeededToFinish));
            }
            prevTotalThreads = stateToIBThreads.get(state) + stateToMBThreads.get(state);
            IBWorkers.put(state, stateIBworkers);
            MBWorkers.put(state, stateMBworkers);
            previousState = state;
        }

        ParallelInputBuffer inputQueue;
        ParallelMatchBuffer outputQueue = new ParallelMatchBuffer(timeWindow, 0);
        secondStateInputQueue = new ParallelMatchBuffer(timeWindow, MBWorkers.get(getWorkerStates().get(0)).size());
        ParallelMatchBuffer MBinputQueue = secondStateInputQueue;
        for (TypedNFAState state : getWorkerStates()) {
            //TODO: check that iterating on states by the correct order
            inputQueue = new ParallelInputBuffer(timeWindow, IBWorkers.get(state).size());
            eventInputQueues.put(state, inputQueue);
            outputQueue = new ParallelMatchBuffer(timeWindow, MBWorkers.get(state).size());
            MBinputQueue.setNumberOfWorkers(MBWorkers.get(state).size());
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                //Every worker has an "equal" ThreadContainer but they must be different since each worker should have a unique sub-list
                ThreadContainers IBthreadData = new ThreadContainers(inputQueue, MBinputQueue, outputQueue, state.getEventType(), timeWindow, i);
//                ThreadContainers IBthreadData = new ThreadContainers(inputQueue,
//                        MBWorkers.get(state),
//                        outputQueue,
//                        state.getEventType(),
//                        timeWindow);
                IBWorkers.get(state).get(i).initializeDataStorage(IBthreadData);
            }
            for (int i = 0; i <stateToMBThreads.get(state); i++) {
                ThreadContainers MBthreadData = new ThreadContainers(MBinputQueue, inputQueue, outputQueue, state.getEventType(), timeWindow, i);
//                ThreadContainers MBthreadData = new ThreadContainers(MBinputQueue,
//                        IBWorkers.get(state),
//                        outputQueue,
//                        state.getEventType(),
//                        timeWindow);
                MBWorkers.get(state).get(i).initializeDataStorage(MBthreadData);
            }
            MBinputQueue = outputQueue;
        }
        completeMatchOutputQueue = outputQueue; // The final output queue is that of the final state and the main thread should get the matches from it

        for (TypedNFAState state : getWorkerStates()) {
            for (InputBufferWorker worker : IBWorkers.get(state)) {
                Thread t = new Thread(worker);
                t.start();
//                executor.submit(worker);
            }
            for (MatchBufferWorker worker: MBWorkers.get(state)) {
                Thread t = new Thread(worker);
                t.start();
//                executor.submit(worker);
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
        final int MIN_THREADS_PER_STATE = 2;
        int threadsToAdd = 0;
        double costLeft = totalCost;
        for (Double costOfState : costOfStates) {
            int numOfThreadsForState = (int)(costOfState/ totalCost * threadsLeft);
            if (numOfThreadsForState < MIN_THREADS_PER_STATE) {
                threadsToAdd += MIN_THREADS_PER_STATE - numOfThreadsForState;
                costLeft -= costOfState;
            }
        }
        threadsLeft -= threadsToAdd;
        int threadToAllocate = threadsLeft;
        threadsLeft = numOfThreads;
        for (Double costOfState : costOfStates) {
            int numOfThreadsForState = (int) (costOfState / costLeft * threadToAllocate);
            numOfThreadsForState = Math.max(numOfThreadsForState, MIN_THREADS_PER_STATE);
            threadNumCalculation(MIN_THREADS_PER_STATE, inputBufferThreadsPerState, matchBufferThreadsPerState, numOfThreadsForState);
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
                threadNumCalculation(MIN_THREADS_PER_STATE, inputBufferThreadsPerState, matchBufferThreadsPerState, numOfThreadsForState);
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
        ThroughputCostModel costModel = (ThroughputCostModel) CostModelFactory.createCostModel(costModelType, new Object[] { eventTypes, tlr});
        // Cost of all the automata
        Double sumOfLastState = null;
        List<EventType> eventTypesSoFar = new ArrayList<>();
        List<Double> costOfStates = new ArrayList<>();
        costOfStates.add(evaluationOrder.getFullEvaluationOrder().get(0).getRate());
        for (TypedNFAState state : nfaStates) {
//            CNFCondition filteredCondition = ((CNFCondition) pattern.getCondition()).getConditionForTypes(new ArrayList<>(eventTypesSoFar), true);
            CNFCondition filteredCondition = (CNFCondition) state.getActualIncomingTransition().getCondition();
            costOfStates.add(costModel.getCostOfSingleState(filteredCondition,
                                                            state.getEventType(),
                                                            costOfStates.get(costOfStates.size() - 1)));
            eventTypesSoFar.add(state.getEventType());
            /*
            eventTypesSoFar.add(state.getEventType());
            if (costOfStates.isEmpty()) {
                double previousStateCost = 0;
                Double firstCost = costModel.getCostOfSingleState((filteredCondition), previousStateCost);
                costOfStates.add(firstCost);
                sumOfLastState = firstCost;
                continue;
            }
            // This is sum of costs till count's state. Need to calculate
            Double sumCosts = costModel.getOrderCost(createDummyPattern(filteredCondition), evaluationOrder.getFullEvaluationOrder());
            costOfStates.add(sumCosts - sumOfLastState);
            sumOfLastState = sumCosts;
            */
        }
        costOfStates.remove(0);
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
