package evaluation.tree;

import evaluation.tree.creators.OrderedZStreamTreeTopologyCreator;
import evaluation.tree.creators.SelingerTreeTopologyCreator;
import evaluation.tree.creators.ZStreamTreeTopologyCreator;
import evaluation.tree.creators.adaptive.zstream.AdaptiveZStreamTreeTopologyCreator;

public class TopologyCreatorFactory {

	public static ITreeTopologyCreator createTopologyCreator(TopologyCreatorTypes topologyCreatorType) {
		switch (topologyCreatorType) {
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
