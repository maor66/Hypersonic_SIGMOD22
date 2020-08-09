package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.evaluation.nfa.lazy.ParallelLazyChainNFA;
import sase.simulator.Environment;
import sase.statistics.Statistics;


import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static sase.evaluation.nfa.parallel.WorkerGroup.EVENT_WORKER;

public class BufferWorker implements Runnable {

    private  boolean isInputBufferWorker;
    private  long windowSize;
    String threadName;
    protected boolean canCreateMatches= true;
    public long actualCalcTime = 0;
    public long windowverifyTime = 0;
    public Thread thread;
    protected CopyOnWriteArrayList<BufferWorker> finishedWorkers;
    private CopyOnWriteArrayList<BufferWorker> finishedWithGroup;
    protected AtomicBoolean isMainFinished;

    //Tasks
    private Map<EventType, Map <Boolean, ElementWorker>> typeToWorker;

    //input
    private final ParallelQueue<? extends ContainsEvent> primaryInput;
    private final ParallelQueue<? extends ContainsEvent> secondaryInput;
    private final Map<ParallelQueue<? extends ContainsEvent>, ElementWorker> inputsToTasks; //Also actions
    private ParallelQueue<? extends ContainsEvent> lastInputUsed;

    // Finish run
    private List<BufferWorker> workersNeededToFinish;
    private EventType eventType;
    private FinishBarrier barrier;

    public long numberOfPrimaryHandledItems = 0;
    public long numberOfSecondaryHandledItems = 0;
    public long numberOfOtherStateHandledItems = 0;
    public long numberOfOppositeItems = 0;
    public  long primaryIdleTime = 0;
    public  long secondaryIdleTime = 0;
    public long iteratingBufferTime = 0;
    public long sliceTime = 0;
    public long sliceTimeActual = 0;
    public long sendMatchingTime = 0;
    public long conditionTime = 0;
    public Long innerCondTime = 0L;
    public Long innerWindowTime = 0L;

    private boolean primaryTakenOnce = false;

    private int isPrimaryInputTakenLast = 1;
    private boolean isFinishedWithThisState = false;
    private long latestSecondary = 0;
    private long latestPrimary = 0;
    private ElementWorker taskUsed;


