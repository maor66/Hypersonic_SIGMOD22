package sase.evaluation.tree;

import sase.evaluation.tree.creators.OrderedZStreamTreeTopologyCreator;
import sase.evaluation.tree.creators.SelingerTreeTopologyCreator;
import sase.evaluation.tree.creators.TrivialTreeTopologyCreator;
import sase.evaluation.tree.creators.ZStreamTreeTopologyCreator;
import sase.evaluation.tree.creators.adaptive.zstream.AdaptiveZStreamTreeTopologyCreator;

public class TopologyCreatorFactory {

	public static ITreeTopologyCreator createTopologyCreator(TopologyCreatorTypes topologyCreatorType) {
		switch (topologyCreatorType) {
			case TRIVIAL:
				return new TrivialTreeTopologyCreator();
			case SELINGER:
				return new SelingerTreeTopologyCreator();
			case ZSTREAM:
				return new ZStreamTreeTopologyCreator();
			case ORDERED_ZSTREAM:
				return new OrderedZStreamTreeTopologyCreator();
			case ADAPTIVE_ZSTREAM:
				return new AdaptiveZStreamTreeTopologyCreator();
			case NONE:
			default:
				return null;
		}
	}

}
