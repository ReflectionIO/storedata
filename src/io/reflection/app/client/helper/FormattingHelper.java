//
//  FormattingHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.datatypes.shared.EventPriorityType;
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
public class FormattingHelper extends io.reflection.app.shared.util.FormattingHelper {

	public static NumberFormat TWO_DECIMALS_FORMATTER = NumberFormat.getFormat(TWO_DECIMALS_FORMAT);
	public static NumberFormat WHOLE_NUMBER_FORMATTER = NumberFormat.getFormat(WHOLE_NUMBER_FORMAT);

	public static DateTimeFormat DATE_FORMATTER_EEE_DD_MMM_YYYY = DateTimeFormat.getFormat(DATE_FORMAT_EEE_DD_MMM_YYYY);

	public static DateTimeFormat DATE_FORMATTER_EEE_DD_MM_YY = DateTimeFormat.getFormat(DATE_FORMAT_EEE_DD_MM_YY);

	public static DateTimeFormat DATE_FORMATTER_DD_MMM_YYYY = DateTimeFormat.getFormat(DATE_FORMAT_DD_MMM_YYYY);

	public static DateTimeFormat DATE_FORMATTER_DD_MMM_YYYY_HH_MM = DateTimeFormat.getFormat(DATE_FORMAT_DD_MMM_YYYY_HH_MM);

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
		return (currency == null ? "" : getCurrencySymbol(currency)) + TWO_DECIMALS_FORMATTER.format((double) money);
	}

	public static String asWholeMoneyString(String currency, float money) {
		return (currency == null ? "" : getCurrencySymbol(currency)) + WHOLE_NUMBER_FORMATTER.format((double) money);
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

	public static SafeHtml convertEventPriorityToIcon(EventPriorityType priority) {
		SafeHtml icon = SafeHtmlUtils.EMPTY_SAFE_HTML;

		switch (priority) {
		case EventPriorityTypeCritical:
			icon = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-exclamation-sign\" style=\"color:#ff496a\"></i>");
			break;
		case EventPriorityTypeDebug:
			icon = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-minus\" style=\"color:#eee\"></i>");
			break;
		case EventPriorityTypeHigh:
			icon = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-bell\" style=\"color:#f8c765\"></i>");
			break;
		case EventPriorityTypeLow:
			icon = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-arrow-down\" style=\"color:#ddd\"></i>");
			break;
		case EventPriorityTypeNormal:
			icon = SafeHtmlUtils.fromSafeConstant("<i class=\"glyphicon glyphicon-bell\" style=\"color:#ccc\"></i>");
			break;
		}

		return icon;
	}
}
