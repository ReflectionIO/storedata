//
//  FilterHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author billy1380
 * 
 */
public class FilterHelper {
	public static void addStores(ListBox list) {
		List<Store> stores = StoreController.get().getStores();

		if (stores != null) {
			for (Store store : stores) {
				list.addItem(store.name, store.a3Code);
			}
		}
	}

	public static void addCountries(ListBox list) {
		List<Country> countries = CountryController.get().getCountries();

		if (countries != null) {
			for (Country country : countries) {
				list.addItem(country.name, country.a2Code);
			}
		}
	}

	public static void addCategories(ListBox list) {}

	/**
	 * Normalize the date to midnight being sure the milliseconds are set at zero.
	 * 
	 * @param date
	 *            Date to be set at noon
	 * @return newDate The new date set at noon
	 */
	@SuppressWarnings("deprecation")
	public static Date normalizeDate(Date date) {
		Date newDate = new Date(0L);
		newDate.setDate(date.getDate());
		newDate.setMonth(date.getMonth());
		newDate.setYear(date.getYear());
		newDate.setHours(0);
		return newDate;
	}

	/**
	 * 
	 */
	public static void disableOutOfRangeDates(DatePicker datePicker, Date startDate, Date endDate) {

	}

	public static void disableFutureDates(DatePicker datePicker) {
		List<Date> dates = new ArrayList<Date>();
		Date firstShownOnCalendar = CalendarUtil.copyDate(datePicker.getFirstDate());
		Date lastShownOnCalendar = CalendarUtil.copyDate(datePicker.getLastDate());
		Date today = normalizeDate(new Date());
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || firstShownOnCalendar.equals(lastShownOnCalendar)) && lastShownOnCalendar.after(today)) {
			if (datePicker.isDateVisible(CalendarUtil.copyDate(lastShownOnCalendar))) {
				dates.add(CalendarUtil.copyDate(lastShownOnCalendar));
			}
			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}
		datePicker.setTransientEnabledOnDates(false, dates);
	}
}
