//
//  FormattingHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 20 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

/**
 * @author stefanocapuzzi
 * 
 */
public class FormattingHelper {

	public static String getCurrencySymbol(String currency) {
		if (currency.equals("USD")) {
			return "$";
		} else if (currency.equals("EUR")) {
			return "€";
		} else if (currency.equals("CNY")) {
			return "¥";
		} else {
			return "?";
		}
	}
}
