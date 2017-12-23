package pattern.creation;

import pattern.EventTypesManager;
import pattern.Pattern;
import specification.InputSpecification;
import specification.PatternSpecification;
import user.speedd.fraud.CreditCardFraudEventTypesManager;
import user.speedd.traffic.TrafficEventTypesManager;
import user.stocks.StockEventTypesManager;
import user.synthetic.SyntheticEventTypesManager;
import user.traffic.AarhusTrafficEventTypesManager;
import user.trams.TramEventTypesManager;

/**
 * This class is responsible for pattern creation.
 */
public class PatternFactory {

	private static void createEventTypesManager(PatternSpecification patternSpecification,
												InputSpecification inputSpecification) {
		//TODO: there is an implicit assumption here that ALL specifications have the same pattern type. OK for now
		//(since anyway there is only one input source as of now), but must be fixed in future!
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
				EventTypesManager.setInstance(new SyntheticEventTypesManager(inputSpecification.numberOfEventTypes));
				break;
			case TRAFFIC_PATTERN:
				EventTypesManager.setInstance(new AarhusTrafficEventTypesManager());
				break;
			default:
				throw new RuntimeException(String.format("Unexpected pattern type: %s", patternSpecification.getType()));
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
	
	public static Pattern createPattern(PatternSpecification patternSpecification, InputSpecification inputSpecification) {
		if (!EventTypesManager.isInitialized()) {
			createEventTypesManager(patternSpecification, inputSpecification);
		}
		EventTypesManager.getInstance().initializeTypes();
		
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
}
