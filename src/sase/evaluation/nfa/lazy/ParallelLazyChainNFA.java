package sase.evaluation.nfa.lazy;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.config.MainConfig;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.InstanceStorage;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.order.cost.CostModelFactory;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;
import sase.evaluation.nfa.lazy.order.cost.ThroughputCostModel;
import sase.evaluation.nfa.parallel.*;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;
import sase.simulator.Environment;
import sase.specification.evaluation.ParallelLazyNFAEvaluationSpecification;
import sase.statistics.Statistics;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


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
    private Map<NFAState, List<Worker>> IBWorkers;
    private Map<NFAState, List<Worker>> MBWorkers;
    private Map<TypedNFAState, List<ParallelQueue<Event>>> eventInputQueues;
    protected Map<TypedNFAState, Integer> stateToIBThreads = new HashMap<>();
    protected Map<TypedNFAState, Integer> stateToMBThreads = new HashMap<>();
    private List<ParallelQueue<Match>> secondStateInputQueue = Collections.singletonList(new ParallelQueue<>());
    private List<ParallelQueue<Match>> completeMatchOutputQueue;
    protected int numOfThreads;
    private BufferWorker dummyWorkerNeededForFinish = new BufferWorker();
    private CostModelTypes costModelType;
    private Double tlr;
    private List<EventType> eventTypes;
    private Pattern pattern;
    private double inputMatchThreadRatio;
    private List<Future<ThreadContainers.ParallelStatistics>> threadStatistics = new ArrayList<>();
    private long mainThreadIdleTime = 0;
    private CopyOnWriteArrayList<Worker> finishedThreads = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Worker> finishedWithGroup = new CopyOnWriteArrayList<>();
    private AtomicBoolean isFinishedWithInput = new AtomicBoolean(false);
    private List<ParallelQueue<Match>>  allMatchQueues = new ArrayList<>();
    private final boolean isMyAlgorithm;

    private class PrintMatchTimerTask extends TimerTask {

        Set<Match> matches;
        PrintMatchTimerTask(Set<Match> matches) {
            this.matches = matches;
        }
        @Override
        public void run() {
            System.out.println("Match: " + matches.size()+ " Finished threads " + finishedThreads.size());
            finishedThreads.forEach(bufferWorker -> {if (bufferWorker != dummyWorkerNeededForFinish) {
                System.out.print(bufferWorker.thread.getName()+ ", "); }
            });

            getAllWorkers().forEach(worker -> System.out.print(worker.thread.getName() + " Thread state " + worker.thread.getState() + ", "));
            System.out.println("\n");

        }
    }

    private class NotEnoughThreadsException extends RuntimeException {
        public NotEnoughThreadsException(String message) {
            super(message);
        }
    }

    public ParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, boolean isMyAlgorithm, ParallelLazyNFAEvaluationSpecification specification) {
        super(pattern, evaluationPlan, specification.negationType);
        this.numOfThreads = specification.numOfThreads;
        this.costModelType = specification.costModelType;
        tlr = specification.throughputToLatencyRatio;
        this.eventTypes = (List<EventType>) pattern.getEventTypes();
        this.pattern = pattern;
        this.inputMatchThreadRatio = specification.inputMatchThreadRatio;
        this.isMyAlgorithm = isMyAlgorithm;
        System.out.println("Processors: " + Runtime.getRuntime().availableProcessors());
    }
    
    @Override
    public List<Match> processNewEvent(Event event, boolean canStartInstance) {

        TypedNFAState eventState = getStateByEventType(getWorkerAndInitialState(), event);

        if (null == eventState) {
            // The event has irrelevent type (not in the query)
            return null;
        }
        Environment.getEnvironment().getStatisticsManager().incrementDiscreteStatistic(Statistics.events);
        try {
            if (!isMyAlgorithm) {
                if (eventState.isInitial()) {
                    /// This is actually a match queue so need to duplicate it
                    for (ParallelQueue<Match> queue : secondStateInputQueue) {
                        queue.put(List.of(new Match(Event.asList(event))));
                    }
                }
                else {
                    ParallelQueue<ContainsEvent> chosenQueue = chooseEventQueue((List<ParallelQueue<ContainsEvent>>) (List<?>) eventInputQueues.get(eventState));
                    chosenQueue.put(List.of(event));
                }
                return null;
            }
            if (eventState.isInitial()) {
                for (ParallelQueue<Match> transferQueue : secondStateInputQueue) {
                    transferQueue.put(List.of(new Match(Event.asList(event))));
                }
            }
            else {
                for (ParallelQueue<Event> transferQueue : eventInputQueues.get(eventState)) {
                    transferQueue.put(List.of(event));
                    Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.parallelBufferInsertions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ParallelQueue<ContainsEvent> chooseEventQueue(List<ParallelQueue<ContainsEvent>> queueList)
    {
        //TODO: Using JSQ (shortest queue) instead of LLSF (the least loaded thread in terms of memory)
        long shortestSize = Integer.MAX_VALUE;
        ParallelQueue<ContainsEvent> shortestQueue = null;
        for (ParallelQueue<ContainsEvent> inputQueue : queueList) {
            long queueSize = inputQueue.size();
            if (queueSize < shortestSize) {
                shortestSize = queueSize;
                shortestQueue = inputQueue;
            }
        }
        return shortestQueue;
    }

    @Override
    public ArrayList<Match> waitForGroupToFinish()
    {
        finishedWithGroup.add(dummyWorkerNeededForFinish);
        HashSet<Match> matches = new HashSet<>();
        while (true) {
            List<Match> m = null;
            for (ParallelQueue<Match> outputQueue : completeMatchOutputQueue) {
                List<Match> batch = outputQueue.poll(1, TimeUnit.SECONDS);
                if (m == null) {
                    m = batch;
                }
                m.addAll(batch);
            }
            if (m == null) {
                if (finishedWithGroup.size() - 1 == this.getAllWorkers().size()) {
                    break;
                }
            }
            else {
                for (Match match : m)
                matches.add(match);
            }
        }
        finishedWithGroup.clear();
        for (Worker worker: getAllWorkers())
        {
            worker.resetGroupFinish();
        }
        return new ArrayList<>(matches);

    }

    @Override
    public List<Match> getLastMatches() {

        isFinishedWithInput.set(true);
        finishedThreads.add(dummyWorkerNeededForFinish);
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

        boolean flag = true;
        while (true) {
            List<Match> m = null;
            if (flag&& finishedThreads.size() - 1 ==this.getAllWorkers().size()) {
                System.out.println("So far got matches - " + matches.size());
                flag = false;
            }
            for (ParallelQueue<Match> outputQueue : completeMatchOutputQueue) {
                List<Match> batch = outputQueue.poll(1, TimeUnit.SECONDS);
                if (m == null) {
                    m = batch;
                }
                else if (batch != null) {
                    m.addAll(batch);
                }
            }

            if (m == null) {
                if (finishedThreads.size() - 1 == this.getAllWorkers().size()) {
                    break;
                }
            }
            else {
                for (Match match : m)
                matches.add(match);
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
    @Override
    public List<Match> validateTimeWindow(long currentTime, Event event) {return null; }
    private List<? extends Worker> getAllWorkers()
    {
        List <Worker> allWorkers = MBWorkers.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        allWorkers.addAll(IBWorkers.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        return allWorkers;
    }

    public void startThreads() {
        for (TypedNFAState state : getWorkerStates()) { //TODO: Add delay until mechanism actually start sedning events - we need to add the option to have the first partial match wait a bit before switching to getting events
            for (Worker worker : IBWorkers.get(state)) {
                Thread t = new Thread(worker);
                t.start();
//                executor.submit(worker);
            }
            if (isMyAlgorithm) {
                for (Worker worker : MBWorkers.get(state)) {
                    Thread t = new Thread(worker);
                    t.start();
//                executor.submit(worker);
                }
            }
        }
    }

    @Override
    public long size() {
        int queuesSize = 0;
        int partialMatchSize = 2;
        for (ParallelQueue<Match>  q : allMatchQueues) {
            long individualQueueSize = q.getMaxSize();
            queuesSize += individualQueueSize * partialMatchSize * EventTypesManager.getInstance().getAverageEventSize();

            System.out.println("Max elements in queue is " +q.getMaxSize());
            partialMatchSize++;
        }

        return queuesSize + getAllWorkers().stream().mapToLong(Worker::size).sum();
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
        if (!isMyAlgorithm) {
            createWorkersForSplitDuplicateAlgorithm();
            return;
        }

        TypedNFAState previousState = null;
        ParallelQueue<Match> partialMatchInput = secondStateInputQueue.get(0);
        for (TypedNFAState state : getWorkerStates()) {
            // Construct the workers first so it will be possible to add them as parameters in the ThreadContainer c'tor. Probably should've used a better design pattern (builder?)
            List<Worker> stateIBworkers = new ArrayList<>();
            List<Worker> stateMBworkers = new ArrayList<>();

            List<ThreadContainers> eventThreadContainers = new CopyOnWriteArrayList<>();
            List<ThreadContainers> partialMatchThreadContainers = new CopyOnWriteArrayList<>();
            ParallelQueue<Match> matchesOutput = new ParallelQueue<Match>();
            allMatchQueues.add(matchesOutput);
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                eventThreadContainers.add(createThreadContainerByState(state,matchesOutput));
            }
            for (int i = 0; i < stateToMBThreads.get(state); i++) {
                partialMatchThreadContainers.add(createThreadContainerByState(state, matchesOutput));
            }
            ParallelQueue<Event> eventInput = new ParallelQueue<Event>();
            eventInputQueues.put(state, Collections.singletonList(eventInput));

            List<Worker> workersNeededToFinish;
            if (previousState == null) { //First state in the chain - Add a dummy worker to be changed by main
                workersNeededToFinish = new ArrayList<>();
                workersNeededToFinish.add(dummyWorkerNeededForFinish);
            }
            else { // Inner/Last state - Add all previous state's workers
                workersNeededToFinish = new ArrayList<>(IBWorkers.get(previousState));
                workersNeededToFinish.addAll(MBWorkers.get(previousState));
            }

            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                stateIBworkers.add(new BufferWorker(state, eventInput, partialMatchInput, eventThreadContainers.get(i),eventThreadContainers, partialMatchThreadContainers , finishedThreads, finishedWithGroup, workersNeededToFinish, true));
            }
            for (int i = 0; i < stateToMBThreads.get(state); i++) {
                stateMBworkers.add(new BufferWorker(state, eventInput, partialMatchInput,partialMatchThreadContainers.get(i), eventThreadContainers, partialMatchThreadContainers,  finishedThreads, finishedWithGroup, workersNeededToFinish, false));
            }
            IBWorkers.put(state, stateIBworkers);
            MBWorkers.put(state, stateMBworkers);
            previousState = state;
            partialMatchInput = matchesOutput;
        }
        completeMatchOutputQueue = Collections.singletonList(partialMatchInput); // The final output queue is that of the final state and the main thread should get the matches from it
        allMatchQueues.remove(partialMatchInput);
//        if (!MainConfig.latencyCalculation) {
//            for (ParallelQueue<Match> outputQueue : allMatchQueues) {
//                outputQueue.setSizeLimit(1000);
//            }
//        }
//        allMatchQueues.get(0).setSizeLimit(500);
//        allMatchQueues.get(1).setSizeLimit(500);
//        allMatchQueues.get(2).setSizeLimit(500);
//        allMatchQueues.get(3).setSizeLimit(500);
//        completeMatchOutputQueue.setSizeLimit(Integer.MAX_VALUE);
//        for (TypedNFAState state : getWorkerStates()) {
//            for (int i = 0; i < stateToIBThreads.get(state); i++) {
//                IBWorkers.get(state).get(i).initializeOppositeWorkers(MBWorkers.get(state), IBWorkers.get(state));
//            }
//            for (int i = 0; i < stateToMBThreads.get(state); i++) {
//                MBWorkers.get(state).get(i).initializeOppositeWorkers(IBWorkers.get(state), MBWorkers.get(state));
//            }
//
//        }


    }

    private void createWorkersForSplitDuplicateAlgorithm()
    {
        TypedNFAState previousState = null;
        secondStateInputQueue = new LinkedList<>();
        for (int i = 0; i < stateToIBThreads.get(getWorkerStates().get(0)); i++) {
            secondStateInputQueue.add(new ParallelQueue<>());
        }
        List<ParallelQueue<Match>> partialMatchInput = secondStateInputQueue;

        for (TypedNFAState state : getWorkerStates()) {
            List<Worker> stateIBworkers = new ArrayList<>();

            //Create "split" input queues
            List<ParallelQueue<Event>> eventInputs = new ArrayList<>();
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                eventInputs.add(new ParallelQueue<>());
            }
            eventInputQueues.put(state, eventInputs);

            //Create "duplicate" output queues
            List<ParallelQueue<Match>> duplicatesOutputs = new ArrayList<>();
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                //Actually creates a number based on the number of threads in this state instead of the next. Allocation is uniform so it is fine
                duplicatesOutputs.add(new ParallelQueue<>());
            }
            //Create ThreadContainers with multiple outputs
            List<ThreadContainers> eventThreadContainers = new CopyOnWriteArrayList<>();
            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                eventThreadContainers.add(createThreadContainerByState(state,duplicatesOutputs));
            }
            //Create "end conditions"
            List<Worker> workersNeededToFinish;
            if (previousState == null) { //First state in the chain - Add a dummy worker to be changed by main
                workersNeededToFinish = new ArrayList<>();
                workersNeededToFinish.add(dummyWorkerNeededForFinish);
            }
            else { // Inner/Last state - Add all previous state's workers
                workersNeededToFinish = new ArrayList<>(IBWorkers.get(previousState));
            }

            for (int i = 0; i < stateToIBThreads.get(state); i++) {
                stateIBworkers.add(new SplitDuplicateWorker(state, eventInputs.get(i), partialMatchInput.get(i), eventThreadContainers.get(i), finishedThreads, finishedWithGroup, workersNeededToFinish));
            }
            IBWorkers.put(state, stateIBworkers);
            partialMatchInput = duplicatesOutputs;
            previousState = state;
        }
        completeMatchOutputQueue = partialMatchInput;
    }

    private ThreadContainers createThreadContainerByState(TypedNFAState state, ParallelQueue<Match> matchesOutput) {
        return new ThreadContainers(Collections.singletonList(matchesOutput), state.getEventType(), timeWindow);
    }
    private ThreadContainers createThreadContainerByState(TypedNFAState state, List<ParallelQueue<Match>> matchesOutput) {
        return new ThreadContainers(matchesOutput, state.getEventType(), timeWindow);
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
        List<Boolean> isMinThreadStates = new ArrayList<>();
        for (int i =0; i < costOfStates.size(); i++) {
            Double costOfState = costOfStates.get(i);
            int numOfThreadsForState = (int)(costOfState/ totalCost * threadsLeft);
            if (numOfThreadsForState < MIN_THREADS_PER_STATE) {
                threadsToAdd += MIN_THREADS_PER_STATE;
                costLeft -= costOfState;
                isMinThreadStates.add(true);
            }
            else {
                isMinThreadStates.add(false);
            }
        }
        threadsLeft -= threadsToAdd;
        int threadToAllocate = threadsLeft;
        threadsLeft = numOfThreads;
        for (int i =0; i < costOfStates.size(); i++) {
            Double costOfState = costOfStates.get(i);
            if (isMinThreadStates.get(i)) {
                inputBufferThreadsPerState.add(MIN_THREADS_PER_STATE / 2);
                matchBufferThreadsPerState.add(MIN_THREADS_PER_STATE / 2);
                threadsLeft -= MIN_THREADS_PER_STATE;
                continue;
            }
            int numOfThreadsForState = (int) (costOfState / costLeft * threadToAllocate);
            numOfThreadsForState = Math.max(numOfThreadsForState, MIN_THREADS_PER_STATE);
            threadNumCalculation(MIN_THREADS_PER_STATE, inputBufferThreadsPerState, matchBufferThreadsPerState, numOfThreadsForState);
            threadsLeft -= numOfThreadsForState;
        }
//        threadsLeft = -1;

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
                inputBufferThreadsPerState.add(inputBufferThreadsPerState.remove(count) + numOfThreadsForState);
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
        if (!MainConfig.conditionSelectivityMeasurementMode) {
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
        }
        else  {
            for (TypedNFAState state : nfaStates) {
                costOfStates.add(1.0);
            }
        }
        double totalCost = 0;
        for (int i = 0; i < costOfStates.size(); ++i) {
            totalCost += costOfStates.get(i);
        }
        // Calculate num of threads per state based on costs
        List<Integer> inputBufferThreadsPerState = new ArrayList<>();
        List<Integer> matchBufferThreadsPerState = new ArrayList<>();

//        costOfStates.clear();
//        costOfStates.add(1.0);
//        costOfStates.add(10.0);
//        costOfStates.add(1.0);
//        costOfStates.add(1.0);
//        costOfStates.add(1.0);

        balanceThreads(nfaStates, costOfStates, totalCost, inputBufferThreadsPerState, matchBufferThreadsPerState);
        if (!MainConfig.conditionSelectivityMeasurementMode) {
            if (inputBufferThreadsPerState.subList(0, nfaStates.size()).stream().mapToInt(integer -> integer).sum() +
                matchBufferThreadsPerState.subList(0, nfaStates.size()).stream().mapToInt(integer -> integer).sum() != numOfThreads) {
                throw new RuntimeException("Not using all possible threads");
            }
        }

//        inputBufferThreadsPerState.clear();
//        matchBufferThreadsPerState.clear();
//                inputBufferThreadsPerState.add(1);
//                matchBufferThreadsPerState.add(1);
//                inputBufferThreadsPerState.add(1);
//                matchBufferThreadsPerState.add(1);
//                inputBufferThreadsPerState.add(1);
//                matchBufferThreadsPerState.add(1);
//                inputBufferThreadsPerState.add(1);
//                matchBufferThreadsPerState.add(1);
//                inputBufferThreadsPerState.add(8);
//                matchBufferThreadsPerState.add(8);

        //SEQ 4
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(3);
//        inputBufferThreadsPerState.add(8);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(2);
//        matchBufferThreadsPerState.add(8);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(10);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(10);
        //SEQ 4
//        inputBufferThreadsPerState.add(5);
//        inputBufferThreadsPerState.add(4);
//        inputBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(5);
//        matchBufferThreadsPerState.add(5);
//        matchBufferThreadsPerState.add(1);
// SEQ 5
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(3);
//        inputBufferThreadsPerState.add(5);
//        inputBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(3);
//        matchBufferThreadsPerState.add(6);
//        matchBufferThreadsPerState.add(1);
        //SEQ 8
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(2);
//        inputBufferThreadsPerState.add(3);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(3);
//        matchBufferThreadsPerState.add(3);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
        //SEQ 7
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(4);
//        inputBufferThreadsPerState.add(3);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(5);
//        matchBufferThreadsPerState.add(3);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
        //SEQ 8
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(3);
//        inputBufferThreadsPerState.add(3);
//        inputBufferThreadsPerState.add(1);
//        inputBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(4);
//        matchBufferThreadsPerState.add(3);
//        matchBufferThreadsPerState.add(1);
//        matchBufferThreadsPerState.add(1);
        int listIndex = 0;
        System.out.println("Computed thread allocation:");
        if (isMyAlgorithm) {
            for (TypedNFAState state : nfaStates) {
                stateToIBThreads.put(state, inputBufferThreadsPerState.get(listIndex));
                stateToMBThreads.put(state, matchBufferThreadsPerState.get(listIndex++));
                System.out.print(state.getEventType() + " - [" + inputBufferThreadsPerState.get(listIndex - 1) + "," + matchBufferThreadsPerState.get(listIndex - 1) + "]    ");
            }
        }
        else {
            int numberOfWorkersPerState = numOfThreads / nfaStates.size();
            int extraThreads = numOfThreads % nfaStates.size();
            for (int i = 0; i < nfaStates.size(); i++) {
                int shouldAddExtraThread = (extraThreads > i) ? 1 : 0;
                stateToIBThreads.put(nfaStates.get(i), numberOfWorkersPerState + shouldAddExtraThread);
                System.out.print(nfaStates.get(i).getEventType() + " - [" + (numberOfWorkersPerState + shouldAddExtraThread) + "] ");
            }
        }
        initializeThreads();
    }
}
