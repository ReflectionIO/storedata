//
//  FormattingHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 20 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.datatypes.shared.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefanocapuzzi
 * 
 */
public class FormattingHelper {

	public static final String DATE_FORMAT_EEE_DD_MMM_YYYY = "EEE dd MMM yyyy";
	public static final String DATE_FORMAT_DD_MMM_YYYY = "dd MMM yyyy";
	public static final String DATE_FORMAT_DD_MM_YYYY = "dd-MM-yyyy";
	// public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	public static final String DATE_FORMAT_DD_MMM_YYYY_HH_MM = "dd MMM yyyy - HH:mm";
	
	public static final String MONEY_FORMAT = ",###.##";
	public static final String WHOLE_NUMBER_FORMAT = ",###";

	private static Map<String, String> currencySymbolLookup = null;

	private static void setup() {
		if (currencySymbolLookup == null) {
			currencySymbolLookup = new HashMap<String, String>();

			currencySymbolLookup.put("USD", "$");
			currencySymbolLookup.put("EUR", "€");
			currencySymbolLookup.put("CNY", "¥");
			currencySymbolLookup.put("GBP", "£");
		}
	}

	/**
	 * Returns a currency sumbol or code if none are found
	 * 
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

	public static String getUserName(User user) {
		return user == null ? "None" : user.forename + " " + user.surname;
	}

	public static String getUserLongName(User user) {
		String longName = getUserName(user);

		if (user != null && user.company != null & user.company.trim().length() > 0) {
			longName += " (" + user.company.trim() + ")";
		}
		return longName;
	}

	public static String getCompanyName(User user) {
		String result = "";
		if (user != null && user.company != null && user.company.trim().length() > 0) {
			result = user.company.trim();
		}
		return result;
	}

	public static String getTimeSince(Date date) {
		return date == null ? "" : getTimeSince(date, true);
	}

	public static String getTimeSince(Date date, boolean blur) {
		String timeSince;

		long time = date.getTime();
		long timeNow = (new Date()).getTime();

		double seconds = Math.floor((double) (timeNow - time) / 1000.0);

		double interval;

		if ((interval = Math.floor(seconds / 31536000.0)) > 1) {
			timeSince = interval + " years ago";
		} else if ((interval = Math.floor(seconds / 2592000.0)) > 1) {
			timeSince = interval + " months ago";
		} else if ((interval = Math.floor(seconds / 86400.0)) > 1) {
			if (blur) {
				if (interval < 2) {
					timeSince = "yesterday";
				} else if (interval > 7 && interval < 14) {
					timeSince = "last week";
				} else {
					timeSince = interval + " days ago";
				}
			} else {
				timeSince = interval + " days ago";
			}
		} else if ((interval = Math.floor(seconds / 3600.0)) > 1) {
			if (blur) {
				if (interval > 12) {
					timeSince = "earlier today";
				} else {
					timeSince = interval + " hours ago";
				}
			} else {
				timeSince = interval + " hours ago";
			}
		} else if ((interval = Math.floor(seconds / 60.0)) > 1) {
			timeSince = interval + " minutes ago";
		} else {
			if (blur) {
				timeSince = "less than a minute ago";
			} else {
				timeSince = Math.floor(seconds) + " seconds ago";
			}
		}

		return timeSince;
	}
}
