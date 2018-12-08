package sase.evaluation.nfa.lazy.order.cost;

import java.util.List;

import sase.base.EventType;
import sase.multi.MultiPatternTree;
import sase.pattern.Pattern;

public interface ICostModel {

	public Double getOrderCost(Pattern pattern, List<EventType> order);
	public Double getMPTCost(MultiPatternTree mpt);
}
