package sase.evaluation.nfa.lazy.order.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.Pattern;

public class IterativeImprovementOrderingAlgorithm implements IOrderingAlgorithm {

	private class State {
		public final List<EventType> order;
		public final Double cost;
		
		public State(List<EventType> order, Pattern pattern, ICostModel costModel) {
			this.order = order;
			this.cost = costModel.getOrderCost(pattern, order);
		}
	}
	
	private static final long maxSteps = 1000000000;
	private static final long localMinimaRatio = 10;
	private static final long localMinimaThreshold = 10000000;
	
	private final IOrderingAlgorithm initialStateAlgorithm;
	
	public IterativeImprovementOrderingAlgorithm(IOrderingAlgorithm initialStateAlgorithm) {
		this.initialStateAlgorithm = initialStateAlgorithm;
	}
	
	private long getNumberOfLegalMoves(Pattern pattern) {
		long length = pattern.getEventTypes().size();
		long numberOfSwapMoves = length * (length - 1) / 2;
		long numberOfCycleMoves = length * (length - 1) * (length - 2) / 3;
		return numberOfSwapMoves + numberOfCycleMoves;
	}
	
	private List<EventType> attemptSwapMove(Pattern pattern, List<EventType> order) {
		Random random = new Random();
		int first = random.nextInt(order.size());
		int second = random.nextInt(order.size());
		while (first == second) {
			second = random.nextInt(order.size());
		}
		List<EventType> result = new ArrayList<EventType>(order);
		Collections.swap(result, first, second);
		return result;
	}
	
	private List<EventType> attemptCycleMove(Pattern pattern, List<EventType> order) {
		Random random = new Random();
		List<EventType> result = new ArrayList<EventType>(order);
		if (order.size() < 4) {
			Collections.rotate(result, 1);
			return result;
		}
		int first = random.nextInt(order.size());
		int second = random.nextInt(order.size());
		while (first == second) {
			second = random.nextInt(order.size());
		}
		int third = random.nextInt(order.size());
		while (first == third || second == third) {
			third = random.nextInt(order.size());
		}
		Collections.swap(result, first, second);
		Collections.swap(result, first, third);
		Collections.swap(result, third, second);
		return result;
	}
	
	private State attemptMove(Pattern pattern, ICostModel costModel, State currentState) {
		Random random = new Random();
		List<EventType> newOrder;
		if (random.nextBoolean()) {
			newOrder = attemptSwapMove(pattern, currentState.order);
		}
		else {
			newOrder = attemptCycleMove(pattern, currentState.order);
		}
		State newState = new State(newOrder, pattern, costModel);
		return (currentState.cost <= newState.cost) ? currentState : newState;
	}
	
	private long calculateNumOfStepsToLocalMinima(Pattern pattern) {
		long numberOfMoves = getNumberOfLegalMoves(pattern);
		if (numberOfMoves <= localMinimaThreshold) {
			return numberOfMoves;
		}
		return numberOfMoves / localMinimaRatio;
	}

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		List<EventType> initialOrder = initialStateAlgorithm.calculateEvaluationOrder(pattern, costModel);
		if (pattern.getEventTypes().size() < 3) {
			return initialOrder;
		}
		State currentState = new State(initialOrder, pattern, costModel);
		int failedMovesCount = 0;
		long movesToLocalMinima = calculateNumOfStepsToLocalMinima(pattern);
		for(int i = 0; i < maxSteps; ++i) {
			State newState = attemptMove(pattern, costModel, currentState);
			if (newState == currentState) {
				++failedMovesCount;
			}
			else {
				currentState = newState;
				failedMovesCount = 0;
			}
			if (failedMovesCount == movesToLocalMinima) {
				break;
			}
		}
		return currentState.order;
	}

}
