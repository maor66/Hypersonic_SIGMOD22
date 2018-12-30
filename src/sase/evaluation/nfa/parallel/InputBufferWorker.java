package sase.evaluation.nfa.parallel;

import sase.base.Event;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.StampedLock;

public class InputBufferWorker implements Runnable {

    ThreadContainers dataStorage;

    public InputBufferWorker(ThreadContainers dataStorage) {
        this. dataStorage = dataStorage;
    }
    @Override
    public void run() {
        while (true) {
            Event newEvent;
            try {
                newEvent = dataStorage.getEventsFromMain().take(); //TODO: better to take batches instead of one event
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("Exception while trying to get event from BlockingQueue");
            }
            if (newEvent.isFinisherEvent()) {
                return; //TODO: how to end task?
            }
            if (dataStorage.getEventType() != newEvent.getType()) {
                throw new RuntimeException("Got wrong event type in Input Buffer");
            }
            dataStorage.addEventToOwnInputBuffer(newEvent);
            removeExpiredEvents(newEvent);
        }
//        iterateOnMatchBuffer();
    }

    private void removeExpiredEvents(Event newEvent) {
        dataStorage.removeExpiredEvents(newEvent);
        //TODO: wrong removing!!! Can only remove after taken for comparison against partial match. Should remove when a partial match is received
        //TODO: When a rPM arrives, must check based on all partial match TS. example: got B13_D17. In IB we have C6_C11
        //TODO: It is possible to remove C6 only since it is not possible to get D16 or smaller so B must be B7 or above.
        //TODO: Should check if it is indeed possible for rPM to be delayed (multiple threads so...) such that BX_D15 is possible
    }

    public ThreadContainers getDataStorage() {
        return dataStorage;
    }
}
