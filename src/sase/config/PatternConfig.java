package sase.config;

import sase.multi.sla.SlaVerifierTypes;
import sase.pattern.creation.PatternTypes;
import sase.specification.condition.ConditionSpecification;
import sase.specification.workload.ParallelPatternSpecification;
import sase.specification.workload.PatternSpecification;
import sase.user.stocks.StockEventTypesManager;
import sase.user.stocks.specification.StockCorrelationConditionSpecification;
import sase.user.stocks.specification.StockDeltaOrderingConditionSpecification;
import sase.user.stocks.specification.StockSameCompanyNameFirstLetterConditionSpecification;
import sase.user.synthetic.SyntheticConditionSpecification;
import sase.user.traffic.TrafficSpeedToVehiclesNumberCorrelationConditionSpecification;

public class PatternConfig {
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Stocks
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Sequences */
	public static final PatternSpecification sequenceOfThree = 
			new PatternSpecification("SEQ3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThree,
									 ConditionConfig.sequenceOfThree, SlaVerifierTypes.NONE);

	public static final PatternSpecification sequenceOfThreeNAEUCA = 
			new PatternSpecification("SEQ3NAEUCA", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.sequenceOfThreeNAEUCA,
									 ConditionConfig.sequenceOfThreeNAEUCA, SlaVerifierTypes.NONE);

