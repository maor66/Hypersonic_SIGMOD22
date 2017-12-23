package sase.config;

import sase.pattern.creation.PatternTypes;
import sase.specification.ConditionSpecification;
import sase.specification.PatternSpecification;
import sase.user.stocks.StockEventTypesManager;
import sase.user.stocks.specification.StockDeltaOrderingConditionSpecification;
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
									 ConditionConfig.sequenceOfThree);

	public static final PatternSpecification sequenceOfThreeNAEUCA = 
			new PatternSpecification("SEQ3NAEUCA", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.sequenceOfThreeNAEUCA,
									 ConditionConfig.sequenceOfThreeNAEUCA);

	public static final PatternSpecification sequenceOfThreeEUASCA = 
			new PatternSpecification("SEQ3EUASCA", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.sequenceOfThreeEUASCA,
									 ConditionConfig.sequenceOfThreeEUASCA);
	
	public static final PatternSpecification sequenceOfFourEager = 
			new PatternSpecification("SEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFour,
									 ConditionConfig.sequenceOfFourEager);
	
	public static final PatternSpecification sequenceOfFiveEager = 
			new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFive,
									 ConditionConfig.sequenceOfFiveEager);
	
	public static final PatternSpecification sequenceOfSixEager = 
			new PatternSpecification("SEQ6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSix,
									 ConditionConfig.sequenceOfSixEager);
	
	public static final PatternSpecification sequenceOfSevenEager = 
			new PatternSpecification("SEQ7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSeven,
									 ConditionConfig.sequenceOfSevenEager);
	
	public static final PatternSpecification sequenceOfEightEager = 
			new PatternSpecification("SEQ8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfEight,
									 ConditionConfig.sequenceOfEightEager);
	
	public static final PatternSpecification sequenceOfFourLazy = 
			new PatternSpecification("SEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFour,
									 ConditionConfig.sequenceOfFourLazy);
	
	public static final PatternSpecification sequenceOfFiveLazy = 
			new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfFive,
									 ConditionConfig.sequenceOfFiveLazy);
	
	public static final PatternSpecification sequenceOfSixLazy = 
			new PatternSpecification("SEQ6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSix,
									 ConditionConfig.sequenceOfSixLazy);
	
	public static final PatternSpecification sequenceOfSevenLazy = 
			new PatternSpecification("SEQ7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfSeven,
									 ConditionConfig.sequenceOfSevenLazy);
	
	public static final PatternSpecification sequenceOfEightLazy = 
			new PatternSpecification("SEQ8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.sequenceOfEight,
									 ConditionConfig.sequenceOfEightLazy);

	
	
	/* Conjunctions */
	public static final PatternSpecification conjunctionOfTwo = 
			new PatternSpecification("AND2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.conjunctionOfTwo,
									 ConditionConfig.sequenceOfTwo);

	public static final PatternSpecification conjunctionOfThree = 
			new PatternSpecification("AND3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.conjunctionOfThree,
									 ConditionConfig.conjunctionOfThree);

	public static final PatternSpecification conjunctionOfThreeEUSAAFR = 
			new PatternSpecification("AND3EUSAAFR", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.conjunctionOfThreeEUSAAFR,
									 ConditionConfig.conjunctionOfThreeEUSAAFR);

	public static final PatternSpecification conjunctionOfThreeCASAAU = 
			new PatternSpecification("AND3CASAAU", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.conjunctionOfThreeCASAAU,
									 ConditionConfig.conjunctionOfThreeCASAAU);
	
	public static final PatternSpecification conjunctionOfFourEager = 
			new PatternSpecification("AND4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFour,
									 ConditionConfig.sequenceOfFourEager);
	
	public static final PatternSpecification conjunctionOfFiveEager = 
			new PatternSpecification("AND5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFive,
									 ConditionConfig.sequenceOfFiveEager);
	
	public static final PatternSpecification conjunctionOfSixEager = 
			new PatternSpecification("AND6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSix,
									 ConditionConfig.sequenceOfSixEager);
	
	public static final PatternSpecification conjunctionOfSevenEager = 
			new PatternSpecification("AND7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSeven,
									 ConditionConfig.sequenceOfSevenEager);
	
	public static final PatternSpecification conjunctionOfEightEager = 
			new PatternSpecification("AND8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfEight,
									 ConditionConfig.sequenceOfEightEager);
	
	public static final PatternSpecification conjunctionOfFourLazy = 
			new PatternSpecification("AND4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFour,
									 ConditionConfig.sequenceOfFourLazy);
	
	public static final PatternSpecification conjunctionOfFiveLazy = 
			new PatternSpecification("AND5", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFive,
									 ConditionConfig.sequenceOfFiveLazy);
	
	public static final PatternSpecification conjunctionOfSixLazy = 
			new PatternSpecification("AND6", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSix,
									 ConditionConfig.sequenceOfSixLazy);
	
	public static final PatternSpecification conjunctionOfSevenLazy = 
			new PatternSpecification("AND7", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfSeven,
									 ConditionConfig.sequenceOfSevenLazy);
	
	public static final PatternSpecification conjunctionOfEightLazy = 
			new PatternSpecification("AND8", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfEight,
									 ConditionConfig.sequenceOfEightLazy);


	
	/* Conjunctions of sequences */
	public static final PatternSpecification conjunctionOfTwoSequencesEager = 
			new PatternSpecification("ANDSEQ2", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfTwoSequences,
									 ConditionConfig.sequenceOfFourEager);

	public static final PatternSpecification conjunctionOfFourSequencesEager = 
			new PatternSpecification("ANDSEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFourSequences,
									 ConditionConfig.sequenceOfEightEager);

	public static final PatternSpecification conjunctionOfTwoSequencesLazy = 
			new PatternSpecification("ANDSEQ2", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfTwoSequences,
									 ConditionConfig.sequenceOfFourLazy);

	public static final PatternSpecification conjunctionOfFourSequencesLazy = 
			new PatternSpecification("ANDSEQ4", PatternTypes.STOCK_PATTERN, null,
									 StructureConfig.conjunctionOfFourSequences,
									 ConditionConfig.sequenceOfEightLazy);
	
	
	
	/* Negations */
	public static final PatternSpecification negativeSequenceOfThree = 
			new PatternSpecification("NEGSEQ3MID", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThree,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast);
	
	public static final PatternSpecification negativeSequenceOfThreeEUFirst = 
			new PatternSpecification("NEGSEQ3BEG", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeEUFirst,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast);//not a typo!
	
	public static final PatternSpecification negativeSequenceOfThreeEULast = 
			new PatternSpecification("NEGSEQ3END", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeEULast,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast);

	public static final PatternSpecification negativeConjunctionOfThree = 
			new PatternSpecification("NEGAND3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.conjunctionOfThree,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfThreeEULast);
	
	public static final PatternSpecification negativeSequenceOfFiveEager = 
			new PatternSpecification("NEGSEQ5", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfFiveForNegation,
									 new String[] {StockEventTypesManager.asianCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfFiveNegativeEager);
	
	public static final PatternSpecification negativeSequenceOfFiveLazy = 
			new PatternSpecification("NEGSEQ5", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfFiveForNegation,
									 new String[] {StockEventTypesManager.asianCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfFiveNegativeLazy);
	
	public static final PatternSpecification negativeSequenceOfEightEager = 
			new PatternSpecification("NEGSEQ8", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfEight,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName,
										 	   	   StockEventTypesManager.centralAmericanCompanyEventTypeName,
											 	   StockEventTypesManager.middleEasternCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfEightNegativeEager);
	
	public static final PatternSpecification negativeSequenceOfEightLazy = 
			new PatternSpecification("NEGSEQ8", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfEight,
									 new String[] {StockEventTypesManager.europeanCompanyEventTypeName,
										 	   StockEventTypesManager.centralAmericanCompanyEventTypeName,
										 	   StockEventTypesManager.middleEasternCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfEightNegativeLazy);
	
	
	/* Disjunctions */
	public static final PatternSpecification disjunctionOfTwoSequencesOfTwo = 
			new PatternSpecification("OR2_2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfTwo,
									 ConditionConfig.disjunctionOfTwoSequencesOfTwo);
	
	public static final PatternSpecification disjunctionOfTwoSequencesOfTwoNACAEUSA = 
			new PatternSpecification("OR2_2NACAEUSA", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.disjunctionOfTwoSequencesOfTwoNACAEUSA,
									 ConditionConfig.disjunctionOfTwoSequencesOfTwoNACAEUSA);
	
	public static final PatternSpecification disjunctionOfTwoSequencesOfTwoEUASCAME = 
			new PatternSpecification("OR2_2EUASCAME", PatternTypes.STOCK_PATTERN, (long)20, 
									 StructureConfig.disjunctionOfTwoSequencesOfTwoEUASCAME,
									 ConditionConfig.disjunctionOfTwoSequencesOfTwoEUASCAME);

	public static final PatternSpecification disjunctionOfThreeSequencesOfTwo = 
			new PatternSpecification("OR3_2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfThreeSequencesOfTwo,
									 ConditionConfig.disjunctionOfThreeSequencesOfTwo);

	public static final PatternSpecification disjunctionOfFourSequencesOfTwo = 
			new PatternSpecification("OR4_2", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfFourSequencesOfTwo,
									 ConditionConfig.disjunctionOfFourSequencesOfTwo);

	public static final PatternSpecification disjunctionOfTwoSequencesOfThreeEager = 
			new PatternSpecification("OR2_3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfThree,
									 ConditionConfig.disjunctionOfTwoSequencesOfThreeEager);

	public static final PatternSpecification disjunctionOfTwoSequencesOfThreeLazy = 
			new PatternSpecification("OR2_3", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfThree,
									 ConditionConfig.disjunctionOfTwoSequencesOfThreeLazy);

	public static final PatternSpecification disjunctionOfTwoSequencesOfFourEager = 
			new PatternSpecification("OR2_4", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfFour,
									 ConditionConfig.disjunctionOfTwoSequencesOfFourEager);

	public static final PatternSpecification disjunctionOfTwoSequencesOfFourLazy = 
			new PatternSpecification("OR2_4", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.disjunctionOfTwoSequencesOfFour,
									 ConditionConfig.disjunctionOfTwoSequencesOfFourLazy);
	
	
	
	/* Iterations/Aggregations */
	public static final PatternSpecification sequenceOfThreeWithIteration = 
			new PatternSpecification("ITER3MID", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeForIteration,
									 null,
									 new String[] {StockEventTypesManager.africanCompanyEventTypeName},
									 ConditionConfig.sequenceOfThreeWithMiddleIteration);
	
	
	//Equality patterns for SASE comparison
	public static final PatternSpecification sequenceOfThreeWithEquation = 
			new PatternSpecification("SEQ3EQ", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfThreeForEqualitySequence,
									 ConditionConfig.sequenceOfThreeEquality);
	public static final PatternSpecification sequenceOfFourWithNegation = 
			new PatternSpecification("NEG3EQ", PatternTypes.STOCK_PATTERN, null, 
									 StructureConfig.sequenceOfFourForEqualityNegation,
									 new String[] {StockEventTypesManager.asianCompanyEventTypeName},
									 null,
									 ConditionConfig.sequenceOfFourNegativeEqualityLazy);
	public static final PatternSpecification sequenceOfThreeWithEquationIteration = 
			new PatternSpecification("ITER3EQ", PatternTypes.STOCK_PATTERN, (long)60, 
									 StructureConfig.sequenceOfThreeForEqualityIteration,
									 null,
									 new String[] {StockEventTypesManager.africanCompanyEventTypeName},
									 ConditionConfig.sequenceOfThreeEqualityIteration);
	
	
	////////////////////////////////////////Test configuration/////////////////////////////////////
	public static final PatternSpecification testSequenceOfFive = 
			new PatternSpecification("SEQ5TEST", PatternTypes.STOCK_PATTERN, (long)20,
									 StructureConfig.sequenceOfFive,
									 ConditionConfig.testSequenceOfFive);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Trams
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final PatternSpecification sequenceOfThreeCongestions = 
			new PatternSpecification("SEQ3", PatternTypes.TRAM_CONGESTION_PATTERN, null, 
									 StructureConfig.sequenceOfThreeCongestions,
									 ConditionConfig.sequenceOfThreeCongestions);
	
	public static final PatternSpecification sequenceOfFourCongestions = 
			new PatternSpecification("SEQ4", PatternTypes.TRAM_CONGESTION_PATTERN, null,
									 StructureConfig.sequenceOfFourCongestions,
									 ConditionConfig.sequenceOfFourCongestions);
	
	public static final PatternSpecification sequenceOfFiveCongestions = 
			new PatternSpecification("SEQ5", PatternTypes.TRAM_CONGESTION_PATTERN, null,
									 StructureConfig.sequenceOfFiveCongestions,
									 ConditionConfig.sequenceOfFiveCongestions);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Traffic Speed
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final PatternSpecification sequenceOfThreeSpeedMeasurementsEager = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null, 
									 StructureConfig.sequenceOfThreeSpeedMeasurements,
									 ConditionConfig.sequenceOfThreeSpeedMeasurementsEager);
	
	public static final PatternSpecification sequenceOfFourSpeedMeasurementsEager = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFourSpeedMeasurements,
									 ConditionConfig.sequenceOfFourSpeedMeasurementsEager);
	
	public static final PatternSpecification sequenceOfFiveSpeedMeasurementsEager = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFiveSpeedMeasurements,
									 ConditionConfig.sequenceOfFiveSpeedMeasurementsEager);
	
	public static final PatternSpecification sequenceOfThreeSpeedMeasurementsLazy = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null, 
									 StructureConfig.sequenceOfThreeSpeedMeasurements,
									 ConditionConfig.sequenceOfThreeSpeedMeasurementsLazy);
	
	public static final PatternSpecification sequenceOfFourSpeedMeasurementsLazy = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFourSpeedMeasurements,
									 ConditionConfig.sequenceOfFourSpeedMeasurementsLazy);
	
	public static final PatternSpecification sequenceOfFiveSpeedMeasurementsLazy = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_TRAFFIC_PATTERN, null,
									 StructureConfig.sequenceOfFiveSpeedMeasurements,
									 ConditionConfig.sequenceOfFiveSpeedMeasurementsLazy);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Credit Card Fraud
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final PatternSpecification sequenceOfThreeTransactionsEager = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null, 
									 StructureConfig.sequenceOfThreeTransactions,
									 new ConditionSpecification[]{});//ConditionConfig.sequenceOfThreeTransactionsEager);
	
	public static final PatternSpecification sequenceOfFourTransactionsEager = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFourTransactions,
									 new ConditionSpecification[]{});//ConditionConfig.sequenceOfFourTransactionsEager);
	
	public static final PatternSpecification sequenceOfFiveTransactionsEager = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFiveTransactions,
									 new ConditionSpecification[]{});//ConditionConfig.sequenceOfFiveTransactionsEager);
	
	public static final PatternSpecification sequenceOfThreeTransactionsLazy = 
			new PatternSpecification("SEQ3", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null, 
									 StructureConfig.sequenceOfThreeTransactions,
									 new ConditionSpecification[]{});//ConditionConfig.sequenceOfThreeTransactionsLazy);
	
	public static final PatternSpecification sequenceOfFourTransactionsLazy = 
			new PatternSpecification("SEQ4", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFourTransactions,
									 new ConditionSpecification[]{});//ConditionConfig.sequenceOfFourTransactionsLazy);
	
	public static final PatternSpecification sequenceOfFiveTransactionsLazy = 
			new PatternSpecification("SEQ5", PatternTypes.SPEEDD_CREDIT_CARD_FRAUD_PATTERN, null,
									 StructureConfig.sequenceOfFiveTransactions,
									 new ConditionSpecification[]{});//ConditionConfig.sequenceOfFiveTransactionsLazy);
	
	
	
	private static final long stockByCompanyPatternTimeWindow = 10;
	public static final PatternSpecification[] stockByCompanyPatternSpecifications = {
		new PatternSpecification("SEQ3", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.rambusEventTypeName 
				 }}},
				 new ConditionSpecification[] {
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.microsoftEventTypeName,
							 StockEventTypesManager.ciscoEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.ciscoEventTypeName,
							 StockEventTypesManager.rambusEventTypeName),
								 }),
		new PatternSpecification("SEQ4", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.rambusEventTypeName 
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
							 StockEventTypesManager.rambusEventTypeName),
				 }),
		new PatternSpecification("SEQ5", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.rambusEventTypeName 
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
							 StockEventTypesManager.rambusEventTypeName),
				 }),
		new PatternSpecification("SEQ6", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.etradeEventTypeName,
						 StockEventTypesManager.rambusEventTypeName 
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
							 StockEventTypesManager.etradeEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.etradeEventTypeName,
							 StockEventTypesManager.rambusEventTypeName),
				 }),
		new PatternSpecification("SEQ7", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.appleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.etradeEventTypeName,
						 StockEventTypesManager.rambusEventTypeName 
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
							 StockEventTypesManager.etradeEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.etradeEventTypeName,
							 StockEventTypesManager.rambusEventTypeName),
				 }),
		new PatternSpecification("SEQ8", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ 
						 StockEventTypesManager.microsoftEventTypeName,
						 StockEventTypesManager.googleEventTypeName,
						 StockEventTypesManager.appleEventTypeName,
						 StockEventTypesManager.ciscoEventTypeName,
						 StockEventTypesManager.intelEventTypeName,
						 StockEventTypesManager.etradeEventTypeName,
						 StockEventTypesManager.broadcomEventTypeName,
						 StockEventTypesManager.rambusEventTypeName 
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
							 StockEventTypesManager.etradeEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.etradeEventTypeName,
							 StockEventTypesManager.broadcomEventTypeName),
					 new StockDeltaOrderingConditionSpecification(
							 StockEventTypesManager.broadcomEventTypeName,
							 StockEventTypesManager.rambusEventTypeName),
				 }),
