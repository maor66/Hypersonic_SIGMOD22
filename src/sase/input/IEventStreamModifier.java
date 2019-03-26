package sase.input;

import java.util.List;

import sase.base.Event;

public interface IEventStreamModifier {
	
	public List<Event> produceModifiedEvents(Event initialEvent);
}
