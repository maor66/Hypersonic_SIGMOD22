package sase.evaluation;

import java.util.List;

import sase.base.EventType;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.TopologyCreatorFactory;
import sase.evaluation.tree.TreeCostModelFactory;
import sase.evaluation.tree.elements.Node;
import sase.order.IOrderingAlgorithm;
import sase.order.OrderingAlgorithmFactory;
import sase.order.cost.CostModelFactory;
import sase.order.cost.ICostModel;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;
import sase.specification.EvaluationSpecification;

public class EvaluationPlanCreator {

	private final EvaluationSpecification specification;
	
	public EvaluationPlanCreator(EvaluationSpecification specification) {
		this.specification = specification;
	}
	
	public EvaluationPlan createEvaluationPlan(Pattern pattern) {
		switch (specification.type) {
			case LAZY_CHAIN:
				return createOrderBasedPlan(pattern);
			case TREE:
				return createTreeBasedPlan(pattern);
			case EAGER:
			case LAZY_TREE:
			default:
				return null;
		}
	}
	
	private EvaluationPlan createOrderBasedPlan(Pattern pattern) {
		List<EventType> evaluationOrder = specification.evaluationOrder == null ? null :
			 							  EventTypesManager.getInstance().convertNamesToTypes(specification.evaluationOrder);
		if (evaluationOrder != null && pattern.getEventTypes().size() != evaluationOrder.size())
				throw new RuntimeException(
						String.format("Invalid evaluation order %s specified for pattern containing events %s", 
									  evaluationOrder.toString(), pattern.getEventTypes().toString()));
		IOrderingAlgorithm orderingAlgorithm = 
				OrderingAlgorithmFactory.createOrderingAlgorithm(specification.orderingAlgorithmType,
											 					 new Object[] { evaluationOrder });
		ICostModel costModel = CostModelFactory.createCostModel(specification.costModelType, 
																new Object[] { pattern.getEventTypes(),
															    specification.throughputToLatencyRatio});
		return new EvaluationPlan(specification.type, orderingAlgorithm.calculateEvaluationOrder(pattern, costModel));
	}
	
	private EvaluationPlan createTreeBasedPlan(Pattern pattern) {
		ITreeTopologyCreator topologyCreator = TopologyCreatorFactory.createTopologyCreator(specification.topologyCreatorType);
		ITreeCostModel costModel = TreeCostModelFactory.createTreeCostModel(
													specification.treeCostModelType, 
													new Object[] { pattern.getEventTypes(),
															   	   specification.throughputToLatencyRatio});
		Node root = topologyCreator.createTreeTopology(pattern, (CNFCondition) pattern.getCondition(), costModel);
		root.finalizeTree();
		return new EvaluationPlan(specification.type, root);
	}

	public EvaluationSpecification getSpecification() {
		return specification;
	}
}
