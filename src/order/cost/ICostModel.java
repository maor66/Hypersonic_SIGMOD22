package order.cost;

import java.util.List;

import base.EventType;
import pattern.Pattern;

public interface ICostModel {

	public Double getOrderCost(Pattern pattern, List<EventType> order);
}
