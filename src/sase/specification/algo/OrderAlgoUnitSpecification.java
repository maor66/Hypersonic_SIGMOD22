package sase.specification.algo;

import sase.evaluation.nfa.lazy.order.OrderingAlgorithmTypes;
import sase.evaluation.nfa.lazy.order.cost.CostModelTypes;
import sase.multi.algo.AlgoUnitTypes;

public class OrderAlgoUnitSpecification extends AlgoUnitSpecification {

	public OrderingAlgorithmTypes algorithmType;
	public CostModelTypes costModelType;
	
	public OrderAlgoUnitSpecification(OrderingAlgorithmTypes algorithmType,	CostModelTypes costModelType) {
		super(AlgoUnitTypes.ORDER);
		this.algorithmType = algorithmType;
		this.costModelType = costModelType;
	}
	
}
