package evaluation.nfa.lazy;

public enum LazyNFANegationTypes {
	NONE,
	POST_PROCESSING,
	FIRST_CHANCE;
	
	@Override
	public String toString() {
		switch(this) {
			case NONE: 
				return "None";
			case POST_PROCESSING:
				return "Post Processing";
			case FIRST_CHANCE:
				return "First Chance";
			default: 
				return "";
		}
	}
}