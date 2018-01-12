package evaluation.nfa.eager.elements;

import java.util.Arrays;
import java.util.List;

import base.Event;
import base.EventType;
import config.MainConfig;
import pattern.condition.Condition;

public class Transition {
	public enum Action {
		TAKE, IGNORE, STORE, ITERATE
	}
	
	private static final List<Action> MATCH_BUFFER_ACTIONS = Arrays.asList(Action.TAKE,	Action.ITERATE);

	private NFAState source;
	private NFAState destination;
	private Action action;
	private EventType eventType;
	private Condition condition;

	public Transition(NFAState source, NFAState destination, Action action,
			EventType eventType, Condition condition) {
		this(source, destination, action, eventType, condition, true);
	}
	
	@SuppressWarnings("unused")
	public Transition(NFAState source, NFAState destination, Action action,
			EventType eventType, Condition condition, boolean printConstructionMessage) {
		this.source = source;
		this.destination = destination;
		this.action = action;
		this.eventType = eventType;
		this.condition = condition;
		source.addOutgoingTransition(this);
		destination.addIncomingTransition(this);
		if (MainConfig.debugMode && printConstructionMessage) {
			System.out.println(String.format("Created %s", this));
		}
	}
	
	public NFAState getSource() {
		return source;
	}
	
	public NFAState getDestination() {
		return destination;
	}
	
	public Action getAction() {
		return action;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	public Condition getCondition() {
		return condition;
	}
	
	public boolean verifyCondition(List<Event> events) {
		return condition.verify(events);
	}
	
	public void setSource(NFAState newSource) {
		source.getOutgoingTransitions().remove(this);
		source = newSource;
		source.addOutgoingTransition(this);
	}
	
	public void setDestination(NFAState newDestination) {
		destination.getIncomingTransitions().remove(this);
		destination = newDestination;
		destination.addIncomingTransition(this);
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public boolean isMatchTransition() {
		return (MATCH_BUFFER_ACTIONS.contains(getAction()));
	}
	
	@Override
	public String toString() {
		return String.format("transition from %s to %s, performing %s on event type %s with %s", source, destination,
				action.toString(), eventType == null ? "<Empty>" : eventType.getName(),
				condition == null ? "no condition" : condition);
	}
}