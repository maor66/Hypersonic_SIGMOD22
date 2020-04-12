package sase.evaluation.nfa.parallel;


import sase.base.ContainsEvent;
import sase.base.Event;
import sase.evaluation.common.Match;
import sase.evaluation.nfa.eager.elements.TypedNFAState;

import java.util.ArrayList;
import java.util.List;


public class EventWorker extends ElementWorker {
    public EventWorker(TypedNFAState eventState) {
        super(eventState);
    }

    @Override
    protected boolean oppositeTaskNotFinished(List<BufferWorker> workersNeededToFinish) {
        return true;
    }

    @Override
    protected boolean isBufferSorted() {
        return true;
    }

    @Override
    protected ContainsEvent iterateOnSubList(ContainsEvent newElement, List<ContainsEvent> bufferSubList) {
        long latestEarliestTimeStamp = Long.MIN_VALUE;
        Match latest = null;
        for (Match match : ((List<Match>) (List<?>) bufferSubList)) {
            if (match.getEarliestTimestamp() > latestEarliestTimeStamp) {
                latest = match;
                latestEarliestTimeStamp = match.getEarliestTimestamp();
            }
//            if (match.isExpired()) {
////                System.out.println("Doest expired " + match);
//                continue;
//            }
//            if (match.getEarliestTimestamp() - dataStorage.getTimeWindow() > newElement.getTimestamp() || match.getLatestEventTimestamp() + dataStorage.getTimeWindow() < newElement.getTimestamp()) {
//                match.setExpired();
//                continue;
//            }
            List<Event> partialMatchEvents = new ArrayList<>(match.getPrimitiveEvents());
            checkAndSendToNextState((Event) newElement, partialMatchEvents, match);
        }
        return latest;
    }
}
