package config;

import specification.ConditionSpecification;
import user.speedd.fraud.CreditCardFraudEventTypesManager;
import user.speedd.fraud.SameCreditCardIDConditionSpecification;
import user.speedd.traffic.TrafficEventTypesManager;
import user.speedd.traffic.TrafficSameVehicleIDConditionSpecification;
import user.stocks.StockEventTypesManager;
import user.stocks.condition.StockFirstValueCmpCondition.ComparisonOperation;
import user.stocks.specification.IterativeAverageStockCorrelationConditionSpecification;
import user.stocks.specification.IterativeValueCmpStockCorrelationConditionSpecification;
import user.stocks.specification.StockCorrelationConditionSpecification;
import user.stocks.specification.StockFirstValueCmpConditionSpecification;
import user.trams.TramCongestionIntersectionConditionSpecification;
import user.trams.TramEventTypesManager;

public class ConditionConfig {
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Stocks
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final ConditionSpecification[] sequenceOfTwo = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] sequenceOfThree = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] sequenceOfThreeNAEUCA = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] sequenceOfThreeEUASCA = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] sequenceOfThreeEUSAAFR = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] sequenceOfThreeCASAAU = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] sequenceOfThreeEULast = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9) };

	public static final ConditionSpecification[] sequenceOfFourEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfFiveEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfFiveNegativeEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.northAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfSixEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfSevenEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfEightEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9)};
	
	public static final ConditionSpecification[] sequenceOfEightNegativeEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfFourLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfFiveLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9)};
	
	public static final ConditionSpecification[] sequenceOfFiveNegativeLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9)};


	public static final ConditionSpecification[] sequenceOfSixLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfSevenLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfEightLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] sequenceOfEightNegativeLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9)};
	
	public static final ConditionSpecification[] conjunctionOfThree = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.northAmericanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] conjunctionOfThreeEUSAAFR = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] conjunctionOfThreeCASAAU = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9) };
	
	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfTwo = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9)};
	
	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfTwoNACAEUSA = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9)};
	
	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfTwoEUASCAME = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] disjunctionOfThreeSequencesOfTwo = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] disjunctionOfFourSequencesOfTwo = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfThreeEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfFourEager = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.australianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfThreeLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9)};

	public static final ConditionSpecification[] disjunctionOfTwoSequencesOfFourLazy = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.asianCompanyEventTypeName,
					   StockEventTypesManager.middleEasternCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.middleEasternCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.australianCompanyEventTypeName,
					   0.9)};
	
	public static final ConditionSpecification[] sequenceOfThreeWithMiddleIteration = new ConditionSpecification[] {
			new IterativeAverageStockCorrelationConditionSpecification(
															StockEventTypesManager.centralAmericanCompanyEventTypeName,
															StockEventTypesManager.africanCompanyEventTypeName,
															StockEventTypesManager.southAmericanCompanyEventTypeName,
															0.9),};
	

	//Equality patterns for SASE comparison
	public static final ConditionSpecification[] sequenceOfThreeEquality = new ConditionSpecification[] {
			new StockFirstValueCmpConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName, ComparisonOperation.BIGGER),
			new StockFirstValueCmpConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName, ComparisonOperation.BIGGER)};
	public static final ConditionSpecification[] sequenceOfFourNegativeEqualityLazy = new ConditionSpecification[] {
			new StockFirstValueCmpConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.asianCompanyEventTypeName, ComparisonOperation.BIGGER),
			new StockFirstValueCmpConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName, ComparisonOperation.BIGGER),
			new StockFirstValueCmpConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName, ComparisonOperation.SMALLER)};
	public static final ConditionSpecification[] sequenceOfThreeEqualityIteration = new ConditionSpecification[] {
			new IterativeValueCmpStockCorrelationConditionSpecification(
															StockEventTypesManager.centralAmericanCompanyEventTypeName,
															StockEventTypesManager.africanCompanyEventTypeName,
															StockEventTypesManager.southAmericanCompanyEventTypeName)};
	
	
	////////////////////////////////////////Test configuration/////////////////////////////////////
	public static final ConditionSpecification[] testSequenceOfFive = new ConditionSpecification[] {
			new StockCorrelationConditionSpecification(StockEventTypesManager.northAmericanCompanyEventTypeName,
					   StockEventTypesManager.europeanCompanyEventTypeName,
					   0.9, 1.0),
			new StockCorrelationConditionSpecification(StockEventTypesManager.europeanCompanyEventTypeName,
					   StockEventTypesManager.africanCompanyEventTypeName,
					   0.9, 1.0),
			new StockCorrelationConditionSpecification(StockEventTypesManager.africanCompanyEventTypeName,
					   StockEventTypesManager.southAmericanCompanyEventTypeName,
					   0.9, 1.0),
			new StockCorrelationConditionSpecification(StockEventTypesManager.southAmericanCompanyEventTypeName,
					   StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   0.9, 1.0),
			new StockCorrelationConditionSpecification(StockEventTypesManager.centralAmericanCompanyEventTypeName,
					   StockEventTypesManager.northAmericanCompanyEventTypeName,
					   0.9, 1.0),};
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Trams
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final ConditionSpecification[] sequenceOfThreeCongestions = new ConditionSpecification[] {
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.normalTrafficEventTypeName, TramEventTypesManager.lightCongestionEventTypeName),
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.lightCongestionEventTypeName, TramEventTypesManager.heavyCongestionEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFourCongestions = new ConditionSpecification[] {
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.normalTrafficEventTypeName, TramEventTypesManager.lightCongestionEventTypeName),
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.lightCongestionEventTypeName, TramEventTypesManager.mediumCongestionEventTypeName),
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.mediumCongestionEventTypeName, TramEventTypesManager.heavyCongestionEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFiveCongestions = new ConditionSpecification[] {
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.normalTrafficEventTypeName, TramEventTypesManager.lightCongestionEventTypeName),
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.lightCongestionEventTypeName, TramEventTypesManager.mediumCongestionEventTypeName),
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.mediumCongestionEventTypeName, TramEventTypesManager.severeCongestionEventTypeName),
		new TramCongestionIntersectionConditionSpecification(
			TramEventTypesManager.severeCongestionEventTypeName, TramEventTypesManager.heavyCongestionEventTypeName)};
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Traffic Speed
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final ConditionSpecification[] sequenceOfThreeSpeedMeasurementsEager = new ConditionSpecification[] {
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.veryLowSpeedEventTypeName, TrafficEventTypesManager.lowSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.veryHighSpeedEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFourSpeedMeasurementsEager = new ConditionSpecification[] {
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.veryLowSpeedEventTypeName, TrafficEventTypesManager.lowSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.mediumSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.mediumSpeedEventTypeName, TrafficEventTypesManager.veryHighSpeedEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFiveSpeedMeasurementsEager = new ConditionSpecification[] {
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.veryLowSpeedEventTypeName, TrafficEventTypesManager.lowSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.mediumSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.mediumSpeedEventTypeName, TrafficEventTypesManager.highSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.highSpeedEventTypeName, TrafficEventTypesManager.veryHighSpeedEventTypeName)};

	public static final ConditionSpecification[] sequenceOfThreeSpeedMeasurementsLazy = new ConditionSpecification[] {
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.veryLowSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.veryHighSpeedEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFourSpeedMeasurementsLazy = new ConditionSpecification[] {
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.veryLowSpeedEventTypeName, TrafficEventTypesManager.mediumSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.mediumSpeedEventTypeName, TrafficEventTypesManager.lowSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.veryHighSpeedEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFiveSpeedMeasurementsLazy = new ConditionSpecification[] {
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.veryLowSpeedEventTypeName, TrafficEventTypesManager.highSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.highSpeedEventTypeName, TrafficEventTypesManager.mediumSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.mediumSpeedEventTypeName, TrafficEventTypesManager.lowSpeedEventTypeName),
		new TrafficSameVehicleIDConditionSpecification(
			TrafficEventTypesManager.lowSpeedEventTypeName, TrafficEventTypesManager.veryHighSpeedEventTypeName)};
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Credit Card Fraud
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final ConditionSpecification[] sequenceOfThreeTransactionsEager = new ConditionSpecification[] {
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
				CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFourTransactionsEager = new ConditionSpecification[] {
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFiveTransactionsEager = new ConditionSpecification[] {
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.largeTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.largeTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName)};

	public static final ConditionSpecification[] sequenceOfThreeTransactionsLazy = new ConditionSpecification[] {
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName, 
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFourTransactionsLazy = new ConditionSpecification[] {
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName, 
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName)};

	public static final ConditionSpecification[] sequenceOfFiveTransactionsLazy = new ConditionSpecification[] {
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName, 
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.largeTransactionNoFraudEventTypeName),
		new SameCreditCardIDConditionSpecification(
				CreditCardFraudEventTypesManager.largeTransactionNoFraudEventTypeName, 
				CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName)};
	
}
