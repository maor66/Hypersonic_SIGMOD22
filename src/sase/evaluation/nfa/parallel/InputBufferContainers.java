//TODO: Delete
///package sase.evaluation.nfa.parallel;
//
//import sase.base.Event;
//import sase.base.EventType;
//import sase.evaluation.common.Match;
//import sase.simulator.Environment;
//import sase.statistics.Statistics;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedTransferQueue;
//
//public class InputBufferContainers extends ThreadContainers
//{
//    private BlockingQueue<Match> removingData;
//    private List<Event> inputBufferSubList;
//    private BlockingQueue<Event> eventsFromMain;
//
//
//
//    public InputBufferContainers(BlockingQueue<Event> eventsFromMain, BlockingQueue<Match> removingData, EventType state, long timeWindow) {
//        super(state, timeWindow);
//        inputBufferSubList = new ArrayList<>();
//        this.removingData = removingData;
//        this.eventsFromMain = eventsFromMain;
//    }
//
//    public List<Event>  getInputBufferSubListWithOptimisticLock() {
//        return getBufferSubListWithOptimisticLock(inputBufferSubList);
//    }
//
//        public void removeExpiredEvents(Match removingCriteria) {
////        long latest_timestamp = removingCriteria.getLatestEvent();
//        long stamp = lock.writeLock();
//        int numberOfRemovedEvents = 0;
//        if (inputBufferSubList.isEmpty()) {
//            lock.unlock(stamp);
//            return;
//        }
//        Event currEvent = inputBufferSubList.get(0);
////        while (currEvent.getTimestamp() + timeWindow < latest_timestamp) {
//        //TODO: This doesn't remove as much events as ilya's algorithm. Using the commented out line improves it but should check if its enough
//        // The problem is that I should remove based on the rPM as they indicate what "time" it is for the partial matches, which means that older rPMs won't arrive,
//        // (if assuming that OoO can't happen) If I remove based on coming events, I could have a delayed rPM that should have been compared to an already deleted event
//        while (currEvent.getTimestamp() + timeWindow < inputBufferSubList.get(inputBufferSubList.size() - 1).getTimestamp()) {
//            inputBufferSubList.remove(0);
//            numberOfRemovedEvents++;
//            if (inputBufferSubList.isEmpty()) {
//                break;
//            }
//            currEvent = inputBufferSubList.get(0);
//        }
//        lock.unlock(stamp);
////        System.out.println("PAR deleted: " + numberOfRemovedEvents); //TODO: delete
//        Environment.getEnvironment().getStatisticsManager().updateDiscreteMemoryStatistic(Statistics.bufferRemovals,
//                numberOfRemovedEvents);
//    }
//
//
//    public LinkedTransferQueue<Event> getEventsFromMain() {
//        return (LinkedTransferQueue<Event>) eventsFromMain;
//    }
//
//    public BlockingQueue<Match> getRemovingData() {
//        return removingData;
//    }
//
//    @Override
//    public Event takeFromInputQueue() {
//        try {
//            return eventsFromMain.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Exception while trying to get event from BlockingQueue");
//        }
//    }
//
//    @Override
//    public Match pollRemovingCriteria() {
//        return removingData.poll();
//    }
//
//    @Override
//    public <T> void removeExpiredElements(T removingCriteria) {
//        removeExpiredEvents((Match) removingCriteria);
//    }
//
//    @Override
//    public void addEventToOwnInputBuffer(Event newEvent) {
//        addToOwnBuffer(newEvent, inputBufferSubList);
//    }
//}
