package sase.multi.algo;

import sase.evaluation.nfa.lazy.order.IIncrementalOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.IOrderingAlgorithm;
import sase.evaluation.nfa.lazy.order.OrderingAlgorithmFactory;
import sase.evaluation.nfa.lazy.order.cost.CostModelFactory;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.evaluation.tree.IIncrementalTreeTopologyCreator;
import sase.evaluation.tree.ITreeCostModel;
import sase.evaluation.tree.ITreeTopologyCreator;
import sase.evaluation.tree.TopologyCreatorFactory;
import sase.evaluation.tree.TreeCostModelFactory;
import sase.specification.algo.AlgoUnitSpecification;
import sase.specification.algo.OrderAlgoUnitSpecification;
import sase.specification.algo.TreeAlgoUnitSpecification;

public class AlgoUnitFactory {

	public static IAlgoUnit createAlgoUnit(AlgoUnitSpecification specification) {
		switch(specification.type) {
			case ORDER:
				OrderAlgoUnitSpecification orderSpecification = (OrderAlgoUnitSpecification)specification;
				IOrderingAlgorithm orderingAlgorithm = 
						OrderingAlgorithmFactory.createOrderingAlgorithm(orderSpecification.algorithmType, null);
				ICostModel costModel = CostModelFactory.createCostModel(orderSpecification.costModelType, null);
				return new OrderAlgoUnit((IIncrementalOrderingAlgorithm)orderingAlgorithm, costModel);
			case TREE:
				TreeAlgoUnitSpecification treeSpecification = (TreeAlgoUnitSpecification)specification;
				ITreeTopologyCreator treeCreator = 
						TopologyCreatorFactory.createTopologyCreator(treeSpecification.treeCreatorType);
				ITreeCostModel treeCostModel = TreeCostModelFactory.createTreeCostModel(treeSpecification.costModelType, null);
				return new TreeAlgoUnit((IIncrementalTreeTopologyCreator)treeCreator, treeCostModel);
			default:
				throw new RuntimeException("Unknown Algo Unit type");
		}
	}
}
