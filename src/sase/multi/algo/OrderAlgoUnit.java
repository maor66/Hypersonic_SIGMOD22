package sase.multi.algo;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.IIncrementalOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmFactory;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.evaluation.plan.EvaluationPlan;
import sase.evaluation.plan.OrderEvaluationPlan;
import sase.multi.MultiPatternTree;
import sase.multi.MultiPlan;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;

public class OrderAlgoUnit implements IAlgoUnit {

	private final IIncrementalOrderingAlgorithm orderingAlgorithm;
	private final ICostModel costModel;
	private final IOrderingAlgorithm trivialOrderingAlgorithm;
	
	public OrderAlgoUnit(IIncrementalOrderingAlgorithm orderingAlgorithm, ICostModel costModel) {
		this.orderingAlgorithm = orderingAlgorithm;
		this.costModel = costModel;
		this.trivialOrderingAlgorithm = OrderingAlgorithmFactory.createOrderingAlgorithm(OrderingAlgorithmTypes.TRIVIAL, null);
	}
	
	public ICostModel getCostModel() {
		return costModel;
	}

	@Override
	public EvaluationPlan calculateEvaluationPlan(Pattern pattern) {
		return new OrderEvaluationPlan(orderingAlgorithm.calculateEvaluationOrder(pattern, costModel));
	}

	@Override
	public EvaluationPlan calculateFullEvaluationPlan(Pattern pattern, EvaluationPlan subPlan,
													  MultiPlan currentMultiPlan) {
		if (subPlan == null) {
			return calculateEvaluationPlan(pattern);
		}
		List<EventType> prefix = ((OrderEvaluationPlan)subPlan).getRepresentation();
		return new OrderEvaluationPlan(orderingAlgorithm.calculateEvaluationOrder(pattern, costModel, prefix));
	}

	@Override
	public Double getPlanCost(Pattern pattern, EvaluationPlan plan) {
		return costModel.getOrderCost(pattern, ((OrderEvaluationPlan)plan).getRepresentation());
	}

	@Override
	public Double getMultiPlanCost(MultiPlan multiPlan) {
		return costModel.getMPTCost((MultiPatternTree)multiPlan);
	}

	@Override
	public MultiPlan instantiateMultiPlan() {
		return new MultiPatternTree();
	}

	@Override
	public MultiPlan instantiateMultiPlan(boolean enableSharing) {
		return new MultiPatternTree(enableSharing);
	}

	@Override
	public MultiPlan instantiateMultiPlan(MultiPlan source) {
		return new MultiPatternTree((MultiPatternTree)source);
	}

	@Override
	public void swapRandomEventTypes(CompositePattern pattern, MultiPlan multiPlan) {
		MultiPatternTree multiPatternTree = (MultiPatternTree)multiPlan;
		Random random = new Random();
		List<EventType> order = ((OrderEvaluationPlan)multiPatternTree.getPlanForPattern(pattern)).getRepresentation();
		int firstIndex = random.nextInt(order.size());
		int secondIndex = random.nextInt(order.size());
		while (firstIndex == secondIndex) {
			secondIndex = random.nextInt(order.size());
		}
		Collections.swap(order, firstIndex, secondIndex);
		multiPlan.replacePatternPlan(pattern, new OrderEvaluationPlan(order));
	}

	@Override
	public EvaluationPlan getTrivialEvaluationPlan(Pattern pattern) {
		return new OrderEvaluationPlan(trivialOrderingAlgorithm.calculateEvaluationOrder(pattern, costModel));
	}

}
