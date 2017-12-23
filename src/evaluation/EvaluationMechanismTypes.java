package evaluation;

public enum EvaluationMechanismTypes {
	EAGER,
	LAZY_CHAIN,
	LAZY_TREE,
	TREE;
	
	@Override
	public String toString() {
		switch(this) {
			case EAGER: 
				return "Eager NFA";
			case LAZY_CHAIN:
				return "Chain NFA";
			case LAZY_TREE:
				return "Lazy Tree NFA";
			case TREE:
				return "Tree";
			default: 
				return "";
		}
	}
}