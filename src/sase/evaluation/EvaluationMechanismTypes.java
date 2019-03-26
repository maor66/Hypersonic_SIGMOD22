package sase.evaluation;

public enum EvaluationMechanismTypes {
	EAGER,
	LAZY_CHAIN,
	LAZY_TREE,
	TREE,
	MULTI_PATTERN_TREE,
	MULTI_PATTERN_MULTI_TREE,
	LAZY_CHAIN_PARALLEL,
	HIRZEL_CHAIN_NFA,
	RIP_CHAIN_NFA;

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
			case LAZY_CHAIN_PARALLEL:
				return "Parallel chain NFA";
			case HIRZEL_CHAIN_NFA:
				return "Hirzel Parallel chain NFA";
			case RIP_CHAIN_NFA:
				return "RIP Parallel chain NFA";
			default: 
				return "";
		}
	}
}