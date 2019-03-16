package sase.evaluation.nfa.lazy;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.elements.LazyTransition;
import sase.evaluation.nfa.parallel.InputBufferWorker;
import sase.evaluation.nfa.parallel.MatchBufferWorker;
import sase.evaluation.nfa.parallel.ThreadContainers;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;

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
//    private static int INPUT_BUFFER_THREADS_PER_STATE = 2;
//    private static int MATCH_BUFFER_THREADS_PER_STATE = 3;
    private BlockingQueue<Match> secondStateInputQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Match> completeMatchOutputQueue;

    public ParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
        super(pattern, evaluationPlan, negationType);
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
                transferQueue.put(new Match(List.of(event), System.currentTimeMillis()));
//                secondStateInputQueue.put(new Match(List.of(event), System.currentTimeMillis()));
            }
            else {
                LinkedBlockingQueue<Event> transferQueue = (LinkedBlockingQueue<Event>) eventInputQueues.get(eventState);
                transferQueue.put(event);
            }
//            while (!workerToSendEventTo.getDataStorage().getInputQueue().tryTransfer(event)) {
//            }//TODO: change to BlockingQueue.put
//
////            workerToSendEventTo.getDataStorage().getInputQueue().transfer(event); //TODO: change to BlockingQueue.put
////            TimeUnit.MILLISECONDS.sleep(100);
//            List<ContainsEvent> events = new ArrayList<>();
//            while (!events.contains(event)) {
//                events = workerToSendEventTo.getDataStorage().getBufferSubListWithOptimisticLock();
//            }
//            if (!events.contains(event)) {
//                System.err.println("Doesn't contain event");
//            }
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
//            if (matches.size() == 2444 || matches.size() > 686668 && completeMatchOutputQueue.size() == 0) break;
//            if (matches.size() == 617) break;
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

//    private List<Match> verifyMatches(List<Match> matches) {
//        matches.removeIf(match -> match.getLatestEventTimestamp() - match.getEarliestEvent() > timeWindow);
//        return matches;
//    }
//
//    private List<Match> findPartialMatchesOnNewEvent(TypedNFAState eventState, Event event) {
////        return findPartialMatchesInCurrentState(eventState, new ArrayList<>(List.of(event)), partialMatchBuffer.get(eventState));
//    }
//
//    private List<Match> findPartialMatchesOnNewPartialMatch(TypedNFAState eventState, Match partialMatch) {
//        List<ContainsEvent> stateInputBuffer = new ArrayList<>();
//        for (InputBufferWorker worker : IBWorkers.get(eventState)) {
//            stateInputBuffer.addAll(worker.getDataStorage().getBufferSubListWithOptimisticLock());
//        }
//        List<Event> actualEvents = (List<Event>) (List<?>) stateInputBuffer;
//
////        System.out.print("\nOriginal IB: ");
////        stateInputBuffer.forEach(System.out::print); System.out.println();
////        List<Event> slices = getSlice(stateInputBuffer, partialMatch, eventState);
////        System.out.println("Current match "+ partialMatch + " Current state: "+ eventState);
////        System.out.print("IB: ");
////        stateInputBuffer.forEach(System.out::print);
////        System.out.print("\nslice: ");
////        slices.forEach(System.out::println);
////        return findPartialMatchesInCurrentState(eventState,stateInputBuffer, new ArrayList<>(List.of(partialMatch)));
//        return findPartialMatchesInCurrentState(eventState, getSlice(actualEvents, partialMatch, eventState), new ArrayList<>(List.of(partialMatch)));
//    }

    private List<Event> getSlice(List<Event> events, Match partialMatch, TypedNFAState eventState) {
        if (events.isEmpty()) {
            return events;
        }
        //TODO: getting too much results, probably not calculating correctly
        Event lowerBoundEvent = getActualNextTransition(eventState).getActualPrecedingEvent(partialMatch.getPrimitiveEvents());
        Event upperBoundEvent = getActualNextTransition(eventState).getActualSucceedingEvent(partialMatch.getPrimitiveEvents());
        long lowerBoundSequenceNumber = (lowerBoundEvent != null) ? lowerBoundEvent.getSequenceNumber() : 0;
        long upperBoundSequenceNumber = (upperBoundEvent != null) ? upperBoundEvent.getSequenceNumber() : Long.MAX_VALUE;
//        System.out.println("lower "+ lowerBoundEvent);
//        System.out.println("upper "+ upperBoundEvent);
        events.removeIf(event -> event.getSequenceNumber() < lowerBoundSequenceNumber || event.getSequenceNumber() > upperBoundSequenceNumber);
        return events;
        //Cannot use getSlice since IB is not sorted, must go over all events one-by-one
        //TODO: is it possible to use getSlice for performance? maybe sorting while inserting?

//        EfficientInputBuffer EIB = new EfficientInputBuffer(new ArrayList<>(List.of((eventState.getEventType()))), getTimeWindow());
//        EIB.storeAll(events);
//        return EIB.getSlice(eventState.getEventType(), lowerBoundEvent, upperBoundEvent);
    }

