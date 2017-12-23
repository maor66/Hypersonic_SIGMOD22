package simulator;

import java.util.HashMap;

import base.Event;
import pattern.condition.base.AtomicCondition;

//TODO: for now, only two-operand predicates will be supported!
public class PredicateResultsCache {

	private class DoubleEventPredicateCache {
	
		private HashMap<Event, HashMap<Event, Boolean>> cache;
		
		public DoubleEventPredicateCache() {
			cache = new HashMap<Event, HashMap<Event,Boolean>>();
		}
		
		public void recordConditionEvaluation(Event firstEvent, Event secondEvent, boolean result) {
			HashMap<Event, Boolean> internalCache = cache.get(firstEvent);
			boolean internalCacheExists = (internalCache != null);
			if (!internalCacheExists) {
				internalCache = new HashMap<Event, Boolean>();
			}
			internalCache.put(secondEvent, result);
			if (!internalCacheExists) {
				cache.put(firstEvent, internalCache);
			}
		}
		
		public Boolean getConditionEvaluationResult(Event firstEvent, Event secondEvent) {
			HashMap<Event, Boolean> internalCache = cache.get(firstEvent);
			if (internalCache == null) {
				return null;
			}
			return internalCache.get(secondEvent);
		}
	
	}
	
	private HashMap<AtomicCondition, DoubleEventPredicateCache> cache;
	
	public PredicateResultsCache() {
		cache = new HashMap<AtomicCondition, DoubleEventPredicateCache>();
	}
	
	public void recordConditionEvaluation(AtomicCondition condition, Event firstEvent, Event secondEvent, boolean result) {
		DoubleEventPredicateCache predicateCache = cache.get(condition);
		boolean predicateCacheExists = (predicateCache != null);
		if (!predicateCacheExists) {
			predicateCache = new DoubleEventPredicateCache();
		}
		predicateCache.recordConditionEvaluation(firstEvent, secondEvent, result);
		if (!predicateCacheExists) {
			cache.put(condition, predicateCache);
		}
	}
	
	public Boolean getConditionEvaluationResult(AtomicCondition condition, Event firstEvent, Event secondEvent) {
		DoubleEventPredicateCache predicateCache = cache.get(condition);
		if (predicateCache == null) {
			return null;
		}
		return predicateCache.getConditionEvaluationResult(firstEvent, secondEvent);
	}
	
	public void clear() {
		cache.clear();
	}

}