    public BufferWorker(TypedNFAState eventState,
                        Map<ParallelQueue<? extends ContainsEvent>, Map.Entry<TypedNFAState, WorkerGroup>> inputsToTypeAndGroup,
                        Map<Map.Entry<TypedNFAState, WorkerGroup>, ThreadContainers> dataStorageForTasks,
                        Map<Map.Entry<TypedNFAState, WorkerGroup>, List<ThreadContainers>> allOppositeWorkers,
                        Map<TypedNFAState, ParallelQueue<Match>> stateToOutput,
                        FinishBarrier barrier,
                        WorkerGroup group) {
        inputsToTasks = new HashMap<>();
        Map<Map.Entry<TypedNFAState, WorkerGroup>, ParallelQueue<? extends ContainsEvent>> specificInputs = new HashMap<>();
        inputsToTypeAndGroup.forEach((input, typeAndGroup) -> {
//            ThreadContainers dataStorage = new ThreadContainers(stateToOutputs.get(eventState.getEventType()),eventState.getEventType(), timeWindow);
            List<ThreadContainers> oppositeWorkers = allOppositeWorkers.get(Map.entry(typeAndGroup.getKey(), typeAndGroup.getValue().getOpposite()));
            ElementWorker worker = (typeAndGroup.getValue() == EVENT_WORKER) ?
                    new EventWorker(typeAndGroup.getKey(), dataStorageForTasks.get(typeAndGroup), oppositeWorkers, stateToOutput.get((typeAndGroup.getKey()))) :
                    new PartialMatchWorker(typeAndGroup.getKey(), dataStorageForTasks.get(typeAndGroup), oppositeWorkers, stateToOutput.get((typeAndGroup.getKey())));
            inputsToTasks.put(input, worker);
            if (typeAndGroup.getKey().getEventType() == eventState.getEventType()) {
                specificInputs.put(typeAndGroup, input);
            }
//            Map<Boolean, ElementWorker> tasksInState = typeToWorker.get(type);
//            if (tasksInState == null) {
//                tasksInState = new HashMap<>();
//            }
//            tasksInState.put(category, worker);
//            typeToWorker.put(type, tasksInState);

        });
        primaryInput = specificInputs.get(Map.entry(eventState, group));
        secondaryInput = specificInputs.get(Map.entry(eventState, group.getOpposite()));
        this.barrier = barrier;
        this.eventType = eventState.getEventType();
        threadName = (group == EVENT_WORKER) ?  "InputBufferWorker " + eventState.getName() : "MatchBufferWorker "+ eventState.getName();
        isInputBufferWorker = group == EVENT_WORKER;

    }


//    public BufferWorker(TypedNFAState eventState,
//                        ParallelQueue<? extends ContainsEvent> eventInput,
//                        ParallelQueue<? extends ContainsEvent> partialMatchInput,
////                        Map<EventType, ParallelQueue<Match>> stateToOutputs,
//                        Map<ParallelQueue<? extends ContainsEvent>, Map<EventType, Boolean>> inputsToTypeAndCategory,
//
//                        ThreadContainers threadContainer,
//                        List<ThreadContainers> eventOppositeBuffers,
//                        List<ThreadContainers> partialMatchOppositeBuffers,
//                        CopyOnWriteArrayList<BufferWorker> finishedWorkers,
//                        CopyOnWriteArrayList<BufferWorker> finishedWithGroup,
//                        List<BufferWorker> workersNeededToFinish,
//                        boolean isInputBufferWorker)
//    {
//        inputsToTasks = new HashMap<>();
//        inputsToTypeAndCategory.forEach((input, typeAndCategory) -> typeAndCategory.forEach((type, category) -> {
////            ThreadContainers dataStorage = new ThreadContainers(stateToOutputs.get(eventState.getEventType()),eventState.getEventType(), timeWindow);
//            ElementWorker worker = category ? new EventWorker(eventState, dataStorageForTasks.get(typeAndGroup), allOppositeWorkers.get(typeAndGroup), stateToOutput.get(eventState)) : new PartialMatchWorker(eventState, dataStorageForTasks.get(typeAndGroup), allOppositeWorkers.get(typeAndGroup), stateToOutput.get(eventState));
//            inputsToTasks.put(input, worker);
//        }));
//
//        ElementWorker EventWorker = new EventWorker(eventState, dataStorageForTasks.get(typeAndGroup), allOppositeWorkers.get(typeAndGroup), stateToOutput.get(eventState));
//        ElementWorker partialMatchWorker = new PartialMatchWorker(eventState, dataStorageForTasks.get(typeAndGroup), allOppositeWorkers.get(typeAndGroup), stateToOutput.get(eventState));
//        primaryTask = isInputBufferWorker ? EventWorker : partialMatchWorker;
//        primaryTask.initializeDataStorage(threadContainer);
//        secondaryTask = isInputBufferWorker ? partialMatchWorker : EventWorker;
//        secondaryTask.initializeDataStorage(threadContainer.createClone());
//        this.finishedWorkers = finishedWorkers;
//        this.finishedWithGroup = finishedWithGroup;
//        this.primaryInput = isInputBufferWorker ? eventInput : partialMatchInput;
//        this.secondaryInput = isInputBufferWorker ? partialMatchInput : eventInput;
//        this.lastInputUsed = primaryInput; //To indicate that should start try and get an input from the primary input and not any other input
//        this.workersNeededToFinish = workersNeededToFinish;
//        this.eventType = eventState.getEventType();
//        threadName = isInputBufferWorker ?  "InputBufferWorker" + eventState.getName() :"MatchBufferWorker "+ eventState.getName();
//    }

    //TODO: is it needed?
    public BufferWorker() //Creates dummy BufferWorker
    {
        primaryInput = null;
        secondaryInput = null;
        inputsToTasks = null;
    }
    private boolean inputPriorityCriteria() {
        return isInputBufferWorker;
    }

    private List<ContainsEvent> takeOwnStateInput() {
        List<ContainsEvent> newBatch;
        newBatch = (inputPriorityCriteria()) ? takePrimaryInput() : takeSecondaryInput();
        if (newBatch == null) {
            newBatch =  (inputPriorityCriteria()) ? takeSecondaryInput() : takePrimaryInput();
        }
        return newBatch;
    }

