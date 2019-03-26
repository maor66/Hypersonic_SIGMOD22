package sase.pattern.workload;

import java.util.ArrayList;
import java.util.List;

import sase.multi.sla.SlaVerifierFactory;
import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.CompositePattern;
import sase.pattern.EventTypesManager;
import sase.pattern.Pattern;
import sase.pattern.creation.CompositePatternCreator;
import sase.specification.input.InputSpecification;
import sase.specification.input.SyntheticInputSpecification;
import sase.specification.workload.DynamicMultiPatternWorkloadSpecification;
import sase.specification.workload.PatternSpecification;
import sase.specification.workload.WorkloadSpecification;
import sase.user.speedd.fraud.CreditCardFraudEventTypesManager;
import sase.user.speedd.traffic.TrafficEventTypesManager;
import sase.user.stocks.StockEventTypesManager;
import sase.user.synthetic.SyntheticEventTypesManager;
import sase.user.traffic.AarhusTrafficEventTypesManager;
import sase.user.trams.TramEventTypesManager;

/**
 * This class is responsible for sase.pattern creation.
 */
public class PatternWorkloadFactory {

	private static void createEventTypesManager(PatternSpecification patternSpecification,
												InputSpecification inputSpecification) {
		//TODO: there is an implicit assumption here that ALL specifications have the same sase.pattern type. OK for now
		//(since anyway there is only one sase.input source as of now), but must be fixed in future!
		if (EventTypesManager.isInitialized()) {
			return;
		}
		switch(patternSpecification.getType()) {
			case OLD_SEQUENCE_STOCK_CORRELATION:
			case OLD_CONJUNCTION_STOCK_CORRELATION:
			case STOCK_PATTERN:
				EventTypesManager.setInstance(new StockEventTypesManager());
				break;
			case TRAM_CONGESTION_PATTERN:
				EventTypesManager.setInstance(new TramEventTypesManager());
				break;
			case SPEEDD_TRAFFIC_PATTERN:
				EventTypesManager.setInstance(new TrafficEventTypesManager());
				break;
			case SPEEDD_CREDIT_CARD_FRAUD_PATTERN:
				EventTypesManager.setInstance(new CreditCardFraudEventTypesManager());
				break;
			case SYNTHETIC_PATTERN:
				SyntheticInputSpecification syntheticInputSpecification = (SyntheticInputSpecification)inputSpecification;
				EventTypesManager.setInstance(new SyntheticEventTypesManager(syntheticInputSpecification.numberOfEventTypes));
				break;
			case TRAFFIC_PATTERN:
				EventTypesManager.setInstance(new AarhusTrafficEventTypesManager());
				break;
			default:
				throw new RuntimeException(String.format("Unexpected sase.pattern type: %s", patternSpecification.getType()));
		}
	}
	
	private static void preprocessPatternSpecification(PatternSpecification patternSpecification) {
		for (String[][] subStructure : patternSpecification.getStructure()) {
			for (String[] subSubStructure : subStructure) {
				for (int i = 0; i < subSubStructure.length; ++i) {
					try {
						Integer index = Integer.parseInt(subSubStructure[i]);
						subSubStructure[i] = EventTypesManager.getInstance().getKnownEventTypeNames().get(index);
					} catch (NumberFormatException e) {
					}
				}
			}
		}
	}
	
	private static Pattern createPattern(PatternSpecification patternSpecification, InputSpecification inputSpecification) {
		if (!EventTypesManager.isInitialized()) {
			createEventTypesManager(patternSpecification, inputSpecification);
		}
		EventTypesManager.getInstance().initializeTypes();
		
		Pattern pattern = createPatternByType(patternSpecification);
		if (pattern == null) {
			return null;
		}
		return (patternSpecification.getVerifierType() == SlaVerifierTypes.NONE) ? 
					pattern : 
					SlaVerifierFactory.createSlaAwarePattern((CompositePattern)pattern, patternSpecification.getVerifierType());
	}
	
	private static Pattern createPatternByType(PatternSpecification patternSpecification) {
		switch(patternSpecification.getType()) {
			case TRAFFIC_PATTERN:
				preprocessPatternSpecification(patternSpecification);
			case STOCK_PATTERN:
			case TRAM_CONGESTION_PATTERN:
			case SPEEDD_CREDIT_CARD_FRAUD_PATTERN:
			case SPEEDD_TRAFFIC_PATTERN:
			case SYNTHETIC_PATTERN:
				return new CompositePatternCreator(patternSpecification.getStructure(), 
												   patternSpecification.getConditions(),
												   patternSpecification.getNegatedEventNames(),
												   patternSpecification.getIteratedEventNames(),
												   patternSpecification.getTimeWindow()).createPattern();
			case OLD_SEQUENCE_STOCK_CORRELATION:
			case OLD_CONJUNCTION_STOCK_CORRELATION:
			default:
				return null;
		}
	}
	
	public static IWorkloadManager createPatternWorkload(WorkloadSpecification workloadSpecification,
													  	InputSpecification inputSpecification) {
		List<Pattern> patterns = new ArrayList<Pattern>();
		for (PatternSpecification patternSpecification : workloadSpecification.getPatternSpecifications()) {
			patterns.add(createPattern(patternSpecification, inputSpecification));
		}
		switch(workloadSpecification.getManagerType()) {
			case SINGLE:
				return new SinglePatternWorkloadManager(patterns.get(0));
			case MULTI_STATIC:
				return new MultiPatternWorkloadManager(patterns);
			case MULTI_DYNAMIC:
				DynamicMultiPatternWorkloadSpecification dynamicMultiPatternWorkloadSpecification = 
													(DynamicMultiPatternWorkloadSpecification)workloadSpecification;
				return new DynamicMultiPatternWorkloadManager(
													  patterns, 
													  dynamicMultiPatternWorkloadSpecification.initialCurrentToReservedRatio, 
													  dynamicMultiPatternWorkloadSpecification.workloadModificationProbability);
			default:
				return null;
		}
	}
}
