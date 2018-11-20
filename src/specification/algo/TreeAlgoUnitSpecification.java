package sase.specification.algo;

import sase.evaluation.tree.TopologyCreatorTypes;
import sase.evaluation.tree.TreeCostModelTypes;
import sase.multi.algo.AlgoUnitTypes;

public class TreeAlgoUnitSpecification extends AlgoUnitSpecification {

	public TopologyCreatorTypes treeCreatorType;
	public TreeCostModelTypes costModelType;
	
	public TreeAlgoUnitSpecification(TopologyCreatorTypes treeCreatorType, TreeCostModelTypes costModelType) {
		super(AlgoUnitTypes.TREE);
		this.treeCreatorType = treeCreatorType;
		this.costModelType = costModelType;
	}
	
}