    private List<ContainsEvent> getElement()
    {
        if (!isFinishedWithThisState) {
            List<ContainsEvent> newBatch = takeOwnStateInput();
            if (newBatch != null) {
                return newBatch;
            }
            possibleNotifyWorkerFinishedInState();
        }

        return takeElementFromDifferentState();
//return null;

//        if (!primaryTakenOnce) { // Give a chance to the primary only
//            lastInputUsed = primaryInput;
//            return takePrimaryInput();
//        }
//        List<ContainsEvent> newElement;
//        if (!isFinishedWithThisState) {
//            newElement = takePrimaryInput();
//            if (newElement != null) {
//                numberOfPrimaryHandledItems++;
//                lastInputUsed = primaryInput;
//                return newElement;
//            }
//
//            newElement = takeSecondaryInput();
//            if (newElement != null) {
//                numberOfSecondaryHandledItems++;
//                lastInputUsed = secondaryInput;
//                return newElement;
//            }
//            possibleNotifyWorkerFinishedInState();
//        }
//
//        newElement = takeElementFromDifferentState();
//        return newElement;
    }


    private void possibleNotifyWorkerFinishedInState() {
        if (barrier.hasFinishedWithAllPreviousStates(eventType) && primaryInput.isEmpty() && secondaryInput.isEmpty() && !isFinishedWithThisState) {
            isFinishedWithThisState = true;
            barrier.notifyWorkerFinished(eventType);
        }
    }

    private  List<ParallelQueue<? extends ContainsEvent>> chooseOrderForTryingInputs(List<ParallelQueue<? extends ContainsEvent>> inputs,
                                                                                     ParallelQueue<? extends ContainsEvent> failedInput)
    {
        //TODO: add random to this function
        inputs.remove(failedInput);
        List<ParallelQueue<? extends ContainsEvent>> orderedInputs = new ArrayList<>();
        inputsToTasks.forEach((input, elementWorker) -> {
            if (inputs.contains(input)) {
                int insertionIndex = elementWorker.isBufferSorted()  ? 0 : orderedInputs.size(); // Hack to check if IBworker or MBworker
                orderedInputs.add(insertionIndex, input);
            }
        });
        return orderedInputs;
    }

    private List<ContainsEvent> takeElementFromDifferentState() {
        List<ParallelQueue<? extends ContainsEvent>> inputs = getInputsOfOngoingStates();
        List<ContainsEvent> newBatch;

        // Check that the last input is not primary/secondary or was already finished
        if (inputs.contains(lastInputUsed)) { // If input is successful before than it gets priority
            newBatch = takeElementFromSpecificInput(lastInputUsed);
            if (newBatch != null) {
                numberOfOtherStateHandledItems++;
                return newBatch;
            }
        }

        List<ParallelQueue<? extends ContainsEvent>> orderedInputs = chooseOrderForTryingInputs(inputs, lastInputUsed);
//        Collections.shuffle(inputs, ThreadLocalRandom.current());

        inputs.remove(lastInputUsed); // TODO: check that actually removed
        for (ParallelQueue<? extends ContainsEvent> input : orderedInputs){ // check all other inputs
            newBatch = takeElementFromSpecificInput(input);
            if (newBatch != null) {
                lastInputUsed = input;
                numberOfOtherStateHandledItems++;
                return newBatch;
            }
        }
        return null;
    }

    private List<ContainsEvent> takeElementFromSpecificInput(ParallelQueue<? extends ContainsEvent> lastInputUsed) {
//        System.out.println("Taking different input");
        return takeNextInput(lastInputUsed, 0);
    }

    private List<ParallelQueue<? extends ContainsEvent>> getInputsOfOngoingStates() {
        ArrayList<ParallelQueue<? extends ContainsEvent>> inputs = new ArrayList<>(inputsToTasks.keySet());
        Iterator<EventType> finishedStates = barrier.getAllFinishedStates();
        while (finishedStates.hasNext()) {
            EventType finishedType = finishedStates.next();
            inputsToTasks.forEach((input, elementWorker) -> {
                if (elementWorker.eventState.getEventType() == finishedType) {
                    inputs.remove(input);
                }
            });
        }
        // Removing this state's queue
        inputs.remove(primaryInput);
        inputs.remove(secondaryInput);
        return inputs;
    }

