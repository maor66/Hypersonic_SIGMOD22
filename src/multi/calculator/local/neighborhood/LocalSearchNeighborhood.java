package sase.multi.calculator.local.neighborhood;

import java.util.Collection;
import java.util.Iterator;

import sase.multi.MultiPatternGraph;
import sase.multi.MultiPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.sla.SlaAwarePattern;

public abstract class LocalSearchNeighborhood {

	protected static <T> T getObjectAtIndexFromCollection(Collection<T> collection, int index) {
		Iterator<T> iter = collection.iterator();
		for (int i = 0; i < index; i++) {
		    iter.next();
		}
		return iter.next();
	}
	
	protected MultiPatternGraph graph;
	protected IAlgoUnit algoUnit;
	protected MultiPlan currentState;
	protected final Long timeLimit;
	
	public LocalSearchNeighborhood(IAlgoUnit algoUnit, MultiPlan initialState, Long timeLimit) {
		this.graph = initialState.getGraph();
		this.algoUnit = algoUnit;
		this.timeLimit = timeLimit;
		setCurrentState(initialState);
	}
	
	public void setCurrentState(MultiPlan currentState) {
		this.currentState = currentState;
		this.currentState.applyColoring();
	}
	
	public MultiPlan getCurrentState() {
		return currentState;
	}

	private boolean satisfiesSLARequirements(MultiPlan multiPlan) {
		if (multiPlan == null) {
			return true;
		}
		for (SlaAwarePattern pattern : multiPlan.getSlaAwarePatterns()) {
			if (!pattern.verifySlaRequirements(multiPlan, algoUnit)) {
				return false;
			}
		}
		return true;
	}
	
	public MultiPlan getNextNeighbor(long initialTime) {
		return getNextNeighbor(initialTime, false);
	}
	
	public MultiPlan getNextNeighbor() {
		return getNextNeighbor(null, true);
	}
	
	private MultiPlan getNextNeighbor(Long initialTime, boolean ignoreSLA) {
		MultiPlan candidateNeighbor = actuallyGetNextNeighbor();
		if (ignoreSLA) {
			return candidateNeighbor;
		}
		while (!satisfiesSLARequirements(candidateNeighbor)) {
			if (initialTime != null && System.currentTimeMillis() - initialTime > timeLimit) {
				break;
			}
			candidateNeighbor = actuallyGetNextNeighbor();
		}
		return candidateNeighbor;
	}

	protected abstract MultiPlan actuallyGetNextNeighbor();
}
