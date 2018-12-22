package sase.evaluation.nfa.eager.elements;

import sase.base.EventType;

public class TypedNFAState extends NFAState{
    private final EventType eventType;

    public TypedNFAState(EventType eventType) {
        super();
        this.eventType = eventType;
    }

    public TypedNFAState(EventType eventType, String name) {
        super(name);
        this.eventType = eventType;
    }
    public TypedNFAState(EventType eventType, String name, boolean isInitial, boolean isFinal, boolean isAccepting) {
        super(name,isInitial,isFinal,isAccepting);
        this.eventType = eventType;
    }
    public EventType getEventType() {
        return eventType;
    }

}
