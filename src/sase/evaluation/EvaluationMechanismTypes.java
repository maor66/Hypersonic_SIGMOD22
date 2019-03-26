package sase.evaluation;

public enum EvaluationMechanismTypes {
	EAGER,
	LAZY_CHAIN,
	LAZY_TREE,
	TREE,
	MULTI_PATTERN_TREE,
	MULTI_PATTERN_MULTI_TREE;
	
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
			case MULTI_PATTERN_TREE:
				return "Multi-Pattern Tree";
			case MULTI_PATTERN_MULTI_TREE:
				return "Multi-Pattern Multi-Tree";
			default: 
				return "";
		}
	}
}