//		new PatternSpecification("SEQ9", PatternTypes.STOCK_PATTERN, stockByCompanyPatternTimeWindow,
//				 new String[][][] {new String[][]{new String[]{ 
//						 StockEventTypesManager.microsoftEventTypeName,
//						 StockEventTypesManager.yahooEventTypeName,
//						 StockEventTypesManager.googleEventTypeName,
//						 StockEventTypesManager.appleEventTypeName,
//						 StockEventTypesManager.ciscoEventTypeName,
//						 StockEventTypesManager.intelEventTypeName,
//						 StockEventTypesManager.etradeEventTypeName,
//						 StockEventTypesManager.broadcomEventTypeName,
//						 StockEventTypesManager.rambusEventTypeName 
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
//							 StockEventTypesManager.etradeEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.etradeEventTypeName,
//							 StockEventTypesManager.broadcomEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.broadcomEventTypeName,
//							 StockEventTypesManager.rambusEventTypeName),
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
//						 StockEventTypesManager.etradeEventTypeName,
//						 StockEventTypesManager.broadcomEventTypeName,
//						 StockEventTypesManager.rambusEventTypeName 
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
//							 StockEventTypesManager.etradeEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.etradeEventTypeName,
//							 StockEventTypesManager.broadcomEventTypeName),
//					 new StockDeltaOrderingConditionSpecification(
//							 StockEventTypesManager.broadcomEventTypeName,
//							 StockEventTypesManager.rambusEventTypeName),
//				 }),
	};
	private static final long trafficPatternTimeWindow = 20;
	public static final PatternSpecification[] trafficPatternSpecifications = {
		new PatternSpecification("SEQ3", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "4", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,7),
				 }),
		new PatternSpecification("SEQ4", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "3", "4", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,7),
				 }),
		new PatternSpecification("SEQ5", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "3", "4", "5", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,7),
				 }),
		new PatternSpecification("SEQ6", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "2", "3", "4", "5", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,2),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(2,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,7),
				 }),
		new PatternSpecification("SEQ7", PatternTypes.TRAFFIC_PATTERN, trafficPatternTimeWindow,
				 new String[][][] {new String[][]{new String[]{ "0", "2", "3", "4", "5", "6", "7"}}},
				 new ConditionSpecification[] {
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(0,2),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(2,3),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(3,4),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(4,5),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(5,6),
					 new TrafficSpeedToVehiclesNumberCorrelationConditionSpecification(6,7),
				 }),
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
				 }),
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
				 }),
	};
}
