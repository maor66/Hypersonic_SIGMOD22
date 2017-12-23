package sase.config;

import sase.user.speedd.fraud.CreditCardFraudEventTypesManager;
import sase.user.speedd.traffic.TrafficEventTypesManager;
import sase.user.stocks.StockEventTypesManager;
import sase.user.trams.TramEventTypesManager;

public class StructureConfig {
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Stocks
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static final String[][][] sequenceOfThree = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] sequenceOfThreeNAEUCA = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] sequenceOfThreeEUASCA = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
				},
			},
		 };
		 
	public static final String[][][] sequenceOfThreeForIteration = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
						StockEventTypesManager.centralAmericanCompanyEventTypeName,
						StockEventTypesManager.africanCompanyEventTypeName,
						StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
		 };
		 
	 public static final String[][][] sequenceOfThreeEUFirst = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
				},
			},
		 };
		 
	 public static final String[][][] sequenceOfThreeEULast = 
				//main 'OR'
				new String[][][]{
					//first 'AND'
					new String[][] {
						//first 'SEQ'
						new String[] {
							StockEventTypesManager.northAmericanCompanyEventTypeName,
							StockEventTypesManager.africanCompanyEventTypeName,
							StockEventTypesManager.europeanCompanyEventTypeName,
						},
					},
				 };

	public static final String[][][] sequenceOfFour = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] sequenceOfFive = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
				},
			},
		 };
		 
	 public static final String[][][] sequenceOfFiveForNegation = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.middleEasternCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.northAmericanCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] sequenceOfSix = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] sequenceOfSeven = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
					StockEventTypesManager.middleEasternCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] sequenceOfEight = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
					StockEventTypesManager.middleEasternCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] conjunctionOfTwo = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
			},
		 };

	public static final String[][][] conjunctionOfThree = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
			},
		 };

	public static final String[][][] conjunctionOfThreeEUSAAFR = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] conjunctionOfThreeCASAAU = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.australianCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] conjunctionOfFour = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName
				},
			},
		 };

	public static final String[][][] conjunctionOfFive = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName
				},
			},
		 };

	public static final String[][][] conjunctionOfSix = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.australianCompanyEventTypeName
				},
			},
		 };

	public static final String[][][] conjunctionOfSeven = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.australianCompanyEventTypeName
				},
				new String[] {
						StockEventTypesManager.middleEasternCompanyEventTypeName
				},
			},
		 };

	public static final String[][][] conjunctionOfEight = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName
				},
				new String[] {
					StockEventTypesManager.australianCompanyEventTypeName
				},
				new String[] {
						StockEventTypesManager.middleEasternCompanyEventTypeName
				},
				new String[] {
						StockEventTypesManager.asianCompanyEventTypeName
				},
			},
		 };
		 
	 public static final String[][][] conjunctionOfTwoSequences = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
		 };

	 public static final String[][][] conjunctionOfFourSequences = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					},
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
				},
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
				},
				new String[] {
						StockEventTypesManager.middleEasternCompanyEventTypeName,
						StockEventTypesManager.asianCompanyEventTypeName,
				},
			},
		 };
		 
	public static final String[][][] disjunctionOfTwoSequencesOfTwo = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
		 };
		 
	public static final String[][][] disjunctionOfTwoSequencesOfTwoNACAEUSA = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
		 };
		 
	public static final String[][][] disjunctionOfTwoSequencesOfTwoEUASCAME = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.middleEasternCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] disjunctionOfTwoSequencesOfThree = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.southAmericanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] disjunctionOfTwoSequencesOfFour = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
					StockEventTypesManager.middleEasternCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
				},
			},
		 };
		 
	public static final String[][][] disjunctionOfThreeSequencesOfTwo = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
				},
			},
		 };

	public static final String[][][] disjunctionOfFourSequencesOfTwo = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.northAmericanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.australianCompanyEventTypeName,
				},
			},
			new String[][] {
				new String[] {
					StockEventTypesManager.middleEasternCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
				},
			},
		 };
		 
	//Equality patterns for SASE comparison
	public static final String[][][] sequenceOfThreeForEqualitySequence = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.europeanCompanyEventTypeName,
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
				},
			},
		 };
	public static final String[][][] sequenceOfFourForEqualityNegation = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.asianCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.europeanCompanyEventTypeName,
				},
			},
		 };
	public static final String[][][] sequenceOfThreeForEqualityIteration = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					StockEventTypesManager.centralAmericanCompanyEventTypeName,
					StockEventTypesManager.africanCompanyEventTypeName,
					StockEventTypesManager.southAmericanCompanyEventTypeName,
				},
			},
		 };
			
			
			
			
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Trams
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final String[][][] sequenceOfThreeCongestions = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					TramEventTypesManager.normalTrafficEventTypeName,
					TramEventTypesManager.lightCongestionEventTypeName,
					TramEventTypesManager.heavyCongestionEventTypeName,
				},
			},
	 	};

	public static final String[][][] sequenceOfFourCongestions = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					TramEventTypesManager.normalTrafficEventTypeName,
					TramEventTypesManager.lightCongestionEventTypeName,
					TramEventTypesManager.mediumCongestionEventTypeName,
					TramEventTypesManager.heavyCongestionEventTypeName,
				},
			},
	 	};

	public static final String[][][] sequenceOfFiveCongestions = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					TramEventTypesManager.normalTrafficEventTypeName,
					TramEventTypesManager.lightCongestionEventTypeName,
					TramEventTypesManager.mediumCongestionEventTypeName,
					TramEventTypesManager.severeCongestionEventTypeName,
					TramEventTypesManager.heavyCongestionEventTypeName,
				},
			},
	 	};
	 	
	 	
	 	
		
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Traffic
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final String[][][] sequenceOfThreeSpeedMeasurements = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					TrafficEventTypesManager.veryLowSpeedEventTypeName,
					TrafficEventTypesManager.lowSpeedEventTypeName,
					TrafficEventTypesManager.veryHighSpeedEventTypeName,
				},
			},
	 	};

	public static final String[][][] sequenceOfFourSpeedMeasurements = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					TrafficEventTypesManager.veryLowSpeedEventTypeName,
					TrafficEventTypesManager.lowSpeedEventTypeName,
					TrafficEventTypesManager.mediumSpeedEventTypeName,
					TrafficEventTypesManager.veryHighSpeedEventTypeName,
				},
			},
	 	};

	public static final String[][][] sequenceOfFiveSpeedMeasurements = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					TrafficEventTypesManager.veryLowSpeedEventTypeName,
					TrafficEventTypesManager.lowSpeedEventTypeName,
					TrafficEventTypesManager.mediumSpeedEventTypeName,
					TrafficEventTypesManager.highSpeedEventTypeName,
					TrafficEventTypesManager.veryHighSpeedEventTypeName,
				},
			},
	 	};

	 	
		
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	Credit Card Fraud
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final String[][][] sequenceOfThreeTransactions = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
					CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
					CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
					CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName,
				},
			},
	 	};

	public static final String[][][] sequenceOfFourTransactions = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
						CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName,
				},
			},
	 	};

	public static final String[][][] sequenceOfFiveTransactions = 
		//main 'OR'
		new String[][][]{
			//first 'AND'
			new String[][] {
				//first 'SEQ'
				new String[] {
						CreditCardFraudEventTypesManager.smallTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.mediumTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.largeTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.veryLargeTransactionNoFraudEventTypeName,
						CreditCardFraudEventTypesManager.verySmallTransactionFraudEventTypeName,
				},
			},
	 	};

}
