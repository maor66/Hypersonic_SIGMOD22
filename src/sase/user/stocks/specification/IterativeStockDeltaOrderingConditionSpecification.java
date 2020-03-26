package sase.user.stocks.specification;

import sase.base.AggregatedEvent;
import sase.base.Event;
import sase.base.EventType;
import sase.evaluation.common.Match;
import sase.pattern.EventTypesManager;
import sase.pattern.condition.base.AtomicCondition;
import sase.pattern.condition.base.SingleEventCondition;
import sase.pattern.condition.iteration.FirstValueExternalCondition;
import sase.pattern.condition.iteration.IteratedEventInternalCondition;
import sase.specification.condition.ConditionSpecification;
import sase.user.stocks.condition.StockDeltaOrderingCondition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IterativeStockDeltaOrderingConditionSpecification extends ConditionSpecification {
    private final String precedingEventName;
    private final String iterativeEventName;
    private final String succeedingEventName;
    private static final Object o = new Object();

    public IterativeStockDeltaOrderingConditionSpecification(String precedingEventName,
                                                                   String iterativeEventName,
                                                                   String succeedingEventName) {
        this.precedingEventName = precedingEventName;
        this.iterativeEventName = iterativeEventName;
        this.succeedingEventName = succeedingEventName;
    }

    @Override
    public List<AtomicCondition> createConditions() {
        List<AtomicCondition> result = new ArrayList<AtomicCondition>();
        EventType precedingType = EventTypesManager.getInstance().getTypeByName(precedingEventName);
        EventType iterativeType = EventTypesManager.getInstance().getTypeByName(iterativeEventName);
        EventType succeedingType = EventTypesManager.getInstance().getTypeByName(succeedingEventName);
//        result.add(new FirstValueExternalCondition(new StockDeltaOrderingCondition(precedingType,iterativeType), false));
        result.add(new IteratedEventInternalCondition(iterativeType) {
            @Override
            protected boolean verifyAggregatedEvent(AggregatedEvent event) {
                List<Event> primitiveEvents = event.getPrimitiveEvents();
                if (primitiveEvents.size() < 2) {
                    return true;
                }

                for (int i = 1; i < primitiveEvents.size()  ;i++) {
                    Event lastEvent = primitiveEvents.get(i);
                    Event secondToLastEvent = primitiveEvents.get(i-1);
                    List<Event> checkedEvents = List.of(secondToLastEvent, lastEvent);
                    SingleEventCondition condition = new SingleEventCondition(iterativeType) {
                        @Override
                        protected boolean verifySingleEvent(Event event) {
                            StockDeltaOrderingCondition stoCond = new StockDeltaOrderingCondition(iterativeType, iterativeType);
                            boolean  b= stoCond.verifyDoubleEvent(secondToLastEvent, lastEvent);

//                                try {
//                                    BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Maor\\Documents\\"+ Thread.currentThread().getId() + "calc1.txt", true));
//                                    writer.append("Comparing " + secondToLastEvent.toString() + " with " + lastEvent.toString() + " result is "+ b);
//                                    writer.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }

                            return b;
                        }

                        @Override
                        protected String getConditionKey() {
                            return null;
                        }
                    };
                    if (!condition.verify(checkedEvents)) {
                        return  false;
                    }

                }
                return true;
            }

            @Override
            protected String getConditionKey() {
                return null;
            }
        });
//        result.add(new FirstValueExternalCondition(new StockDeltaOrderingCondition(iterativeType,succeedingType), true));
        return result;

    }

    @Override
    public String getShortDescription() {
        return String.format("%s=>(%s)*=>%s", precedingEventName, iterativeEventName, succeedingEventName);
    }
}
