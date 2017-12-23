package order.algorithm;

import java.util.ArrayList;
import java.util.List;

import base.EventType;
import order.IOrderingAlgorithm;
import order.cost.ICostModel;
import pattern.Pattern;

public class TrivialOrderingAlgorithm implements IOrderingAlgorithm {

	@Override
	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel) {
		switch(pattern.getType()) {
		case SEQ:
		case AND_SEQ:
			return new ArrayList<EventType>(pattern.getEventTypes());
		case ITER:
		case NEG:
		case NOP:
		case OLD_AND:
		case OLD_SEQ:
		case OR:
		default:
			throw new RuntimeException(String.format("Cannot generate trivial evaluation order for main pattern type %s", 
					   pattern.getType()));
		}
	}

}
