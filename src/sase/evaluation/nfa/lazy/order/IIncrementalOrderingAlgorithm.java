package sase.evaluation.nfa.lazy.order;

import java.util.List;

import sase.base.EventType;
import sase.evaluation.nfa.lazy.order.cost.ICostModel;
import sase.pattern.Pattern;

public interface IIncrementalOrderingAlgorithm extends IOrderingAlgorithm {

	public List<EventType> calculateEvaluationOrder(Pattern pattern, ICostModel costModel, List<EventType> prefix);
}
