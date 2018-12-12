package sase.evaluation.nfa.eager.elements;

import sase.base.EventType;

public class TypedNFAState extends NFAState{
    private final EventType eventType;

    public TypedNFAState(EventType eventType) {
        super();
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

}
