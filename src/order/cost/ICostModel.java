package sase.order.cost;

import java.util.List;

import sase.base.EventType;
import sase.pattern.Pattern;

public interface ICostModel {

	public Double getOrderCost(Pattern pattern, List<EventType> order);
}