    public void getAndWork() {
        List<ContainsEvent> newBatch = getElement();
        if (newBatch == null) {
            possibleNotifyWorkerFinishedInState();
            return;
        }
        ElementWorker usedTask = inputsToTasks.get(lastInputUsed);
        for (ContainsEvent element : newBatch) {
            usedTask.handleElement(element);
        }
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        thread.setName(threadName + " " + Thread.currentThread().getName());
        while (!thread.isInterrupted()) {
            getAndWork();
        }
        finishRun();
//            ContainsEvent newElement;
//            ElementWorker taskUsed = primaryTask;
////            long time = System.nanoTime();
//            newElement = takePrimaryInput();
////            sliceTime += System.nanoTime() - time;
//            if (newElement == null) {
//                newElement = takeSecondaryInput(); // Check if the secondaryTask queue has an item
//                if (newElement == null) {
//                    if (isPreviousStateFinished(finishedWithGroup) && !addedToGroupFinish) {
//                        finishedWithGroup.add(this);
//                        addedToGroupFinish = true;
//                    }
//                    if (isPreviousStateFinished(finishedWorkers)) {
//                        finishRun();
//                        return;
//                    } else {
//                        continue;
//                    }
//                }
//                numberOfSecondaryHandledItems++;
//                isPrimaryInputTakenLast = 0;
//                primaryTask.updateOppositeWorkers(secondaryTask);
//                taskUsed = secondaryTask; // The secondaryTask queue had an item so it is the task used for adding, iterating and removing
//            }
//            else {
//                isPrimaryInputTakenLast = 1;
//                numberOfPrimaryHandledItems++;
//            }
////            else { //Primary is used
////                secondaryTask.updateOppositeWorkers(primaryTask);
////            }
//
////            long time = System.nanoTime();
////            try {
//                if ((numberOfPrimaryHandledItems + numberOfSecondaryHandledItems) % 500== 0) {
//                    Thread.sleep(20);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            taskUsed.handleElement(newElement);
//            ContainsEvent removingCriteria = null;
//            long latestTimeStamp = Long.MIN_VALUE;
//            for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
//               ContainsEvent ce = iterateOnSubList(newElement, worker.getDataStorage().getBufferSubListWithReadLock());
//               worker.getDataStorage().releaseReadLock();
//               if (ce != null && latestTimeStamp < ce.getEarliestTimestamp()) {
//                   latestTimeStamp = ce.getEarliestTimestamp();
//                   removingCriteria = ce;
//               }
//            }
//
//            List<List<ContainsEvent>> oppositeBufferList = getOppositeBufferList();
//            idleTime += System.nanoTime() - time;
//            if (oppositeBufferList.isEmpty()) {
//                continue;
//            }
//            numberOfHandledItems++;
//            time = System.nanoTime();
//            ContainsEvent removingCriteria = iterateOnOppositeBuffer(newElement, oppositeBufferList);
//            iteratingBufferTime += System.nanoTime() - time;
//            if (removingCriteria != null) {
//                time = System.nanoTime();
//                dataStorage.removeExpiredElements(removingCriteria.getEarliestTimestamp(), isBufferSorted(), removingCriteria);
//                windowverifyTime += System.nanoTime() -time;
//            }
    }


    private List<ContainsEvent> takeSecondaryInput() {
//        long time = System.nanoTime();
        if (secondaryInput.isEmpty()) {
//            secondaryIdleTime += System.nanoTime() - time;
            return null;
        }
//        secondaryIdleTime += System.nanoTime() - time;
        List<ContainsEvent> elementBatch = takeNextInput(secondaryInput, 0);
        if (elementBatch != null) {
            lastInputUsed = secondaryInput;
            numberOfSecondaryHandledItems++;
        }
        return elementBatch;
    }

