package sase.user.stocks.specification.creators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sase.base.EventType;
import sase.config.SimulationConfig;
import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.EventTypesManager;
import sase.pattern.creation.PatternTypes;
import sase.pattern.workload.WorkloadManagerTypes;
import sase.specification.condition.ConditionSpecification;
import sase.specification.condition.DoubleEventConditionSpecification;
import sase.specification.creators.CrossProductSimulationSpecificationCreator;
import sase.specification.creators.ISimulationSpecificationCreator;
import sase.specification.creators.condition.ConditionSpecificationCreatorFactory;
import sase.specification.creators.condition.ConditionSpecificationCreatorTypes;
import sase.specification.creators.condition.IConditionSpecificationCreator;
import sase.specification.workload.DynamicMultiPatternWorkloadSpecification;
import sase.specification.workload.PatternSpecification;
import sase.specification.workload.WorkloadCreationSpecification;
import sase.specification.workload.WorkloadSpecification;
import sase.user.stocks.StockEventTypesManager;

public class StockMultiPatternSpecificationCreator extends CrossProductSimulationSpecificationCreator
		implements ISimulationSpecificationCreator {

	private enum StockEventTypeSources {
		HIGH_ONLY,
		MEDIUM_ONLY,
		LOW_ONLY,
		HIGH_AND_MEDIUM,
		MEDIUM_AND_LOW,
		ALL,
	}
	
	@Override
	protected Map<WorkloadSpecification, WorkloadCreationSpecification> createWorkloadSpecifications() {
		if (!EventTypesManager.isInitialized()) {
			EventTypesManager.setInstance(new StockEventTypesManager());
			EventTypesManager.getInstance().initializeTypes();
		}
		Map<WorkloadSpecification, WorkloadCreationSpecification> result = 
				new HashMap<WorkloadSpecification, WorkloadCreationSpecification>();
		for (WorkloadCreationSpecification workloadCreationSpecification : SimulationConfig.workloadCreationSpecifications) {
			result.put(createWorkloadSpecification(workloadCreationSpecification), workloadCreationSpecification);
		}
		return result;
	}

	private WorkloadSpecification createWorkloadSpecification(WorkloadCreationSpecification workloadCreationSpecification) {
		StockEventTypeSources typeSources = generateEventTypeSources(workloadCreationSpecification);
		List<PatternSpecification> patternSpecifications = new ArrayList<PatternSpecification>();
		while (patternSpecifications.size() < workloadCreationSpecification.workloadSize) {
			createNewPattern(patternSpecifications, workloadCreationSpecification, typeSources);
		}
		addConditions(patternSpecifications, workloadCreationSpecification.conditionCreatorType);
		return (workloadCreationSpecification.managerType == WorkloadManagerTypes.MULTI_DYNAMIC) ?
				new DynamicMultiPatternWorkloadSpecification(patternSpecifications, 
															 workloadCreationSpecification.initialCurrentToReservedRatio,
															 workloadCreationSpecification.workloadModificationProbability) :
				new WorkloadSpecification(patternSpecifications, workloadCreationSpecification.managerType);
	}

	private StockEventTypeSources generateEventTypeSources(WorkloadCreationSpecification workloadCreationSpecification) {
		switch (workloadCreationSpecification.patternReorderingSensitivity) {
			case HIGH:
				return StockEventTypeSources.ALL;
			case LOW:
				return StockEventTypeSources.MEDIUM_ONLY;
			case MEDIUM:
				return StockEventTypeSources.HIGH_AND_MEDIUM;
			default:
				return null;
		}
	}

	private void createNewPattern(List<PatternSpecification> patternSpecifications,
								  WorkloadCreationSpecification workloadCreationSpecification,
								  StockEventTypeSources typeSources) {
		PatternSpecification newPatternSpecification = 
							new PatternSpecification(String.format("Pattern%d", patternSpecifications.size() + 1),
													 PatternTypes.STOCK_PATTERN, 
													 workloadCreationSpecification.timeWindow,
													 createStructure(workloadCreationSpecification, typeSources,
															 		 patternSpecifications),
													 new ConditionSpecification[0], 
													 determineSlaVerifierType(workloadCreationSpecification));
		patternSpecifications.add(newPatternSpecification);
	}
	
	private String[][][] createStructure(WorkloadCreationSpecification workloadCreationSpecification,
										 StockEventTypeSources typeSources, 
										 List<PatternSpecification> existingPatternSpecifications) {
		int patternSize = createRandomPatternSize(workloadCreationSpecification);
		List<EventType> candidateEventTypes = getCandidateEventTypes(typeSources);
		List<String> eventTypeNames = getEventTypesForPattern(candidateEventTypes.toArray(new EventType[0]), 
															  patternSize,
															  existingPatternSpecifications,
															  workloadCreationSpecification);
		String[][][] structure = new String[][][]{ new String[][] { new String[patternSize],},};
		for (int i = 0; i < eventTypeNames.size(); ++i) {
			structure[0][0][i] = eventTypeNames.get(i);
		}
		return structure;
	}

	private List<EventType> getCandidateEventTypes(StockEventTypeSources typeSources) {
		StockEventTypesManager stockEventTypesManager = (StockEventTypesManager)EventTypesManager.getInstance();
		List<EventType> result = stockEventTypesManager.getMediumCompaniesEventTypes();
		switch (typeSources) {
			case ALL:
				return stockEventTypesManager.getKnownEventTypes();
			case HIGH_AND_MEDIUM:
				result.addAll(stockEventTypesManager.getLargeCompaniesEventTypes());
				return result;
			case HIGH_ONLY:
				return stockEventTypesManager.getLargeCompaniesEventTypes();
			case LOW_ONLY:
				return stockEventTypesManager.getSmallCompaniesEventTypes();
			case MEDIUM_AND_LOW:
				result.addAll(stockEventTypesManager.getSmallCompaniesEventTypes());
				return result;
			case MEDIUM_ONLY:
				return result;
			default:
				return null;
		}
	}

	private int createRandomPatternSize(WorkloadCreationSpecification workloadCreationSpecification) {
		int delta = workloadCreationSpecification.maxPatternSize - workloadCreationSpecification.minPatternSize;
		return workloadCreationSpecification.minPatternSize + (delta == 0 ? 0 : new Random().nextInt(delta + 1));
	}

	private SlaVerifierTypes determineSlaVerifierType(WorkloadCreationSpecification workloadCreationSpecification) {
		return (new Random().nextDouble() < workloadCreationSpecification.slaConstraintProbability) ?
																						SlaVerifierTypes.BEST_PLAN : 
																						SlaVerifierTypes.NONE;
	}
	
	private void addConditions(List<PatternSpecification> patternSpecifications,
							   ConditionSpecificationCreatorTypes conditionCreatorType) {
		IConditionSpecificationCreator conditionCreator = 
						ConditionSpecificationCreatorFactory.createConditionSpecificationCreator(conditionCreatorType);
		PatternSpecification specificationWithoutCondition = getSpecificationWithoutCondition(patternSpecifications);
		while (specificationWithoutCondition != null) {
			List<String> eventNames = getRandomElements(specificationWithoutCondition.getEventNames(), 2);
			String firstEventName = eventNames.get(0);
			String secondEventName = eventNames.get(1);
			DoubleEventConditionSpecification newConditionSpecification = 
									conditionCreator.createDoubleEventCondition(firstEventName, secondEventName);
			for (PatternSpecification patternSpecification : patternSpecifications) {
				List<String> patternEventNames = Arrays.asList(patternSpecification.getEventNames());
				if (!patternEventNames.contains(firstEventName) || !patternEventNames.contains(secondEventName)) {
					continue;
				}
				patternSpecification.addConditionSpecification(newConditionSpecification);
			}
			specificationWithoutCondition = getSpecificationWithoutCondition(patternSpecifications);
		}
	}

	private <T> List<T> getRandomElements(T[] eventNames, int numberOfElements) {
		if (numberOfElements > eventNames.length) {
			numberOfElements = eventNames.length;
		}
		List<T> result = new ArrayList<T>(Arrays.asList(eventNames));
		Collections.shuffle(result);
		return result.subList(0, numberOfElements);
	}

	private PatternSpecification getSpecificationWithoutCondition(List<PatternSpecification> patternSpecifications) {
		for (PatternSpecification patternSpecification : patternSpecifications) {
			if (patternSpecification.getConditions().length == 0) {
				return patternSpecification;
			}
		}
		return null;
	}
	
	private List<String> getEventTypesForPattern(EventType[] candidateEventTypes, int patternSize,
			 									 List<PatternSpecification> existingPatternSpecifications,
			 									 WorkloadCreationSpecification workloadCreationSpecification) {
		List<String> result;
		do {
			result = generateEventTypesForPattern(candidateEventTypes, patternSize, 
												  existingPatternSpecifications, workloadCreationSpecification);
		} while (containsIdenticalPattern(result, existingPatternSpecifications));		
		return result;
	}
	
	private boolean containsIdenticalPattern(List<String> eventsForNewPattern,
											 List<PatternSpecification> existingPatternSpecifications) {
		for (PatternSpecification patternSpecification : existingPatternSpecifications) {
			String[] currentEventNames = patternSpecification.getEventNames();
			if (currentEventNames.length != eventsForNewPattern.size()) {
				continue;
			}
			boolean areEqual = true;
			for (int i = 0; i < currentEventNames.length; ++i) {
				if (!currentEventNames[i].equals(eventsForNewPattern.get(i))) {
					areEqual = false;
					break;
				}
			}
			if (areEqual) {
				return true;
			}
		}
		return false;
	}
	
	private List<String> generateEventTypesForPattern(EventType[] candidateEventTypes, int patternSize,
												 	  List<PatternSpecification> existingPatternSpecifications,
												 	  WorkloadCreationSpecification workloadCreationSpecification) {
		if (workloadCreationSpecification.minDependencyGraphVertexDegree == null || existingPatternSpecifications.isEmpty()) {
			return getRandomEventTypes(candidateEventTypes, patternSize, null);
		}
		List<String> newPatternEventNames = new ArrayList<String>();
		int newPatternPeersNumber = 0;
		int actualMinPeerNumber = 
				workloadCreationSpecification.minDependencyGraphVertexDegree < existingPatternSpecifications.size() ?
						workloadCreationSpecification.minDependencyGraphVertexDegree : existingPatternSpecifications.size();
		while (newPatternPeersNumber < actualMinPeerNumber) {
			int maxSubSetSize = patternSize - newPatternEventNames.size();
			if (maxSubSetSize <= 0) {
				//nothing better to do than restart the process
				newPatternEventNames = new ArrayList<String>();
				newPatternPeersNumber = 0;
				continue;
			}
			List<String> randomSubSet = getRandomSubset(newPatternEventNames, existingPatternSpecifications, maxSubSetSize);
			List<String> potentialNewPatternEventNames = balancedMerge(newPatternEventNames, randomSubSet);
			if (violatesMaxPeerNumberConstraint(potentialNewPatternEventNames,
												existingPatternSpecifications,
												workloadCreationSpecification)) {
				continue;
			}
			newPatternEventNames = potentialNewPatternEventNames;
			newPatternPeersNumber = getPeersNumber(newPatternEventNames, existingPatternSpecifications, null, workloadCreationSpecification);
		}
		if (newPatternEventNames.size() < patternSize) {
			List<String> additionalEventNames = getRandomEventTypes(candidateEventTypes,
																	patternSize - newPatternEventNames.size(),
																	newPatternEventNames);
			newPatternEventNames = balancedMerge(newPatternEventNames, additionalEventNames);
		}
		return newPatternEventNames;
	}

	private List<String> getRandomSubset(List<String> occupiedNames,
										 List<PatternSpecification> specifications, int maxSubSetSize) {
		List<PatternSpecification> shuffledSpecifications = new ArrayList<PatternSpecification>(specifications);
		Collections.shuffle(shuffledSpecifications);
		for (PatternSpecification patternSpecification : shuffledSpecifications) {
			List<String> currentEventNames = new ArrayList<String>(Arrays.asList(patternSpecification.getEventNames()));
			List<String> selectedEventNames = new ArrayList<String>(currentEventNames);
			selectedEventNames.removeAll(occupiedNames);
			if (selectedEventNames.isEmpty()) {
				continue;
			}
			Collections.shuffle(selectedEventNames);
			if (selectedEventNames.size() >= maxSubSetSize) {
				selectedEventNames = selectedEventNames.subList(0, 1 + new Random().nextInt(maxSubSetSize));
			}
			//must return the event names in the order of their appearance in the pattern specification - sequence constraints
			List<String> result = new ArrayList<String>();
			for (String eventName : currentEventNames) {
				if (selectedEventNames.contains(eventName)) {
					result.add(eventName);
				}
			}
			return result;
		}
		return null; //a seemingly impossible state
	}

	private List<String> balancedMerge(List<String> firstList, List<String> secondList) {
		if (firstList.isEmpty()) {
			return secondList;
		}
		if (secondList.isEmpty()) {
			return firstList;
		}
		List<String> result = new ArrayList<String>(firstList.size() + secondList.size());
		int i = 0, j = 0;
		double probability = ((double)firstList.size()) / (firstList.size() + secondList.size());
		Random random = new Random();
		while (i < firstList.size() && j < secondList.size()) {
			if (random.nextDouble() < probability) {
				result.add(firstList.get(i));
				++i;
			}
			else {
				result.add(secondList.get(j));
				++j;
			}
		}
		if (i < firstList.size()) {
			result.addAll(firstList.subList(i, firstList.size()));
		}
		if (j < secondList.size()) {
			result.addAll(secondList.subList(j, secondList.size()));
		}
		return result;
	}

	private List<String> getRandomEventTypes(EventType[] candidateEventTypes, int patternSize, List<String> blackList) {
		List<EventType> filteredEventTypes;
		if (blackList == null) {
			filteredEventTypes = Arrays.asList(candidateEventTypes);
		}
		else {
			filteredEventTypes = new ArrayList<EventType>();
			for (EventType eventType : Arrays.asList(candidateEventTypes)) {
				if (!blackList.contains(eventType.getName())) {
					filteredEventTypes.add(eventType);
				}
			}
		}
		List<EventType> newPatternEventTypes = getRandomElements(filteredEventTypes.toArray(new EventType[0]), patternSize);
		List<String> newPatternEventNames = new ArrayList<String>();
		for (EventType eventType : newPatternEventTypes) {
			newPatternEventNames.add(eventType.getName());
		}
		return newPatternEventNames;
	}

	private boolean violatesMaxPeerNumberConstraint(List<String> eventTypeNames, List<PatternSpecification> specifications, 
													WorkloadCreationSpecification workloadCreationSpecification) {
		if (workloadCreationSpecification.maxDependencyGraphVertexDegree == null) {
			return false;
		}
		List<PatternSpecification> peers = getPeers(eventTypeNames, specifications, null, workloadCreationSpecification);
		if (peers.size() > workloadCreationSpecification.maxDependencyGraphVertexDegree) {
			return true;
		}
		for (PatternSpecification patternSpecification : peers) {
			List<String> specificationEventNames = Arrays.asList(patternSpecification.getEventNames());
			int numberOfPeers = getPeersNumber(specificationEventNames, specifications, 
					   						   patternSpecification, workloadCreationSpecification);
			if (numberOfPeers >= workloadCreationSpecification.maxDependencyGraphVertexDegree) {
				return true;
			}
		}
		return false;
	}

	private int getPeersNumber(List<String> eventTypeNames, List<PatternSpecification> specifications,
							   PatternSpecification specificationToSkip,
							   WorkloadCreationSpecification workloadCreationSpecification) {
		return getPeers(eventTypeNames, specifications, specificationToSkip, workloadCreationSpecification).size();
	}

	private List<PatternSpecification> getPeers(List<String> eventTypeNames, List<PatternSpecification> specifications,
												PatternSpecification specificationToSkip,
												WorkloadCreationSpecification workloadCreationSpecification) {
		List<PatternSpecification> peers = new ArrayList<PatternSpecification>();
		for (PatternSpecification patternSpecification : specifications) {
			if (specificationToSkip != null && patternSpecification == specificationToSkip) {
				continue;
			}
			if (isPeer(eventTypeNames, patternSpecification, workloadCreationSpecification)) {
				peers.add(patternSpecification);
			}
		}
		return peers;
	}

	private boolean isPeer(List<String> eventTypeNames, PatternSpecification patternSpecification,
						   WorkloadCreationSpecification workloadCreationSpecification) {
		List<String> patternEventNames = Arrays.asList(patternSpecification.getEventNames());
		List<String> intersection = new ArrayList<String>(eventTypeNames);
		intersection.retainAll(patternEventNames);
		if (workloadCreationSpecification.minPeerIntersectionSize == 1) {
			return !intersection.isEmpty();
		}
		intersection = getMaxRefinedIntersection(intersection, patternEventNames);
		return intersection.size() >= workloadCreationSpecification.minPeerIntersectionSize;
	}
	
	private List<String> getMaxRefinedIntersection(List<String> initialIntersection, List<String> referenceList) {
		if (initialIntersection.size() <= 1) {
			return initialIntersection;
		}
		for (int i = 0; i < initialIntersection.size() - 1; ++i) {
			String firstName = initialIntersection.get(i);
			String secondName = initialIntersection.get(i + 1);
			if (referenceList.indexOf(firstName) > referenceList.indexOf(secondName)) {
				List<String> firstRefinedIntersection = new ArrayList<String>(initialIntersection);
				firstRefinedIntersection.remove(i);
				firstRefinedIntersection = getMaxRefinedIntersection(firstRefinedIntersection, referenceList);
				List<String> secondRefinedIntersection = new ArrayList<String>(initialIntersection);
				secondRefinedIntersection.remove(i + 1);
				secondRefinedIntersection = getMaxRefinedIntersection(secondRefinedIntersection, referenceList);
				return (firstRefinedIntersection.size() > secondRefinedIntersection.size()) ?
							firstRefinedIntersection : secondRefinedIntersection;
			}
		}
		return initialIntersection;
	}

}
