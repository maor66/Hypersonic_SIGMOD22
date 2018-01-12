package config;

import user.speedd.fraud.CreditCardFraudEventTypesManager;
import user.speedd.traffic.TrafficEventTypesManager;
import user.stocks.StockEventTypesManager;
import user.trams.TramEventTypesManager;

public class EvaluationOrderConfig {
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Stocks
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static final String[] orderOfTwo = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfThree = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};
	
	public static final String[] orderOfThreeNAEUCA = new String[] {
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};
	
	public static final String[] orderOfThreeEUASCA = new String[] {
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.asianCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
	};

	public static final String[] orderOfThreeEUSAAFR = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
	};
	
	public static final String[] orderOfThreeCASAAU = new String[] {
		StockEventTypesManager.australianCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfThreeForIteration = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfFour = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfFourNACAEUSA = new String[] {
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfFourEUASCAME = new String[] {
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.middleEasternCompanyEventTypeName,
		StockEventTypesManager.asianCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
	};

	public static final String[] orderOfFive = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfFiveForNegation = new String[] {
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.middleEasternCompanyEventTypeName,
		StockEventTypesManager.asianCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfSix = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.middleEasternCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfSeven = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.australianCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.middleEasternCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};

	public static final String[] orderOfEight = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.australianCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.middleEasternCompanyEventTypeName,
		StockEventTypesManager.asianCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
		StockEventTypesManager.northAmericanCompanyEventTypeName,
	};
	
	//Equality patterns for comparison
	public static final String[] orderOfThreeForEqualitySequence = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
	};
	public static final String[] orderOfFourForEqualityNegation = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
		StockEventTypesManager.asianCompanyEventTypeName,
		StockEventTypesManager.europeanCompanyEventTypeName,
	};
	public static final String[] orderOfThreeForEqualityIteration = new String[] {
		StockEventTypesManager.africanCompanyEventTypeName,
		StockEventTypesManager.southAmericanCompanyEventTypeName,
		StockEventTypesManager.centralAmericanCompanyEventTypeName,
	};
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Trams
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static final String[] orderOfThreeCongestions = new String[] {
		TramEventTypesManager.heavyCongestionEventTypeName,
		TramEventTypesManager.lightCongestionEventTypeName,
		TramEventTypesManager.normalTrafficEventTypeName,
	};
	
	public static final String[] orderOfFourCongestions = new String[] {
		TramEventTypesManager.heavyCongestionEventTypeName,
		TramEventTypesManager.mediumCongestionEventTypeName,
		TramEventTypesManager.lightCongestionEventTypeName,
		TramEventTypesManager.normalTrafficEventTypeName,
	};
	
	public static final String[] orderOfFiveCongestions = new String[] {
		TramEventTypesManager.heavyCongestionEventTypeName,
		TramEventTypesManager.severeCongestionEventTypeName,
		TramEventTypesManager.mediumCongestionEventTypeName,
		TramEventTypesManager.lightCongestionEventTypeName,
		TramEventTypesManager.normalTrafficEventTypeName,
	};
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Traffic Speed
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static final String[] orderOfThreeSpeedMeasurements = new String[] {
		TrafficEventTypesManager.veryHighSpeedEventTypeName,
		TrafficEventTypesManager.lowSpeedEventTypeName,
		TrafficEventTypesManager.veryLowSpeedEventTypeName,
	};
	
	public static final String[] orderOfFourSpeedMeasurements = new String[] {
		TrafficEventTypesManager.veryHighSpeedEventTypeName,
		TrafficEventTypesManager.lowSpeedEventTypeName,
		TrafficEventTypesManager.mediumSpeedEventTypeName,
		TrafficEventTypesManager.veryLowSpeedEventTypeName,
	};
	
	public static final String[] orderOfFiveSpeedMeasurements = new String[] {
		TrafficEventTypesManager.veryHighSpeedEventTypeName,
		TrafficEventTypesManager.lowSpeedEventTypeName,
		TrafficEventTypesManager.mediumSpeedEventTypeName,
		TrafficEventTypesManager.highSpeedEventTypeName,
		TrafficEventTypesManager.veryLowSpeedEventTypeName,
	};

	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Credit Card Fraud
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static final String[] orderOfThreeTransactions = new String[] {
		CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName,
		CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
		CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
	};
	
	public static final String[] orderOfFourTransactions = new String[] {
		CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName,
		CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
		CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName,
		CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
	};
	
	public static final String[] orderOfFiveTransactions = new String[] {
		CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName,
		CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
		CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName,
		CreditCardFraudEventTypesManager.largeTransactionNoFraudEventTypeName,
		CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
	};
}
