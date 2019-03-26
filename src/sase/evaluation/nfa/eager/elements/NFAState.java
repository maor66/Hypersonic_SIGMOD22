package sase.evaluation.nfa.eager.elements;

import java.util.ArrayList;
import java.util.List;

import sase.config.MainConfig;
import sase.evaluation.common.State;

public class NFAState extends State {
	
	static int stateIdCounter = 0;
	
	private final int id;
	private final String name;
	
	private List<Transition> incomingTransitions;
	private List<Transition> outgoingTransitions;
	private final boolean isInitial;
	private final boolean isFinal;
	private final boolean isAccepting;
	
	public NFAState() {
		this("Unspecified");
	}
	
	public NFAState(String name) {
		this(name, false, false, false);
	}
	
	public NFAState(String name, boolean isInitial, boolean isFinal, boolean isAccepting) {
		this.id = stateIdCounter++;
		this.name = name == null ? String.format("State_%d", id) : name;
		this.isInitial = isInitial;
		this.isFinal = isFinal;
		this.isAccepting = isAccepting;
		incomingTransitions = new ArrayList<Transition>();
		outgoingTransitions = new ArrayList<Transition>();
		if (MainConfig.debugMode) {
			System.out.println(String.format("State created: %s", toString()));
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void addIncomingTransition(Transition transition) {
		incomingTransitions.add(transition);
	}
	
	public void addOutgoingTransition(Transition transition) {
		outgoingTransitions.add(transition);
	}
	
	public List<Transition> getIncomingTransitions() {
		return incomingTransitions;
	}

	public List<Transition> getOutgoingTransitions() {
		return outgoingTransitions;
	}
	
	public boolean isInitial() {
		return isInitial;
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	public boolean isAccepting() {
		return isAccepting;
	}
	
	public boolean isRejecting() {
		return (isFinal && !isAccepting);
	}
	
	public String toString() {
		return String.format("%s(%d)", name, id);
	}
}