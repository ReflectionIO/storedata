//
//  FormattingHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * @author William Shakour (billy1380)
 *
 */
public class FormattingHelper {

	private static NumberFormat MONEY_FORMAT = NumberFormat.getFormat(io.reflection.app.shared.util.FormattingHelper.MONEY_FORMAT);
	public static NumberFormat WHOLE_NUMBER_FORMAT = NumberFormat.getFormat(io.reflection.app.shared.util.FormattingHelper.WHOLE_NUMBER_FORMAT);

	public static DateTimeFormat DATE_FORMAT_EEE_DD_MMM_YYYY = DateTimeFormat
			.getFormat(io.reflection.app.shared.util.FormattingHelper.DATE_FORMAT_EEE_DD_MMM_YYYY);

	public static DateTimeFormat DATE_FORMAT_DD_MMM_YYYY = DateTimeFormat.getFormat(io.reflection.app.shared.util.FormattingHelper.DATE_FORMAT_DD_MMM_YYYY);

	public static DateTimeFormat DATE_FORMAT_DD_MMM_YYYY_HH_MM = DateTimeFormat
			.getFormat(io.reflection.app.shared.util.FormattingHelper.DATE_FORMAT_DD_MMM_YYYY_HH_MM);

	public static String asPriceString(String currency, float price) {
		String priceString;

		if (DataTypeHelper.isZero(price)) {
			priceString = "Free";
		} else {
			priceString = asMoneyString(currency, price);
		}

		return priceString;
	}

	public static String asMoneyString(String currency, float money) {
		return (currency == null ? "" : io.reflection.app.shared.util.FormattingHelper.getCurrencySymbol(currency) + " ") + MONEY_FORMAT.format((double) money);
	}

	public static String asWholeMoneyString(String currency, float money) {
		return (currency == null ? "" : io.reflection.app.shared.util.FormattingHelper.getCurrencySymbol(currency) + " ")
				+ WHOLE_NUMBER_FORMAT.format((double) money);
	}

	public static String asPriceRangeString(String currency, float from, float to) {
		String priceRangeString;

		if (DataTypeHelper.isZero(Math.abs(from - to))) {
			priceRangeString = asPriceString(currency, from); // No need to use a price range
		} else {
			String fromString = asPriceString(currency, from), toString = asPriceString(currency, to);
			priceRangeString = fromString + " - " + toString;
		}

		return priceRangeString;
	}
}
