package sase.input;

import sase.input.modifiers.RarestTypeEventStreamModifier;
import sase.input.modifiers.TrivialEventStreamModifier;
import sase.input.modifiers.TypeShuffleEventStreamModifier;
import sase.specification.input.InputSpecification;
import sase.specification.input.RarestTypeDuplicatorInputSpecification;
import sase.specification.input.ShuffleEventTypesInputSpecification;

public class EventStreamModifierFactory {

	public static IEventStreamModifier createEventStreamModifier(InputSpecification specification) {
		switch (specification.type) {
			case RAREST_TYPE:
				RarestTypeDuplicatorInputSpecification rarestTypeDuplicatorInputSpecification = 
															(RarestTypeDuplicatorInputSpecification)specification;
				return new RarestTypeEventStreamModifier(rarestTypeDuplicatorInputSpecification.filteringFactor,
														 rarestTypeDuplicatorInputSpecification.duplicatingFactor);
			case TYPE_SHUFFLE:
				ShuffleEventTypesInputSpecification shuffleEventTypesInputSpecification = 
																(ShuffleEventTypesInputSpecification)specification;
				return new TypeShuffleEventStreamModifier(shuffleEventTypesInputSpecification.reShufflingPeriod);
			case NONE:
			case SYNTHETIC:
			default:
				return new TrivialEventStreamModifier();
		}
	}
}