    private List<ContainsEvent> takePrimaryInput() {
//        long time = System.nanoTime();
        if (primaryInput.isEmpty()) {
//            primaryIdleTime += System.nanoTime() - time;
            return null;
        }
        primaryTakenOnce = true; //keep and delete comment
//        System.out.println("Taking primary input");
//        primaryIdleTime += System.nanoTime() - time;
        List<ContainsEvent> elementBatch  = takeNextInput(primaryInput, 0);
        if (elementBatch != null) {
            lastInputUsed = primaryInput;
            numberOfPrimaryHandledItems++;
        }
        return elementBatch;
    }


    protected List<ContainsEvent> takeNextInput(ParallelQueue<? extends ContainsEvent> input, int timeoutInMilis)  {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        return  (List<ContainsEvent>) input.poll(timeoutInMilis, TimeUnit.MILLISECONDS);
//            return input.take();
    }
    private void finishRun() {
        System.out.println("Buffer Worker - " +threadName + " " + Thread.currentThread().getId() + " Handled " + numberOfPrimaryHandledItems + " primary items " +
                + numberOfSecondaryHandledItems + " Handled secondary items " + numberOfOtherStateHandledItems +  " Handle other states items. Primary idle time " + primaryIdleTime/1000000 + " Secondary Idle time "+ secondaryIdleTime/ 1000000);
        inputsToTasks.forEach((parallelQueue, elementWorker) ->
        {
            elementWorker.forwardIncompleteBatch();
            elementWorker.finishRun();
        });

//        primaryTask.finishRun();
//        secondaryTask.finishRun();
    }

    protected boolean isPreviousStateFinished(CopyOnWriteArrayList<BufferWorker> finished) {
        for (BufferWorker worker: workersNeededToFinish) {
            if (finished.indexOf(worker) == -1) {
                return false;
            }
        }
        return true;
    }

    private ContainsEvent getReleventRemovingCriteria(List<ContainsEvent> oppositeBufferList)
    {
        //TODO: can be optimized because we already go over the MB when looking for matches, so its possible to calculate latest match at that stage
        long latestEarliestTimeStamp = Long.MIN_VALUE;
        ContainsEvent element = null;
        for (ContainsEvent ce : oppositeBufferList) {
            if (ce.getEarliestTimestamp() > latestEarliestTimeStamp) {
                element = ce;
                latestEarliestTimeStamp = ce.getEarliestTimestamp();
            }
        }
        return element;
    }

    public void resetGroupFinish() {
        isFinishedWithThisState = false;
    }

    public long size() {
        return inputsToTasks.values().stream().mapToLong(ElementWorker::size).sum();
    }

//    public void initializeOppositeWorkers(List<BufferWorker> primaryOppositeWorkers, List<BufferWorker> secondaryOppositeWorkers) {
//        List<ElementWorker> actualPrimaryOppositeWorkers = new ArrayList<>();
//        insertElementWorkers(primaryTask, primaryOppositeWorkers, actualPrimaryOppositeWorkers);
//        List<ElementWorker> actualSecondaryOppositeWorkers = new ArrayList<>();
//        insertElementWorkers(secondaryTask, secondaryOppositeWorkers, actualSecondaryOppositeWorkers);
//    }
//
//    private void insertElementWorkers(ElementWorker task, List<BufferWorker> oppositeWorkers, List<ElementWorker> actualOppositeWorkers) {
//        for (BufferWorker worker : oppositeWorkers) {
//            actualOppositeWorkers.add(worker.primaryTask);
//        }
//        task.initializeOppositeWorkers(actualOppositeWorkers);
//    }
}
//
//    protected List<List<ContainsEvent>> getOppositeBufferList()
//    {
//        List<List<ContainsEvent>> oppositeBuffer = new ArrayList<>();
//        for (BufferWorker worker : dataStorage.getOppositeBufferWorkers()) {
//            if (finishedWorkers.indexOf(worker)==-1) {
////                oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
//                oppositeBuffer.add(worker.getDataStorage().getBufferSubListWithOptimisticLock());
//                }
//            else { //If the worker has finished its run, the sub buffer won't change so we don't have to copy it
//                oppositeBuffer.add(worker.getDataStorage().getBufferSubListAfterWorkerFinished());
//            }
//        }
//        return oppositeBuffer;
//    }


