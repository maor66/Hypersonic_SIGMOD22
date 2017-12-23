package sase.base;

public class Attribute {

	private final Datatype type;
	private final String name;
	
	public Attribute(Datatype type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Datatype getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
