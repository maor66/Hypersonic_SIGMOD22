package sase.evaluation.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sase.base.Event;
import sase.base.EventSelectionStrategies;
import sase.config.MainConfig;
import sase.evaluation.IEvaluationMechanism;
import sase.evaluation.IEvaluationMechanismInfo;
import sase.evaluation.common.Match;
import sase.evaluation.common.State;
import sase.evaluation.plan.DisjunctionEvaluationPlan;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.tree.elements.node.Node;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.Pattern.PatternOperatorTypes;
import sase.pattern.condition.base.AtomicCondition;

public class DisjunctionTreeEvaluationMechanism implements IEvaluationMechanism, IEvaluationMechanismInfo  {

	private final List<TreeEvaluationMechanism> trees;
	
	public DisjunctionTreeEvaluationMechanism(Pattern pattern, EvaluationPlan evaluationPlan) {
		if (pattern.getType() != PatternOperatorTypes.OR) {
			throw new RuntimeException("Illegal pattern specified for multi-tree configuration.");
		}
		trees = new ArrayList<TreeEvaluationMechanism>();
		HashMap<Pattern, EvaluationPlan> nestedPlans = ((DisjunctionEvaluationPlan)evaluationPlan).getRepresentation();
		for (Pattern subPattern : ((CompositePattern)pattern).getNestedPatterns()) {
			EvaluationPlan currentPlan = nestedPlans.get(subPattern);
			if (currentPlan == null) {
				throw new RuntimeException(String.format("No plan found for subpattern %s", subPattern));
			}
			TreeEvaluationMechanism currentTree = new TreeEvaluationMechanism(subPattern, currentPlan);
			trees.add(currentTree);
		}
	}

	@Override
	public State getStateByAtomicCondition(AtomicCondition condition) {
		for (TreeEvaluationMechanism tree : trees) {
			State state = tree.getStateByAtomicCondition(condition);
			if (state != null) {
				return state;
			}
		}
		return null;
	}

	@Override
	public double getStateReachProbability(State state) {
		return ((Node)state).getNodeReachProbability();
	}

	@Override
	public List<Match> processNewEvent(Event event, boolean canStartInstance) {
		List<Match> matches = new ArrayList<Match>();
		for (TreeEvaluationMechanism tree : trees) {
			List<Match> currentMatches = tree.processNewEvent(event, canStartInstance);
			if (currentMatches != null) {
				matches.addAll(currentMatches);
				if (MainConfig.selectionStrategy != EventSelectionStrategies.SKIP_TILL_ANY) {
					break;
				}
			}
		}
		return matches.isEmpty() ? null : matches;
	}

	@Override
	public List<Match> validateTimeWindow(long currentTime) {
		List<Match> matches = new ArrayList<Match>();
		for (TreeEvaluationMechanism tree : trees) {
			matches.addAll(tree.validateTimeWindow(currentTime));
		}
		return matches;
	}

	@Override
	public void completeCreation(List<Pattern> patterns) {		
	}

	@Override
	public List<Match> getLastMatches() {
		List<Match> matches = new ArrayList<Match>();
		for (TreeEvaluationMechanism tree : trees) {
			matches.addAll(tree.getLastMatches());
		}
		return matches;
	}

	@Override
	public long size() {
		long totalSize = 0;
		for (TreeEvaluationMechanism tree : trees) {
			totalSize += tree.size();
		}
		return totalSize;
	}

	@Override
	public String getStructureSummary() {
		String summary = "Set of trees:\n";
		for (TreeEvaluationMechanism tree : trees) {
			summary += tree.getStructureSummary() + "\n";
		}
		return summary;
	}

	@Override
	public void removeConflictingInstances(List<Match> matches) {
		for (TreeEvaluationMechanism tree : trees) {
			tree.removeConflictingInstances(matches);
		}
	}

}
