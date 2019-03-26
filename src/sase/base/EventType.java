package sase.base;

import java.util.ArrayList;
import java.util.List;

import sase.simulator.Environment;

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
	
	public static List<EventType> asList(EventType et) {
		List result = new ArrayList<EventType>();
		result.add(et);
		return result;
	}
	
	public String getName() {
		return name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public Double getRate() {
		return Environment.getEnvironment().getEventRateEstimator().getEventRateEstimate(this);
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