	public static final PatternSpecification sequenceOfThreeEUASCA = 
			new PatternSpecification("SEQ3EUASCA", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.sequenceOfThreeEUASCA,
									 ConditionConfig.sequenceOfThreeEUASCA, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFourEager = 
			new PatternSpecification("SEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFour,
									 ConditionConfig.sequenceOfFourEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFiveEager = 
			new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFive,
									 ConditionConfig.sequenceOfFiveEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfSixEager = 
			new PatternSpecification("SEQ6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSix,
									 ConditionConfig.sequenceOfSixEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfSevenEager = 
			new PatternSpecification("SEQ7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSeven,
									 ConditionConfig.sequenceOfSevenEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfEightEager = 
			new PatternSpecification("SEQ8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfEight,
									 ConditionConfig.sequenceOfEightEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFourLazy = 
			new PatternSpecification("SEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFour,
									 ConditionConfig.sequenceOfFourLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFiveLazy = 
			new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFive,
									 ConditionConfig.sequenceOfFiveLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfSixLazy = 
			new PatternSpecification("SEQ6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSix,
									 ConditionConfig.sequenceOfSixLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfSevenLazy = 
			new PatternSpecification("SEQ7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSeven,
									 ConditionConfig.sequenceOfSevenLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfEightLazy = 
			new PatternSpecification("SEQ8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfEight,
									 ConditionConfig.sequenceOfEightLazy, SlaVerifierTypes.NONE);

	
	
	/* Conjunctions */
	public static final PatternSpecification conjunctionOfTwo = 
			new PatternSpecification("AND2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.conjunctionOfTwo,
									 ConditionConfig.sequenceOfTwo, SlaVerifierTypes.NONE);

	public static final PatternSpecification conjunctionOfThree = 
			new PatternSpecification("AND3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.conjunctionOfThree,
									 ConditionConfig.conjunctionOfThree, SlaVerifierTypes.NONE);

	public static final PatternSpecification conjunctionOfThreeEUSAAFR = 
			new PatternSpecification("AND3EUSAAFR", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.conjunctionOfThreeEUSAAFR,
									 ConditionConfig.conjunctionOfThreeEUSAAFR, SlaVerifierTypes.NONE);

	public static final PatternSpecification conjunctionOfThreeCASAAU = 
			new PatternSpecification("AND3CASAAU", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.conjunctionOfThreeCASAAU,
									 ConditionConfig.conjunctionOfThreeCASAAU, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfFourEager = 
			new PatternSpecification("AND4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFour,
									 ConditionConfig.sequenceOfFourEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfFiveEager = 
			new PatternSpecification("AND5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFive,
									 ConditionConfig.sequenceOfFiveEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfSixEager = 
			new PatternSpecification("AND6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSix,
									 ConditionConfig.sequenceOfSixEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfSevenEager = 
			new PatternSpecification("AND7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSeven,
									 ConditionConfig.sequenceOfSevenEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfEightEager = 
			new PatternSpecification("AND8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfEight,
									 ConditionConfig.sequenceOfEightEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfFourLazy = 
			new PatternSpecification("AND4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFour,
									 ConditionConfig.sequenceOfFourLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfFiveLazy = 
			new PatternSpecification("AND5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFive,
									 ConditionConfig.sequenceOfFiveLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfSixLazy = 
			new PatternSpecification("AND6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSix,
									 ConditionConfig.sequenceOfSixLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfSevenLazy = 
			new PatternSpecification("AND7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSeven,
									 ConditionConfig.sequenceOfSevenLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification conjunctionOfEightLazy = 
			new PatternSpecification("AND8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfEight,
									 ConditionConfig.sequenceOfEightLazy, SlaVerifierTypes.NONE);


	
	/* Conjunctions of sequences */
	public static final PatternSpecification conjunctionOfTwoSequencesEager = 
			new PatternSpecification("ANDSEQ2", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfTwoSequences,
									 ConditionConfig.sequenceOfFourEager, SlaVerifierTypes.NONE);

	public static final PatternSpecification conjunctionOfFourSequencesEager = 
			new PatternSpecification("ANDSEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFourSequences,
									 ConditionConfig.sequenceOfEightEager, SlaVerifierTypes.NONE);

	public static final PatternSpecification conjunctionOfTwoSequencesLazy = 
			new PatternSpecification("ANDSEQ2", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfTwoSequences,
									 ConditionConfig.sequenceOfFourLazy, SlaVerifierTypes.NONE);

	public static final PatternSpecification conjunctionOfFourSequencesLazy = 
			new PatternSpecification("ANDSEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFourSequences,
									 ConditionConfig.sequenceOfEightLazy, SlaVerifierTypes.NONE);
	
	
	
	/* Negations */
	public static final PatternSpecification negativeSequenceOfThree = 
			new PatternSpecification("NEGSEQ3MID", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThree,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification negativeSequenceOfThreeEUFirst = 
			new PatternSpecification("NEGSEQ3BEG", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeEUFirst,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast, SlaVerifierTypes.NONE);//not a typo!
	
	public static final PatternSpecification negativeSequenceOfThreeEULast = 
			new PatternSpecification("NEGSEQ3END", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeEULast,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast, SlaVerifierTypes.NONE);

	public static final PatternSpecification negativeConjunctionOfThree = 
			new PatternSpecification("NEGAND3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.conjunctionOfThree,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification negativeSequenceOfFiveEager = 
			new PatternSpecification("NEGSEQ5", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfFiveForNegation,
									 new String[] {StockEventTypesManager.asianCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfFiveNegativeEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification negativeSequenceOfFiveLazy = 
			new PatternSpecification("NEGSEQ5", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfFiveForNegation,
									 new String[] {StockEventTypesManager.asianCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfFiveNegativeLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification negativeSequenceOfEightEager = 
			new PatternSpecification("NEGSEQ8", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfEight,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName,
										 	   	   StockEventTypesManager.centralAmericanCompanyEventTypeName,
											 	   StockEventTypesManager.middleEasternCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfEightNegativeEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification negativeSequenceOfEightLazy = 
			new PatternSpecification("NEGSEQ8", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfEight,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName,
										 	   StockEventTypesManager.centralAmericanCompanyEventTypeName,
										 	   StockEventTypesManager.middleEasternCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfEightNegativeLazy, SlaVerifierTypes.NONE);
	
	
	/* Disjunctions */
	public static final PatternSpecification disjunctionOfTwoSequencesOfTwo = 
			new PatternSpecification("OR2_2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfTwo,
									 ConditionConfig.disjunctionOfTwoSequencesOfTwo, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification disjunctionOfTwoSequencesOfTwoNACAEUSA = 
			new PatternSpecification("OR2_2NACAEUSA", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.disjunctionOfTwoSequencesOfTwoNACAEUSA,
									 ConditionConfig.disjunctionOfTwoSequencesOfTwoNACAEUSA, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification disjunctionOfTwoSequencesOfTwoEUASCAME = 
			new PatternSpecification("OR2_2EUASCAME", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.disjunctionOfTwoSequencesOfTwoEUASCAME,
									 ConditionConfig.disjunctionOfTwoSequencesOfTwoEUASCAME, SlaVerifierTypes.NONE);

	public static final PatternSpecification disjunctionOfThreeSequencesOfTwo = 
			new PatternSpecification("OR3_2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfThreeSequencesOfTwo,
									 ConditionConfig.disjunctionOfThreeSequencesOfTwo, SlaVerifierTypes.NONE);

	public static final PatternSpecification disjunctionOfFourSequencesOfTwo = 
			new PatternSpecification("OR4_2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfFourSequencesOfTwo,
									 ConditionConfig.disjunctionOfFourSequencesOfTwo, SlaVerifierTypes.NONE);

	public static final PatternSpecification disjunctionOfTwoSequencesOfThreeEager = 
			new PatternSpecification("OR2_3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfThree,
									 ConditionConfig.disjunctionOfTwoSequencesOfThreeEager, SlaVerifierTypes.NONE);

	public static final PatternSpecification disjunctionOfTwoSequencesOfThreeLazy = 
			new PatternSpecification("OR2_3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfThree,
									 ConditionConfig.disjunctionOfTwoSequencesOfThreeLazy, SlaVerifierTypes.NONE);

	public static final PatternSpecification disjunctionOfTwoSequencesOfFourEager = 
			new PatternSpecification("OR2_4", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfFour,
									 ConditionConfig.disjunctionOfTwoSequencesOfFourEager, SlaVerifierTypes.NONE);

	public static final PatternSpecification disjunctionOfTwoSequencesOfFourLazy = 
			new PatternSpecification("OR2_4", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfFour,
									 ConditionConfig.disjunctionOfTwoSequencesOfFourLazy, SlaVerifierTypes.NONE);
	
	
	
	/* Iterations/Aggregations */
	public static final PatternSpecification sequenceOfThreeWithIteration = 
			new PatternSpecification("ITER3MID", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeForIteration,
									 null,
									 new String[] {StockEventTypesManager.africanCompanyEventTypeName},
									 ConditionConfig.sequenceOfThreeWithMiddleIteration, SlaVerifierTypes.NONE);
	
	
	//Equality patterns for SASE comparison
	public static final PatternSpecification sequenceOfThreeWithEquation = 
			new PatternSpecification("SEQ3EQ", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeForEqualitySequence,
									 ConditionConfig.sequenceOfThreeEquality, SlaVerifierTypes.NONE);
	public static final PatternSpecification sequenceOfFourWithNegation = 
			new PatternSpecification("NEG3EQ", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfFourForEqualityNegation,
									 new String[] {StockEventTypesManager.asianCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfFourNegativeEqualityLazy, SlaVerifierTypes.NONE);
	public static final PatternSpecification sequenceOfThreeWithEquationIteration = 
			new PatternSpecification("ITER3EQ", PatternTypes.STOCK_PATTERN, (long)60, 
									 StructureConfig.sequenceOfThreeForEqualityIteration,
									 null,
									 new String[] {StockEventTypesManager.africanCompanyEventTypeName},
									 ConditionConfig.sequenceOfThreeEqualityIteration, SlaVerifierTypes.NONE);
	
	
	////////////////////////////////////////Test configuration/////////////////////////////////////
	public static final PatternSpecification testSequence = 
			new PatternSpecification("SEQTEST", PatternTypes.STOCK_PATTERN, (long)20,
									 StructureConfig.testSequence,
									 ConditionConfig.testSequence, SlaVerifierTypes.NONE);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Trams
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final PatternSpecification sequenceOfThreeCongestions = 
			new PatternSpecification("SEQ3", PatternTypes.TRAM_CONGESTION_PATTERN, null, 
									 StructureConfig.sequenceOfThreeCongestions,
									 ConditionConfig.sequenceOfThreeCongestions, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFourCongestions = 
			new PatternSpecification("SEQ4", PatternTypes.TRAM_CONGESTION_PATTERN, null,
									 StructureConfig.sequenceOfFourCongestions,
									 ConditionConfig.sequenceOfFourCongestions, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFiveCongestions = 
			new PatternSpecification("SEQ5", PatternTypes.TRAM_CONGESTION_PATTERN, null,
									 StructureConfig.sequenceOfFiveCongestions,
									 ConditionConfig.sequenceOfFiveCongestions, SlaVerifierTypes.NONE);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Traffic Speed
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final PatternSpecification sequenceOfThreeSpeedMeasurementsEager = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null, 
									 StructureConfig.sequenceOfThreeSpeedMeasurements,
									 ConditionConfig.sequenceOfThreeSpeedMeasurementsEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFourSpeedMeasurementsEager = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFourSpeedMeasurements,
									 ConditionConfig.sequenceOfFourSpeedMeasurementsEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFiveSpeedMeasurementsEager = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFiveSpeedMeasurements,
									 ConditionConfig.sequenceOfFiveSpeedMeasurementsEager, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfThreeSpeedMeasurementsLazy = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null, 
									 StructureConfig.sequenceOfThreeSpeedMeasurements,
									 ConditionConfig.sequenceOfThreeSpeedMeasurementsLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFourSpeedMeasurementsLazy = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFourSpeedMeasurements,
									 ConditionConfig.sequenceOfFourSpeedMeasurementsLazy, SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFiveSpeedMeasurementsLazy = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFiveSpeedMeasurements,
									 ConditionConfig.sequenceOfFiveSpeedMeasurementsLazy, SlaVerifierTypes.NONE);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Credit Card Fraud
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final PatternSpecification sequenceOfThreeTransactionsEager = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null, 
									 StructureConfig.sequenceOfThreeTransactions,
									 new ConditionSpecification[]{},//ConditionConfig.sequenceOfThreeTransactionsEager); 
									 SlaVerifierTypes.NONE);
	
	public static final PatternSpecification sequenceOfFourTransactionsEager = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFourTransactions,
									 new ConditionSpecification[]{}, 
									 SlaVerifierTypes.NONE);//ConditionConfig.sequenceOfFourTransactionsEager);
	
	public static final PatternSpecification sequenceOfFiveTransactionsEager = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFiveTransactions,
									 new ConditionSpecification[]{}, 
									 SlaVerifierTypes.NONE);//ConditionConfig.sequenceOfFiveTransactionsEager);
	
	public static final PatternSpecification sequenceOfThreeTransactionsLazy = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null, 
									 StructureConfig.sequenceOfThreeTransactions,
									 new ConditionSpecification[]{}, 
									 SlaVerifierTypes.NONE);//ConditionConfig.sequenceOfThreeTransactionsLazy);
	
	public static final PatternSpecification sequenceOfFourTransactionsLazy = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFourTransactions,
									 new ConditionSpecification[]{}, 
									 SlaVerifierTypes.NONE);//ConditionConfig.sequenceOfFourTransactionsLazy);
	
	public static final PatternSpecification sequenceOfFiveTransactionsLazy = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFiveTransactions,
									 new ConditionSpecification[]{}, 
									 SlaVerifierTypes.NONE);//ConditionConfig.sequenceOfFiveTransactionsLazy);
	
	
	
	private static final long stockByCompanyPatternTimeWindow = 10;
	public static final PatternSpecification[] stockByCompanyPatternSpecifications = {
            new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
                    new String[][][] {new String[][]{new String[]{
                            StockEventTypesManager.microsoftEventTypeName,
                            StockEventTypesManager.googleEventTypeName,
                            StockEventTypesManager.appleEventTypeName,
                            StockEventTypesManager.yahooEventTypeName,
                            StockEventTypesManager.amznEventTypeName
                    }}},
                    new ConditionSpecification[] {
                            new StockDeltaOrderingConditionSpecification(
                                    StockEventTypesManager.microsoftEventTypeName,
                                    StockEventTypesManager.googleEventTypeName),
                            new StockDeltaOrderingConditionSpecification(
                                    StockEventTypesManager.googleEventTypeName,
                                    StockEventTypesManager.appleEventTypeName),
                            new StockDeltaOrderingConditionSpecification(
                                    StockEventTypesManager.appleEventTypeName,
                                    StockEventTypesManager.yahooEventTypeName),
                            new StockDeltaOrderingConditionSpecification(
                                    StockEventTypesManager.yahooEventTypeName,
                                    StockEventTypesManager.amznEventTypeName),
                    },
                    SlaVerifierTypes.NONE),
            //////////////////////////////////////////////////////////////
            // MAX : This is second test for Hirzel!
//            new PatternSpecification("HIRZEL_TEST", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
//				new String[][][]{new String[][] {
//					new String[] {
//						StockEventTypesManager.northAmericanCompanyEventTypeName,
//						StockEventTypesManager.europeanCompanyEventTypeName,
//						StockEventTypesManager.asianCompanyEventTypeName,
//					}}},
//            		new ConditionSpecification[] {
//        				new StockSameCompanyNameFirstLetterConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName, 
//        						StockEventTypesManager.europeanCompanyEventTypeName),
//        				new StockSameCompanyNameFirstLetterConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName, 
//        						StockEventTypesManager.asianCompanyEventTypeName) },
//            		SlaVerifierTypes.NONE),
//            //////////////////////////////////////////////////////////////
//			new PatternSpecification("SEQ6", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
//					new String[][][] {new String[][]{new String[]{
//							StockEventTypesManager.microsoftEventTypeName,
//							StockEventTypesManager.googleEventTypeName,
//							StockEventTypesManager.ciscoEventTypeName,
//							StockEventTypesManager.intelEventTypeName,
//							StockEventTypesManager.yahooEventTypeName,
//							StockEventTypesManager.etfcEventTypeName
//					}}},
//					new ConditionSpecification[] {
//							new StockDeltaOrderingConditionSpecification(
//									StockEventTypesManager.microsoftEventTypeName,
//									StockEventTypesManager.googleEventTypeName),
//							new StockDeltaOrderingConditionSpecification(
//									StockEventTypesManager.googleEventTypeName,
//									StockEventTypesManager.ciscoEventTypeName),
//							new StockDeltaOrderingConditionSpecification(
//									StockEventTypesManager.ciscoEventTypeName,
//									StockEventTypesManager.intelEventTypeName),
//							new StockDeltaOrderingConditionSpecification(
//									StockEventTypesManager.intelEventTypeName,
//									StockEventTypesManager.yahooEventTypeName),
//							new StockDeltaOrderingConditionSpecification(
//									StockEventTypesManager.yahooEventTypeName,
//									StockEventTypesManager.etfcEventTypeName),
//					},
//					SlaVerifierTypes.NONE),

			new PatternSpecification("SEQ3", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
					new String[][][] {new String[][]{new String[]{
							StockEventTypesManager.microsoftEventTypeName,
							StockEventTypesManager.googleEventTypeName,
							StockEventTypesManager.appleEventTypeName
					}}},
					new ConditionSpecification[] {
							new StockDeltaOrderingConditionSpecification(
									StockEventTypesManager.microsoftEventTypeName,
									StockEventTypesManager.googleEventTypeName),
							new StockDeltaOrderingConditionSpecification(
									StockEventTypesManager.googleEventTypeName,
									StockEventTypesManager.appleEventTypeName),
					},
					SlaVerifierTypes.NONE),


/*

		new PatternSpecification("SEQ3", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.qtwwEventTypeName 
				 }}},
				 new ConditionSpecification[] {
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.microsoftEventTypeName,
							 StockEventTypesManager.ciscoEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.ciscoEventTypeName,
							 StockEventTypesManager.qtwwEventTypeName),
								 }, 
				SlaVerifierTypes.NONE),

		new PatternSpecification("SEQ4", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.qtwwEventTypeName 
				 }}},
				 new ConditionSpecification[] {
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.microsoftEventTypeName,
							 StockEventTypesManager.googleEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.googleEventTypeName,
							 StockEventTypesManager.ciscoEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.ciscoEventTypeName,
							 StockEventTypesManager.qtwwEventTypeName),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.qtwwEventTypeName 
				 }}},
				 new ConditionSpecification[] {
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.microsoftEventTypeName,
							 StockEventTypesManager.googleEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.googleEventTypeName,
							 StockEventTypesManager.ciscoEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.ciscoEventTypeName,
							 StockEventTypesManager.intelEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.intelEventTypeName,
							 StockEventTypesManager.qtwwEventTypeName),
				 },
				SlaVerifierTypes.NONE),

		new PatternSpecification("SEQ7", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.appleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.xtexEventTypeName,
						 StockEventTypesManager.qtwwEventTypeName 
				 }}},
				 new ConditionSpecification[] {
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.microsoftEventTypeName,
							 StockEventTypesManager.googleEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.googleEventTypeName,
							 StockEventTypesManager.appleEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.appleEventTypeName,
							 StockEventTypesManager.ciscoEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.ciscoEventTypeName,
							 StockEventTypesManager.intelEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.intelEventTypeName,
							 StockEventTypesManager.xtexEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.xtexEventTypeName,
							 StockEventTypesManager.qtwwEventTypeName),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ8", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.appleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.xtexEventTypeName,
						 StockEventTypesManager.xtlbEventTypeName,
						 StockEventTypesManager.qtwwEventTypeName 
				 }}},
				 new ConditionSpecification[] {
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.microsoftEventTypeName,
							 StockEventTypesManager.googleEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.googleEventTypeName,
							 StockEventTypesManager.appleEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.appleEventTypeName,
							 StockEventTypesManager.ciscoEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.ciscoEventTypeName,
							 StockEventTypesManager.intelEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.intelEventTypeName,
							 StockEventTypesManager.xtexEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.xtexEventTypeName,
							 StockEventTypesManager.xtlbEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.xtlbEventTypeName,
							 StockEventTypesManager.qtwwEventTypeName),
				 }, 
				SlaVerifierTypes.NONE),
/*
//		new PatternSpecification("SEQ9", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
//				 new String[][][] {new String[][]{new String[]{ 
//						 StockEventTypesManager.microsoftEventTypeName,
//						 StockEventTypesManager.yahooEventTypeName,
//						 StockEventTypesManager.googleEventTypeName,
//						 StockEventTypesManager.appleEventTypeName,
//						 StockEventTypesManager.ciscoEventTypeName,
//						 StockEventTypesManager.intelEventTypeName,
//						 StockEventTypesManager.crosstechEventTypeName,
//						 StockEventTypesManager.xtlbioEventTypeName,
//						 StockEventTypesManager.quantumEventTypeName 
//				 }}},
//				 new ConditionSpecification[] {
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.microsoftEventTypeName,
//							 StockEventTypesManager.yahooEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.yahooEventTypeName,
//							 StockEventTypesManager.googleEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.googleEventTypeName,
//							 StockEventTypesManager.appleEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.appleEventTypeName,
//							 StockEventTypesManager.ciscoEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.ciscoEventTypeName,
//							 StockEventTypesManager.intelEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.intelEventTypeName,
//							 StockEventTypesManager.crosstechEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.crosstechEventTypeName,
//							 StockEventTypesManager.xtlbioEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.xtlbioEventTypeName,
//							 StockEventTypesManager.quantumEventTypeName),
//				 }),
//		new PatternSpecification("SEQ10", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
//				 new String[][][] {new String[][]{new String[]{ 
//						 StockEventTypesManager.microsoftEventTypeName,
//						 StockEventTypesManager.yahooEventTypeName,
//						 StockEventTypesManager.googleEventTypeName,
//						 StockEventTypesManager.appleEventTypeName,
//						 StockEventTypesManager.ciscoEventTypeName,
//						 StockEventTypesManager.intelEventTypeName,
//						 StockEventTypesManager.illuminaEventTypeName,
//						 StockEventTypesManager.crosstechEventTypeName,
//						 StockEventTypesManager.xtlbioEventTypeName,
//						 StockEventTypesManager.quantumEventTypeName 
//				 }}},
//				 new ConditionSpecification[] {
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.microsoftEventTypeName,
//							 StockEventTypesManager.yahooEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.yahooEventTypeName,
//							 StockEventTypesManager.googleEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.googleEventTypeName,
//							 StockEventTypesManager.appleEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.appleEventTypeName,
//							 StockEventTypesManager.ciscoEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.ciscoEventTypeName,
//							 StockEventTypesManager.intelEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.intelEventTypeName,
//							 StockEventTypesManager.illuminaEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.illuminaEventTypeName,
//							 StockEventTypesManager.crosstechEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.crosstechEventTypeName,
//							 StockEventTypesManager.xtlbioEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.xtlbioEventTypeName,
//							 StockEventTypesManager.quantumEventTypeName),
//				 }),
*/
	};
	private static final long trafficPatternTimeWindow = 20;
	public static final PatternSpecification[] trafficPatternSpecifications = {
		new PatternSpecification("SEQ3", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "4", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,7),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ4", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "3", "4", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,7),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ5", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "3", "4", "5", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,7),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ6", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "2", "3", "4", "5", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,2),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(2,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,7),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ7", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "2", "3", "4", "5", "6", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,2),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(2,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,6),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(6,7),
				 }, 
				SlaVerifierTypes.NONE),
		new PatternSpecification("SEQ8", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "1", "2", "3", "4", "5", "6", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,1),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(1,2),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(2,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,6),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(6,7),
				 }, 
				SlaVerifierTypes.NONE),

	};
	private static final long syntheticPatternTimeWindow = 10;
	public static final PatternSpecification[] syntheticPatternSpecifications = {
		new PatternSpecification("SEQ4", PatternTypes.SYNTHETIC_PATTERN, syntheticPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "1", "2", "3"}}},
				 new ConditionSpecification[] {
					 new SyntheticConditionSpecification("0","1"),
					 new SyntheticConditionSpecification("1","2"),
					 new SyntheticConditionSpecification("2","3"),
					 new SyntheticConditionSpecification("3","0"),
				 }, SlaVerifierTypes.NONE),
	};
}
