package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.EventType;
import evaluation.tree.ITreeCostModel;
import evaluation.tree.ITreeTopologyCreator;
import evaluation.tree.TopologyCreatorFactory;
import evaluation.tree.TreeCostModelFactory;
import evaluation.tree.elements.node.Node;
import order.IOrderingAlgorithm;
import order.OrderingAlgorithmFactory;
import order.cost.CostModelFactory;
import order.cost.ICostModel;
import pattern.CompositePattern;
import pattern.EventTypesManager;
import pattern.Pattern;
import pattern.Pattern.PatternOperatorType;
import pattern.condition.base.CNFCondition;
import specification.EvaluationSpecification;

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
	
	private HashMap<Pattern, EvaluationPlan> createNestedPlans(Pattern pattern) {
		CompositePattern compositePattern = (CompositePattern) pattern;
		List<Pattern> nestedPatterns = compositePattern.getNestedPatterns();
		HashMap<Pattern, EvaluationPlan> nestedPlans = new HashMap<Pattern, EvaluationPlan>();
		for (Pattern nestedPattern : nestedPatterns) {
			nestedPlans.put(nestedPattern, createEvaluationPlan(nestedPattern));
		}
		return nestedPlans;
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
		return actuallyCreateOrderBasedPlan(pattern, orderingAlgorithm, costModel);
	}
	
	private EvaluationPlan actuallyCreateOrderBasedPlan(Pattern pattern,
														IOrderingAlgorithm orderingAlgorithm, ICostModel costModel) {
		if (pattern.getType() == PatternOperatorType.OR) {
			return new EvaluationPlan(EvaluationMechanismTypes.LAZY_CHAIN, createNestedPlans(pattern));
		}
		return new EvaluationPlan(createEvaluationOrder(pattern, orderingAlgorithm, costModel));
	}
	
	private List<EventType> createEvaluationOrder(Pattern pattern,
												  IOrderingAlgorithm orderingAlgorithm, ICostModel costModel) {
		if (!(pattern instanceof CompositePattern)) {
			return orderingAlgorithm.calculateEvaluationOrder(pattern, costModel);
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		List<EventType> iterativeTypes = compositePattern.getIterativeEventTypes();
		List<EventType> negativeTypes = compositePattern.getNegativeEventTypes();
		List<EventType> eventTypesToExclude = new ArrayList<EventType>(iterativeTypes);
		eventTypesToExclude.addAll(negativeTypes);
		CompositePattern simplifiedPattern = compositePattern.getFilteredSubPattern(eventTypesToExclude);
		List<EventType> result = orderingAlgorithm.calculateEvaluationOrder(simplifiedPattern, costModel);
		result.addAll(iterativeTypes);
		result.addAll(negativeTypes);
		return result;
	}
	
	private EvaluationPlan createTreeBasedPlan(Pattern pattern) {
		ITreeTopologyCreator topologyCreator = TopologyCreatorFactory.createTopologyCreator(specification.topologyCreatorType);
		ITreeCostModel costModel = TreeCostModelFactory.createTreeCostModel(
													specification.treeCostModelType, 
													new Object[] { pattern.getEventTypes(),
															   	   specification.throughputToLatencyRatio});
		return actuallyCreateTreeBasedPlan(pattern, topologyCreator, costModel);
	}

	private EvaluationPlan actuallyCreateTreeBasedPlan(Pattern pattern,
													   ITreeTopologyCreator topologyCreator, ITreeCostModel costModel) {
		if (pattern.getType() == PatternOperatorType.OR) {
			return new EvaluationPlan(EvaluationMechanismTypes.TREE, createNestedPlans(pattern));
		}
		CompositePattern compositePattern = (CompositePattern)pattern;
		CompositePattern positivePattern = compositePattern.getFilteredSubPattern(compositePattern.getNegativeEventTypes());
		Node root = topologyCreator.createTreeTopology(positivePattern, (CNFCondition) pattern.getCondition(), costModel);
		root.finalizeTree();
		return new EvaluationPlan(root);
	}

	public EvaluationSpecification getSpecification() {
		return specification;
	}
}
