package sase.evaluation.nfa.lazy;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.NFAState;
import sase.evaluation.nfa.eager.elements.Transition;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.parallel.InputBufferWorker;
import sase.evaluation.nfa.parallel.ThreadContainers;
import sase.evaluation.plan.EvaluationPlan;
import sase.pattern.Pattern;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelLazyChainNFA extends LazyChainNFA {

    private Map<TypedNFAState, List<Event>> parallelInputBuffer;
    private Map<TypedNFAState, List<Match>> partialMatchBuffer;
    private ExecutorService executor;
    private List<ThreadContainers> threadContainers;
    private Map<NFAState, List<InputBufferWorker>> IBWorkers;
    private Map<NFAState, Integer> cyclicInputThreadCounter;
    private static int INPUT_BUFFER_THREADS_PER_STATE = 4;
    private TypedNFAState eventState;

    public ParallelLazyChainNFA(Pattern pattern, EvaluationPlan evaluationPlan, LazyNFANegationTypes negationType) {
        super(pattern, evaluationPlan, negationType);
    }

    @Override
    public List<Match> processNewEvent(Event event, boolean canStartInstance) {
        //return super.processNewEvent(event, canStartInstance);

        TypedNFAState eventState = getStateByEventType(parallelInputBuffer.keySet(), event);
        if (null == eventState) {
            // The event has irrelevent type (not in the query)
            return null;
        }
//        parallelInputBuffer.get(eventState).add(event); //TODO: PARALLELIZE : Add to the state's input buffer
        int cyclicCounter = cyclicInputThreadCounter.computeIfPresent(eventState, (key, value) -> ((value + 1) % INPUT_BUFFER_THREADS_PER_STATE));
        InputBufferWorker workerToSendEventTo = IBWorkers.get(eventState).get(cyclicCounter);
        try {
            workerToSendEventTo.getDataStorage().getEventsFromMain().put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Match> matches = findPartialMatchesOnNewEvent(eventState, event);
        return verifyMatches(matches);
    }

    private List<Match> verifyMatches(List<Match> matches) {
        matches.removeIf(match -> match.getLatestEvent() - match.getEarliestEvent() > timeWindow);
        return matches;
    }

    private List<Match> findPartialMatchesOnNewEvent(TypedNFAState eventState, Event event) {
        return findPartialMatchesInCurrentState(eventState, new ArrayList<>(List.of(event)), partialMatchBuffer.get(eventState));
    }

    private List<Match> findPartialMatchesOnNewPartialMatch(TypedNFAState eventState, Match partialMatch) {
        List<Event> stateInputBuffer = new ArrayList<>();
        for (InputBufferWorker worker : IBWorkers.get(eventState)) {
            stateInputBuffer.addAll(worker.getDataStorage().getInputBufferSubListWithOptimisticLock());
        }
        return findPartialMatchesInCurrentState(eventState, stateInputBuffer, new ArrayList<>(List.of(partialMatch)));
    }


    private List<Match> findPartialMatchesInCurrentState(TypedNFAState eventState, List<Event> eventList, List<Match> partialMatchList) {
        //TODO: PARALLELIZE: This is the step where a thread receives the event and should do its task (find PMs with comparing to MB)
        List<Match> extraEventPartialMatches = new ArrayList<>();
        if (partialMatchList.size() == 1) { //Should only remove when a new rPM arrives
            removeEventsFromIB(eventState, partialMatchList.get(0));
        }
        removeExpiredEvents(eventState); //This removes the rPMs from the MB
        if (eventState.isInitial()) { // In the first state only, events are forwarded automatically to the next state.
            extraEventPartialMatches.add(new Match(eventList, System.currentTimeMillis()));
        } else {
            for (Match partialMatch : partialMatchList) {
                for (Event event : eventList) { //One of the list is of size 1, so it is actually comparing one object with every other on the list
                    if (isEventCompatibleWithPartialMatch(eventState, partialMatch, event)) {
                        extraEventPartialMatches.add(partialMatch.createNewPartialMatchWithEvent(evaluationOrder.getFullEvaluationOrder(), event));
                    }
                }
            }
        }
        TypedNFAState nextState = (TypedNFAState) getActualNextTransition(eventState).getDestination();
        return sendPartialMatchToNextState(nextState, extraEventPartialMatches); // Sending in "batch" all new partial matches (they more "complete" as the new event is added) to the next state
    }

    private void removeEventsFromIB(TypedNFAState eventState, Match partialMatch) {
        for (InputBufferWorker worker: IBWorkers.get(eventState)) {
            try {
                worker.getDataStorage().getRemovingData().put(partialMatch);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEventCompatibleWithPartialMatch(TypedNFAState eventState, Match partialMatch, Event event) {
        //TODO: only checking temporal conditions here, I have to check the extra conditions somehow (stock prices)

        return getActualNextTransition(eventState).verifyCondition( //TODO: check if verifying the (non-temporal) condition works with partial events or consider changing the condition
                Stream.concat(partialMatch.getPrimitiveEvents().stream(), List.of(event).stream()).collect(Collectors.toList())); //Combining two lists
    }

    private List<Match> sendPartialMatchToNextState(TypedNFAState nextState, List<Match> extraEventPartialMatches) {
        if (nextState.isAccepting()) { // Last state so can return the matches
            // TODO: PARALLELIZE: instead of returning matches, should forward to an output thread
            // TODO: There is one extra state after the last state that is the final and accepting state. pay attention to it when designing the parallel solution
            return extraEventPartialMatches;
        }
        partialMatchBuffer.get(nextState).addAll(extraEventPartialMatches); //TODO: PARALLELIZE: should be done in a seperate thread (or threads)
        List<Match> completeMatches = new ArrayList<>();
        for (Match partialMatch : extraEventPartialMatches) {
            completeMatches.addAll(findPartialMatchesOnNewPartialMatch(nextState, partialMatch));
        }
        return completeMatches;
    }

    @Override
    protected void initNFAStructure() {
        super.initNFAStructure();
        createStateMappings();
        initializeThreads();
    }

    private void initializeThreads() {
        threadContainers = new ArrayList<>();
        IBWorkers = new HashMap<>();
        cyclicInputThreadCounter = new HashMap<>();
        executor = Executors.newFixedThreadPool(INPUT_BUFFER_THREADS_PER_STATE * states.size()); //TODO: maybe workstealingpool?
        for (NFAState state : states) {
            if (!state.isRejecting() && !state.isAccepting()) {
                List<InputBufferWorker> stateWorkers = new ArrayList<>();
                cyclicInputThreadCounter.put(state, -1);
                for (int i = 0; i < INPUT_BUFFER_THREADS_PER_STATE; i++) {
                    ThreadContainers threadData = new ThreadContainers(new LinkedBlockingQueue<>(), new LinkedBlockingQueue<>(), ((TypedNFAState) state).getEventType(), timeWindow);
                    threadContainers.add(threadData);
                    InputBufferWorker worker = new InputBufferWorker(threadData);
                    stateWorkers.add(worker);
                    executor.execute(worker);
                }
                IBWorkers.put(state, stateWorkers);
            }
        }
    }

    private void createStateMappings() {
        parallelInputBuffer = new HashMap<>();
        partialMatchBuffer = new HashMap<>();
        //TODO : should create states as TypedNFAStates
        for (NFAState s : states) {
            if (!s.isRejecting() && !s.isAccepting()) {
                parallelInputBuffer.put((TypedNFAState) s, new ArrayList<>());
                partialMatchBuffer.put((TypedNFAState) s, new ArrayList<>());
            }
        }
    }

    private TypedNFAState getStateByEventType(Set<TypedNFAState> states, Event event) {
        for (TypedNFAState s : states) {
            if (s.getEventType().getName().equals(event.getType().getName())) {
                return s;
            }
        }
        return null;
    }

    private Transition getActualNextTransition(NFAState state) {
        for (Transition transition : state.getOutgoingTransitions()) {
            if (transition.getAction() == Transition.Action.TAKE) {
                return transition;
            }
        }
        throw new RuntimeException("No outgoing TAKE transition");
    }

    private void removeExpiredEvents(TypedNFAState stateToRemoveFrom)
    //TODO: PARALLELIZE Removing needs to be done with caution from a specific sub-list
    //TODO: Removing is originally done after processing each event and not during processing. also works on instances (not evnets), should make sure this is ok to change. Relevant method is validateTimeWindow

    // Based on Instance.isExpired
    {
        List<Event> events = parallelInputBuffer.get(stateToRemoveFrom);
        List<Match> partialMatches = partialMatchBuffer.get(stateToRemoveFrom);
//        events.removeIf(event -> event.getTimestamp() + timeWindow < lastKnownGlobalTime);
        partialMatches.removeIf(partialMatch -> partialMatch.getEarliestEvent() + timeWindow < lastKnownGlobalTime);
    }
}
