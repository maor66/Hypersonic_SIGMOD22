package sase.specification.workload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.creation.PatternTypes;
import sase.specification.condition.ConditionSpecification;

public class PatternSpecification {
	
	private final String name;
	private final PatternTypes type;
	private final Long timeWindow;
	private final String[][][] structure;
	private final String[] negatedEventNames;
	private final String[] iteratedEventNames;
	private final String[] allEventNames;
	private ConditionSpecification[] conditions;
	private final SlaVerifierTypes verifierType;
	
	public PatternSpecification(String name, PatternTypes type, Long timeWindow, String[][][] structure,
							    String[] negatedEventNames, String[] iteratedEventNames,
							    ConditionSpecification[] conditions, SlaVerifierTypes verifierType) {
		this.name = name;
		this.type = type;
		this.timeWindow = timeWindow;
		this.structure = structure;
		this.negatedEventNames = negatedEventNames != null ? negatedEventNames : new String[]{};
		this.iteratedEventNames = iteratedEventNames != null ? iteratedEventNames : new String[]{};
		this.allEventNames = fetchAllEventTypes();
		this.conditions = conditions != null ? conditions : new ConditionSpecification[]{};
		this.verifierType = verifierType;
	}

	public PatternSpecification(String name, PatternTypes type, Long timeWindow, String[][][] structure,
							    ConditionSpecification[] conditions, SlaVerifierTypes verifierType) {
		this(name, type, timeWindow, structure, null, null, conditions, verifierType);
	}

	public String getName() {
		return name;
	}

	public PatternTypes getType() {
		return type;
	}

	public Long getTimeWindow() {
		return timeWindow;
	}

	public String[][][] getStructure() {
		return structure;
	}

	public String[] getNegatedEventNames() {
		return negatedEventNames;
	}

	public String[] getIteratedEventNames() {
		return iteratedEventNames;
	}

	public ConditionSpecification[] getConditions() {
		return conditions;
	}
	
	public void addConditionSpecification(ConditionSpecification conditionSpecification) {
		List<ConditionSpecification> oldSpecifications = new ArrayList<ConditionSpecification>(Arrays.asList(conditions));
		oldSpecifications.add(conditionSpecification);
		conditions = oldSpecifications.toArray(new ConditionSpecification[0]);
	}
	
	public String getShortDescription() {
		return String.format("%s(%s)", name, timeWindow);
	}
	
	private String getPrimitiveStructureDescription(String eventTypeName) {
		for (String negatedEventName : negatedEventNames) {
			if (eventTypeName.equals(negatedEventName)) {
				return String.format("NOT(%s)", eventTypeName);
			}
		}
		for (String iteratedEventName : iteratedEventNames) {
			if (eventTypeName.equals(iteratedEventName)) {
				return String.format("(%s)*", eventTypeName);
			}
		}
		return eventTypeName;
	}
	
	private String getSequenceStructureDescription(String[] sequenceSpecification) {
		if (sequenceSpecification.length == 1) {
			return getPrimitiveStructureDescription(sequenceSpecification[0]);
		}
		String result = "SEQ(";
		for (int i = 0; i < sequenceSpecification.length; ++i) {
			result += getPrimitiveStructureDescription(sequenceSpecification[i]);
			if (i < sequenceSpecification.length - 1) {
				result += ",";
			}
		}
		result += ")";
		return result;
		
	}
	
	private String getConjunctionStructureDescription(String[][] conjunctionSpecification) {
		if (conjunctionSpecification.length == 1) {
			return getSequenceStructureDescription(conjunctionSpecification[0]);
		}
		String result = "AND(";
		for (int i = 0; i < conjunctionSpecification.length; ++i) {
			result += getSequenceStructureDescription(conjunctionSpecification[i]);
			if (i < conjunctionSpecification.length - 1) {
				result += ",";
			}
		}
		result += ")";
		return result;
	}
	
	private String getStructureDescription() {
		if (structure.length == 1) {
			return getConjunctionStructureDescription(structure[0]);
		}
		String result = "OR(";
		for (int i = 0; i < structure.length; ++i) {
			result += getConjunctionStructureDescription(structure[i]);
			if (i < structure.length - 1) {
				result += ",";
			}
		}
		result += ")";
		return result;
	}
	
	private String getConditionsDescription() {
		String result = "";
		for (int i = 0; i < conditions.length; ++i) {
			result += conditions[i].toString();
			if (i < conditions.length - 1) {
				result += ",";
			}
		}
		return result;
	}
	
	public String getLongDescription() {
		return String.format("Pattern %s: type %s; time window %d; structure %s; conditions [%s]",
							 name, type, timeWindow, getStructureDescription(), getConditionsDescription());
	}
	

	private String[] fetchAllEventTypes() {
		List<String> eventTypesNames = new ArrayList<String>();
		for (String[][] conjunctionClause : structure) {
			for (String[] sequenceClause : conjunctionClause) {
				for (String eventTypeName : sequenceClause) {
					if (!eventTypesNames.contains(eventTypeName)) {
						eventTypesNames.add(eventTypeName);
					}
				}
			}
		}
		return eventTypesNames.toArray(new String[0]);
	}
	
	public int getNumberOfEventTypes() {
		return allEventNames.length;
	}
	
	public String[] getEventNames() {
		return allEventNames;
	}
	
	public SlaVerifierTypes getVerifierType() {
		return verifierType;
	}

	@Override
	public String toString() {
		return getLongDescription();
	}

}
