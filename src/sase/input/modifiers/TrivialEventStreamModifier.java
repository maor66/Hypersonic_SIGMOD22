package sase.input.modifiers;

import java.util.Arrays;
import java.util.List;

import sase.base.Event;
import sase.input.IEventStreamModifier;

public class TrivialEventStreamModifier implements IEventStreamModifier {

	@Override
	public List<Event> produceModifiedEvents(Event initialEvent) {
		return Arrays.asList(initialEvent);
	}

}
