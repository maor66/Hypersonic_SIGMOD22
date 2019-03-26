package sase.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import sase.base.EventType;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;
import sase.pattern.condition.base.CNFCondition;
import sase.pattern.condition.time.GlobalTemporalOrderCondition;

public class MultiPatternGraph {

	private static <T,S> T getRandomKey(HashMap<T,S> map) {
		Set<T> keys = map.keySet();
		int randomPatternIndex = new Random().nextInt(keys.size());
		Iterator<T> iterator = keys.iterator();
		for (int i = 0; i < randomPatternIndex; ++i) {
			iterator.next();
		}
		return iterator.next();
	}
	
	private HashMap<Set<EventType>, Set<CompositePattern>> maxSubSets;
	private HashMap<CompositePattern, Set<Set<EventType>>> patternToMaxSubSets;
	private Set<CompositePattern> coloredPatterns;
	
	public MultiPatternGraph(List<Pattern> patterns) {
		List<CompositePattern> compositePatterns = new ArrayList<CompositePattern>();
		for (Pattern pattern : patterns) {
			if (!(pattern instanceof CompositePattern)) {
				throw new RuntimeException("Only composite paterns can participate in a sase.multi-sase.pattern setting");
			}
			compositePatterns.add((CompositePattern)pattern);
		}
		maxSubSets = new HashMap<Set<EventType>, Set<CompositePattern>>();
		patternToMaxSubSets = new HashMap<CompositePattern, Set<Set<EventType>>>();
		coloredPatterns = new HashSet<CompositePattern>();
		for (CompositePattern pattern : compositePatterns) {
			addPattern(pattern);
		}
		uncolorAll();
	}
	
	//TODO: only works on sequences and conjunctions as of now
	private Set<EventType> calculateMaxEventSubSet(CompositePattern firstPattern, CompositePattern secondPattern) {
		List<EventType> commonEventTypes = new ArrayList<EventType>(firstPattern.getEventTypes());
		commonEventTypes.retainAll(secondPattern.getEventTypes());
		CNFCondition firstSubCondition = firstPattern.getCNFCondition().getConditionForTypes(commonEventTypes, true);
		CNFCondition secondSubCondition = secondPattern.getCNFCondition().getConditionForTypes(commonEventTypes, true);
		if (!firstSubCondition.isEmpty() || !secondSubCondition.isEmpty()) {
			commonEventTypes = getEventTypesForConditionIntersection(firstSubCondition, secondSubCondition);
			if (commonEventTypes.isEmpty()) {
				return null;
			}
		}
		CNFCondition firstTemporalCondition = new GlobalTemporalOrderCondition(firstPattern.extractSequences(false));
		CNFCondition firstReducedTemporalCondition = firstTemporalCondition.getConditionForTypes(commonEventTypes, true);
		CNFCondition secondTemporalCondition = new GlobalTemporalOrderCondition(secondPattern.extractSequences(false));
		CNFCondition secondReducedTemporalCondition = secondTemporalCondition.getConditionForTypes(commonEventTypes, true);
		if (!firstReducedTemporalCondition.isEmpty() || !secondReducedTemporalCondition.isEmpty()) {
			commonEventTypes = getEventTypesForConditionIntersection(firstReducedTemporalCondition, secondReducedTemporalCondition);
		}
		return commonEventTypes.isEmpty() ? null : new HashSet<EventType>(commonEventTypes);
	}
	
	private List<EventType> getEventTypesForConditionIntersection(CNFCondition firstCondition, CNFCondition secondCondition) {
		CNFCondition commonCondition;
		if (firstCondition.equals(secondCondition)) {
			commonCondition = firstCondition;
		}
		else {
			commonCondition = firstCondition.getIntersection(secondCondition);
		}
		return commonCondition.getEventTypes();
	}
	
	public void addPattern(CompositePattern pattern) {
		patternToMaxSubSets.put(pattern, new HashSet<Set<EventType>>());
		for (CompositePattern existingPattern : patternToMaxSubSets.keySet()) {
			if (existingPattern == pattern) {
				continue;
			}
			Set<EventType> currentMaxSubSet = calculateMaxEventSubSet(pattern, existingPattern);
			if (currentMaxSubSet == null) {
				continue;
			}
			if (!maxSubSets.containsKey(currentMaxSubSet)) {
				Set<CompositePattern> currentPatternSet = new HashSet<CompositePattern>();
				currentPatternSet.add(pattern);
				currentPatternSet.add(existingPattern);
				maxSubSets.put(currentMaxSubSet, currentPatternSet);
			}
			else {
				Set<CompositePattern> currentPatternSet = maxSubSets.get(currentMaxSubSet);
				currentPatternSet.add(pattern);
				currentPatternSet.add(existingPattern);
			}
			patternToMaxSubSets.get(pattern).add(currentMaxSubSet);
			patternToMaxSubSets.get(existingPattern).add(currentMaxSubSet);
		}
		colorPattern(pattern);
	}
	
	public void removePattern(CompositePattern pattern) {
		for (Set<EventType> maxSubSet : patternToMaxSubSets.get(pattern)) {
			Set<CompositePattern> patternsSharingCurrentMaxSubSet = maxSubSets.get(maxSubSet);
			patternsSharingCurrentMaxSubSet.remove(pattern);
			if (patternsSharingCurrentMaxSubSet.size() == 1) {
				maxSubSets.remove(maxSubSet);
				patternToMaxSubSets.get(patternsSharingCurrentMaxSubSet.iterator().next()).remove(maxSubSet);
			}
			else if (maxSubSet.size() > 1) {
				coloredPatterns.addAll(patternsSharingCurrentMaxSubSet);
			}
		}
		patternToMaxSubSets.remove(pattern);
		validateColoring();
	}
	
	public Set<CompositePattern> getPatternsByMaxSubSet(Set<EventType> maxSubSet) {
		return maxSubSets.get(maxSubSet);
	}
	
	public Set<Set<EventType>> getPatternPeerMaxSubSets(CompositePattern pattern) {
		return patternToMaxSubSets.get(pattern);
	}
	
	public Set<CompositePattern> getAllPatterns() {
		return patternToMaxSubSets.keySet();
	}
	
	public CompositePattern getRandomPattern() {
		return getRandomKey(patternToMaxSubSets);
	}
	
	public Set<EventType> getRandomMaxSubSet() {
		return getRandomKey(maxSubSets);
	}

	public Set<CompositePattern> getColoredPatterns() {
		return coloredPatterns;
	}
	
	public void colorPattern(CompositePattern pattern) {
		if (!patternToMaxSubSets.keySet().contains(pattern)) {
			return;
		}
		coloredPatterns.add(pattern);
	}
	
	public void uncolorPattern(CompositePattern pattern) {
		coloredPatterns.remove(pattern);
	}

	public void uncolorAll() {
		coloredPatterns.clear();
	}
	
	public void validateColoring() {
		//everything colored is equivalent to everything uncolored
		if (coloredPatterns.size() == patternToMaxSubSets.keySet().size()) {
			uncolorAll();
		}
	}
	
	public boolean isColoringOn() {
		return !coloredPatterns.isEmpty();
	}

}
