//
//  FormattingHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 20 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefanocapuzzi
 * 
 */
public class FormattingHelper {

	private static Map<String, String> currencySymbolLookup = null;

	/**
	 * Returns a currency sumbol or code if none are found
	 * @param currency
	 * @return
	 */
	public static String getCurrencySymbol(String currency) {

		if (currencySymbolLookup == null) {
			setup();
		}

		String symbol = currencySymbolLookup.get(currency);

		return symbol == null ? currency : symbol;
	}

	public static void setup() {
		if (currencySymbolLookup == null) {
			currencySymbolLookup = new HashMap<String, String>();

			currencySymbolLookup.put("USD", "$");
			currencySymbolLookup.put("EUR", "€");
			currencySymbolLookup.put("CNY", "¥");
			currencySymbolLookup.put("GBP", "£");
		}
	}
}
