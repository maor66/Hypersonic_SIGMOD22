package sase.multi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import sase.evaluation.plan.EvaluationPlan;
import sase.multi.algo.IAlgoUnit;
import sase.multi.sla.SlaAwarePattern;
import sase.pattern.CompositePattern;

public abstract class MultiPlan {

	protected final boolean enableSharing;
	protected final Set<SlaAwarePattern> slaAwarePatterns;
	protected MultiPatternGraph graph;
	protected IAlgoUnit algoUnit = null;
	
	//NOTE: this field is intentionally never copied
	protected Set<CompositePattern> potentiallyColoredPatterns = new HashSet<CompositePattern>();
	
	public MultiPlan() {
		this(true);
	}
	
	public MultiPlan(boolean enableSharing) {
		this.enableSharing = enableSharing;
		slaAwarePatterns = new HashSet<SlaAwarePattern>();
		graph = null;
	}
	
	public MultiPlan(MultiPlan source) {
		this.enableSharing = source.enableSharing;
		slaAwarePatterns = new HashSet<SlaAwarePattern>(source.slaAwarePatterns);
		graph = source.graph;
	}
	
	public boolean isSharingEnabled() {
		return enableSharing;
	}
	
	public void setAlgoUnit(IAlgoUnit algoUnit) {
		this.algoUnit = algoUnit;
	}
	
	public IAlgoUnit getAlgoUnit() {
		return algoUnit;
	}

	public void addPatternPlan(CompositePattern pattern) {
		if (algoUnit == null) {
			throw new RuntimeException("Algorithmic unit is not initialized");
		}
		EvaluationPlan optimalEvaluationPlan = algoUnit.calculateEvaluationPlan(pattern);
		addPatternPlan(pattern, optimalEvaluationPlan);
	}
	
	public boolean replacePatternPlan(CompositePattern pattern, EvaluationPlan plan) {
		if (!removePatternPlan(pattern)) {
			return false;
		}
		addPatternPlan(pattern, plan);
		return true;
	}

	public Set<SlaAwarePattern> getSlaAwarePatterns() {
		return slaAwarePatterns;
	}
	
	public MultiPatternGraph getGraph() {
		return graph;
	}

	public void setGraph(MultiPatternGraph graph) {
		this.graph = graph;
	}
	
	public void registerPotentiallyColoredPatterns(Set<CompositePattern> patterns) {
		potentiallyColoredPatterns.addAll(patterns);
	}
	
	public void applyColoring() {
		if (!graph.isColoringOn()) {
			return;
		}
		for (CompositePattern compositePattern : potentiallyColoredPatterns) {
			graph.colorPattern(compositePattern);
		}
		potentiallyColoredPatterns.clear();
		graph.validateColoring();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MultiPlan)) {
			return false;
		}
		return getSignature().equals(((MultiPlan)other).getSignature());
	}

	@Override
    public int hashCode() {
        return Objects.hash(getSignature());
    }
	
	public abstract void addPatternPlan(CompositePattern pattern, EvaluationPlan plan);
	public abstract boolean removePatternPlan(CompositePattern pattern);
	public abstract EvaluationPlan getPlanForPattern(CompositePattern pattern);
	public abstract Collection<CompositePattern> getPatterns();
	public abstract String getSignature();
}
