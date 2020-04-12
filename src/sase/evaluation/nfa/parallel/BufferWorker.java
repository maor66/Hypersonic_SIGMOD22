package sase.evaluation.nfa.parallel;

import sase.base.ContainsEvent;
import sase.base.EventType;
import sase.evaluation.nfa.eager.elements.TypedNFAState;
import sase.simulator.Environment;
import sase.statistics.Statistics;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferWorker implements Runnable {

    String threadName;
    protected boolean canCreateMatches= true;
    public long actualCalcTime = 0;
    public long windowverifyTime = 0;
    public Thread thread;
    protected CopyOnWriteArrayList<BufferWorker> finishedWorkers;
    private CopyOnWriteArrayList<BufferWorker> finishedWithGroup;
    protected AtomicBoolean isMainFinished;

    //Tasks
    protected ElementWorker primaryTask;
    protected ElementWorker secondaryTask;
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
private boolean addedToGroupFinish =  false;

private int isPrimaryInputTakenLast = 1;

    public BufferWorker(TypedNFAState eventState,
                        ParallelQueue<? extends ContainsEvent> eventInput,
                        ParallelQueue<? extends ContainsEvent> partialMatchInput,
//                        Map<EventType, ParallelQueue<Match>> stateToOutputs,
                        Map<ParallelQueue<? extends ContainsEvent>, Map<EventType, Boolean>> inputsToTypeAndCategory,

                        ThreadContainers threadContainer,
                        List<ThreadContainers> eventOppositeBuffers,
                        List<ThreadContainers> partialMatchOppositeBuffers,
                        CopyOnWriteArrayList<BufferWorker> finishedWorkers,
                        CopyOnWriteArrayList<BufferWorker> finishedWithGroup,
                        List<BufferWorker> workersNeededToFinish,
                        boolean isInputBufferWorker)
    {
        inputsToTasks = new HashMap<>();
        inputsToTypeAndCategory.forEach((input, typeAndCategory) -> typeAndCategory.forEach((type, category) -> {
//            ThreadContainers dataStorage = new ThreadContainers(stateToOutputs.get(eventState.getEventType()),eventState.getEventType(), timeWindow);
            ElementWorker worker = category ? new EventWorker(eventState) : new PartialMatchWorker(eventState);
            inputsToTasks.put(input, worker);
        }));

        ElementWorker EventWorker = new EventWorker(eventState);
        ElementWorker partialMatchWorker = new PartialMatchWorker(eventState);
        primaryTask = isInputBufferWorker ? EventWorker : partialMatchWorker;
        primaryTask.initializeDataStorage(threadContainer);
        secondaryTask = isInputBufferWorker ? partialMatchWorker : EventWorker;
        secondaryTask.initializeDataStorage(threadContainer.createClone());
        this.finishedWorkers = finishedWorkers;
        this.finishedWithGroup = finishedWithGroup;
        this.primaryInput = isInputBufferWorker ? eventInput : partialMatchInput;
        this.secondaryInput = isInputBufferWorker ? partialMatchInput : eventInput;
        this.lastInputUsed = primaryInput; //To indicate that should start try and get an input from the primary input and not any other input
        this.workersNeededToFinish = workersNeededToFinish;
        this.eventType = eventState.getEventType();
        threadName = isInputBufferWorker ?  "InputBufferWorker" + eventState.getName() :"MatchBufferWorker "+ eventState.getName();
    }

    //TODO: is it needed?
    public BufferWorker() //Creates dummy BufferWorker
    {}

    public void initializeOppositesForWorkers(Map<ThreadContainers, Boolean> allSubBuffers) {
        for (ThreadContainers dataStorage : allSubBuffers.keySet()) {
            boolean isEvent = allSubBuffers.get(dataStorage);
            EventType eventType = dataStorage.getEventType();
            getTaskByEventTypeAndCategory(eventType, !isEvent).addToOpposites(dataStorage); //Get the opposing worker of the same state
        }
    }

    private ElementWorker getTaskByEventTypeAndCategory(EventType eventType, boolean isEvent) {
        return typeToWorker.get(eventType).get(isEvent);
    }

    private List<ElementWorker> getAllWorkers() {
        List<ElementWorker> allWorkers = new ArrayList<>(inputsToTasks.values());
        allWorkers.add(primaryTask);
        allWorkers.add(secondaryTask);
        return allWorkers;
    }

    private ContainsEvent getElement()
    {
        if (!primaryTakenOnce) { // Give a chance to the primary only
            return takePrimaryInput();
        }

        ContainsEvent newElement = takePrimaryInput();
        if (newElement != null) {
            lastInputUsed = primaryInput;
            return newElement;
        }

        newElement = takeSecondaryInput();
        if (newElement != null) {
            lastInputUsed = secondaryInput;
            return newElement;
        }
        possibleNotifyWorkerFinishedInState();

        newElement = takeElementFromDifferentState();
        return newElement;
    }

    private void possibleNotifyWorkerFinishedInState() {
        if (barrier.hasFinishedWithAllPreviousStates(eventType)) {
            barrier.notifyWorkerFinished(eventType);
        }
    }

    private ContainsEvent takeElementFromDifferentState() {
        List<ParallelQueue<? extends ContainsEvent>> inputs = getInputsOfOngoingStates();
        Collections.shuffle(inputs, ThreadLocalRandom.current());
        ParallelQueue<? extends ContainsEvent> inputToTake = lastInputUsed;
        ContainsEvent newElement;

        if (inputsToTasks.containsKey(lastInputUsed)) { // If input is successful before than it gets priority
            newElement = takeElementFromSpecificInput(lastInputUsed);
            if (newElement != null) {
                return newElement;
            }
        }

        inputs.remove(lastInputUsed); // TODO: check that actually removed
        for (ParallelQueue<? extends ContainsEvent> input : inputs){ // check all other inputs
            newElement = takeElementFromSpecificInput(input);
            if (newElement != null) {
                lastInputUsed = input;
                return newElement;
            }
        }
        return null;
    }

    private List<ParallelQueue<? extends ContainsEvent>> getInputsOfOngoingStates() {
        //TODO: unimplemented currently... Need a mapping between state/eventType to input queue
        ArrayList<ParallelQueue<? extends ContainsEvent>> inputs = new ArrayList<>(inputsToTasks.keySet());
        // Removing this state's queue
        inputs.remove(primaryInput);
        inputs.remove(secondaryInput);
        return inputs;
    }

    public void getAndWork() {
        ContainsEvent newElement = getElement();
        if (newElement == null) {
            return;
        }
        ElementWorker usedTask = inputsToTasks.get(lastInputUsed);
        usedTask.handleElement(newElement);
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        thread.setName(threadName + " " + Thread.currentThread().getName());
        while (true) {
            ContainsEvent newElement;
            ElementWorker taskUsed = primaryTask;
//            long time = System.nanoTime();
            newElement = takePrimaryInput();
//            sliceTime += System.nanoTime() - time;
            if (newElement == null) {
                newElement = takeSecondaryInput(); // Check if the secondaryTask queue has an item
                if (newElement == null) {
                    if (isPreviousStateFinished(finishedWithGroup) && !addedToGroupFinish) {
                        finishedWithGroup.add(this);
                        addedToGroupFinish = true;
                    }
                    if (isPreviousStateFinished(finishedWorkers)) {
                        finishRun();
                        return;
                    } else {
                        continue;
                    }
                }
                numberOfSecondaryHandledItems++;
                isPrimaryInputTakenLast = 0;
                primaryTask.updateOppositeWorkers(secondaryTask);
                taskUsed = secondaryTask; // The secondaryTask queue had an item so it is the task used for adding, iterating and removing
            }
            else {
                isPrimaryInputTakenLast = 1;
                numberOfPrimaryHandledItems++;
            }
//            else { //Primary is used
//                secondaryTask.updateOppositeWorkers(primaryTask);
//            }

//            long time = System.nanoTime();
//            try {
//                if ((numberOfPrimaryHandledItems + numberOfSecondaryHandledItems) % 500== 0) {
//                    Thread.sleep(20);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            taskUsed.handleElement(newElement, finishedWorkers, (isPrimaryInputTakenLast==1) ? secondaryInput : primaryInput );
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
    }

    private ContainsEvent takeSecondaryInput() {
//        long time = System.nanoTime();
        if (!primaryTakenOnce || secondaryInput.isEmpty()) {
//            secondaryIdleTime += System.nanoTime() - time;
            return null;
        }
        ContainsEvent ce = takeNextInput(secondaryInput, 50);
//        secondaryIdleTime += System.nanoTime() - time;
        return ce;
    }

    private ContainsEvent takePrimaryInput() {
//        long time = System.nanoTime();
        if (primaryInput.isEmpty()) {
//            primaryIdleTime += System.nanoTime() - time;
            return null;
        }
        primaryTakenOnce = true; //keep and delete comment

        ContainsEvent ce  = takeNextInput(primaryInput, 0);
//        primaryIdleTime += System.nanoTime() - time;
        return ce;
    }


    protected ContainsEvent takeNextInput(ParallelQueue<? extends ContainsEvent> input, int timeoutInMilis)  {
        Environment.getEnvironment().getStatisticsManager().incrementParallelStatistic(Statistics.numberOfSynchronizationActions);
        return input.poll(timeoutInMilis, TimeUnit.MILLISECONDS);
//            return input.take();
    }
    private void finishRun() {
        finishedWorkers.add(this);
        System.out.println("Buffer Worker - " + Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " Handled " + numberOfPrimaryHandledItems + " primary items " +
                + numberOfSecondaryHandledItems + " Handled secondary items.  Primary idle time " + primaryIdleTime/1000000 + " Secondary Idle time "+ secondaryIdleTime/ 1000000);
        primaryTask.finishRun();
        secondaryTask.finishRun();
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
        addedToGroupFinish = false;
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


