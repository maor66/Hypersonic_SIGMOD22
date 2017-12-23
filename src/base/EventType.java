package sase.base;

import java.util.ArrayList;
import java.util.List;

public class EventType {

	private final String name;
	private final List<Attribute> attributes;
	
	public EventType(String name, Attribute[] attributes) {
		this.name = name;
		this.attributes = new ArrayList<Attribute>();
		for (Attribute attribute : attributes) {
			this.attributes.add(attribute);
		}
	}
	
	public String getName() {
		return name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
