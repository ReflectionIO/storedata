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
		if ("USD".equals(currency)) {
			return "$";
		} else if ("EUR".equals(currency)) {
			return "€";
		} else if ("CNY".equals(currency)) {
			return "¥";
		} else {
			return currency;
		}
	}
}