//    private List<Match> findPartialMatchesInCurrentState(TypedNFAState eventState, List<Event> eventList, List<Match> partialMatchList) {
//        //TODO: PARALLELIZE: This is the step where a thread receives the event and should do its task (find PMs with comparing to MB)
//        List<Match> extraEventPartialMatches = new ArrayList<>();
//        if (partialMatchList.size() == 1) { //Should only remove when a new rPM arrives
//            removeEventsFromIB(eventState, partialMatchList.get(0));
//        }
//        removeExpiredEvents(eventState); //This removes the rPMs from the MB
//        if (eventState.isInitial()) { // In the first state only, events are forwarded automatically to the next state.
//            extraEventPartialMatches.add(new Match(eventList, System.currentTimeMillis()));
//        } else {
//            for (Match partialMatch : partialMatchList) {
//                for (ContainsEvent event : eventList) { //One of the list is of size 1, so it is actually comparing one object with every other on the list
//                    if (isEventCompatibleWithPartialMatch(eventState, partialMatch, (Event) event)) {
//                        extraEventPartialMatches.add(partialMatch.createNewPartialMatchWithEvent((Event) event));
//                    }
//                }
//            }
//        }
//        TypedNFAState nextState = (TypedNFAState) getActualNextTransition(eventState).getDestination();
//        return sendPartialMatchToNextState(nextState, extraEventPartialMatches); // Sending in "batch" all new partial matches (they more "complete" as the new event is added) to the next state
//    }


    private boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)

        return verifyTimeWindowConstraint(partialMatch, event) && getActualNextTransition(eventState).verifyConditionWithTemporalConditionFirst( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
                Stream.concat(partialMatch.getPrimitiveEvents().stream(), List.of(event).stream()).collect(Collectors.toList())); //Combining two lists
    }

    private boolean verifyTimeWindowConstraint(Match partialMatch, Event event) {
        return (partialMatch.getLatestEventTimestamp() <= event.getTimestamp() + timeWindow) &&
                (partialMatch.getEarliestEvent() + timeWindow >= event.getTimestamp());

    }

//    @Override
//    protected void initNFAStructure() {
//        super.initNFAStructure();
//    }

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

    private LazyTransition getActualNextTransition(NFAState state) {
        for (Transition transition : state.getOutgoingTransitions()) {
            if (transition.getAction() == Transition.Action.TAKE) {
                return (LazyTransition) transition;
            }
        }
        throw new RuntimeException("No outgoing TAKE transition");
    }

    public void initallizeThreadAllocation(List<Integer> inputBufferThreadsPerState, List<Integer> matchBufferThreadsPerState) {
        int listIndex = 0;
        for (TypedNFAState state : getWorkerStates()) {
            stateToIBThreads.put(state, inputBufferThreadsPerState.get(listIndex));
            stateToMBThreads.put(state, matchBufferThreadsPerState.get(listIndex++));
        }
        initializeThreads();
    }


//    private void removeExpiredEvents(TypedNFAState stateToRemoveFrom)
//    //TODO: PARALLELIZE Removing needs to be done with caution from a specific sub-list
//    //TODO: Removing is originally done after processing each event and not during processing. also works on instances (not evnets), should make sure this is ok to change. Relevant method is validateTimeWindow
//
//    // Based on Instance.isExpired
//    {
//        List<Event> events = parallelInputBuffer.get(stateToRemoveFrom);
//        List<Match> partialMatches = partialMatchBuffer.get(stateToRemoveFrom);
//        int MBoriginalSize = partialMatches.size();
////        events.removeIf(event -> event.getTimestamp() + timeWindow < lastKnownGlobalTime);
//        partialMatches.removeIf(partialMatch -> partialMatch.getEarliestEvent() + timeWindow < lastKnownGlobalTime);
//        Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.instanceDeletions, MBoriginalSize - partialMatches.size());
//    }
}
