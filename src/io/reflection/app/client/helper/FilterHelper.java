//
//  FilterHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.Forum;
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

	public static final int TODAY_PARAM = 1;
	public static final int ONE_DAY_AGO_PARAM = 2;
	public static final int ONE_WEEK_AGO_PARAM = 3;
	public static final int ONE_MONTH_AGO_PARAM = 4;
	public static final int THREE_MONTHS_AGO_PARAM = 5;
	public static final int SIX_MONTHS_AGO_PARAM = 6;
	public static final int ONE_YEAR_AGO_PARAM = 7;
	public static final int TWO_WEEKS_AGO_PARAM = 8;
	public static final int SIXTY_DAYS_AGO_PARAM = 9;
	public static final int FOUR_WEEKS_AGO_PARAM = 10;
	public static final int SIX_WEEKS_AGO_PARAM = 11;
	public static final int EIGHT_WEEKS_AGO_PARAM = 12;

	/**
	 * Get a common used date, setting today as default
	 * 
	 * @param defaultDateParam
	 * @return
	 */
	public static Date getFixedDate(int defaultDateParam) {
		Date date = normalizeDate(new Date()); // Today - default
		switch (defaultDateParam) {
		case TODAY_PARAM:
			break;
		case ONE_DAY_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -1); // 1 Day ago
			break;
		case ONE_WEEK_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -7); // 1 Week ago
			break;
		case ONE_MONTH_AGO_PARAM:
			CalendarUtil.addMonthsToDate(date, -1); // 1 Month ago
			break;
		case THREE_MONTHS_AGO_PARAM:
			CalendarUtil.addMonthsToDate(date, -3); // 3 Months ago
			break;
		case SIX_MONTHS_AGO_PARAM:
			CalendarUtil.addMonthsToDate(date, -6); // 6 Months ago
			break;
		case ONE_YEAR_AGO_PARAM:
			CalendarUtil.addMonthsToDate(date, -12); // 1 Year ago
			break;
		case TWO_WEEKS_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -14); // 2 Weeks ago
			break;
		case SIXTY_DAYS_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -60); // 60 Days ago
			break;
		case FOUR_WEEKS_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -28); // 4 Weeks ago
			break;
		case SIX_WEEKS_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -42); // 6 Weeks ago
			break;
		case EIGHT_WEEKS_AGO_PARAM:
			CalendarUtil.addDaysToDate(date, -56); // 8 Weeks ago
			break;
		default:
			break;
		}
		return date;
	}

	public static void addLinkedAccounts(ListBox list) {
		List<DataAccount> linkedAccounts = LinkedAccountController.get().getAllLinkedAccounts();

		if (linkedAccounts != null) {
			for (DataAccount linkedAccount : linkedAccounts) {
				list.addItem(linkedAccount.username, linkedAccount.id.toString());
			}
		}
	}

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

	public static void addForums(ListBox list) {
		List<Forum> forums = ForumController.get().getForums();

		if (forums != null) {
			for (Forum forum : forums) {
				list.addItem(forum.title, forum.id.toString());
			}
		}
	}

	/**
	 * Normalize the date to midnight being sure the milliseconds are set at zero.
	 * 
	 * @param date
	 *            Date to be set at noon
	 * @return newDate The new date set at noon
	 */
	@SuppressWarnings("deprecation")
	public static Date normalizeDate(Date date) {
		Date newDate = null;

		if (date != null) {
			newDate = new Date(0L);
			newDate.setDate(date.getDate());
			newDate.setMonth(date.getMonth());
			newDate.setYear(date.getYear());
			newDate.setHours(0);
		}

		return newDate;
	}

	/**
	 * Disable dates between startDate and endDate
	 * 
	 * @param datePicker
	 * @param startDate
	 * @param endDate
	 */
	public static void disableOutOfRangeDates(DatePicker datePicker, Date startDate, Date endDate) {

		// Enable all dates and reset all highlighted dates
		List<Date> dates = new ArrayList<Date>();
		Date firstShownOnCalendar = CalendarUtil.copyDate(datePicker.getFirstDate());
		Date lastShownOnCalendar = CalendarUtil.copyDate(datePicker.getLastDate());
		while (firstShownOnCalendar.before(lastShownOnCalendar) || CalendarUtil.isSameDate(firstShownOnCalendar, lastShownOnCalendar)) {
			dates.add(CalendarUtil.copyDate(firstShownOnCalendar));
			CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
		}
		datePicker.setTransientEnabledOnDates(true, dates);
		dates.clear();
		firstShownOnCalendar = CalendarUtil.copyDate(datePicker.getFirstDate());

		// Disable dates before start date
		if (startDate != null) {
			while ((firstShownOnCalendar.before(lastShownOnCalendar) || CalendarUtil.isSameDate(firstShownOnCalendar, lastShownOnCalendar))
					&& firstShownOnCalendar.before(startDate)) {
				dates.add(CalendarUtil.copyDate(firstShownOnCalendar));
				CalendarUtil.addDaysToDate(firstShownOnCalendar, 1);
			}
			datePicker.setTransientEnabledOnDates(false, dates);
			dates.clear();
			firstShownOnCalendar = CalendarUtil.copyDate(datePicker.getFirstDate());
		}

		// Disable dates after end date
		if (endDate != null) {
			firstShownOnCalendar = CalendarUtil.copyDate(datePicker.getFirstDate());
			while ((lastShownOnCalendar.after(firstShownOnCalendar) || CalendarUtil.isSameDate(firstShownOnCalendar, lastShownOnCalendar))
					&& lastShownOnCalendar.after(endDate)) {
				dates.add(CalendarUtil.copyDate(lastShownOnCalendar));
				CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
			}
			datePicker.setTransientEnabledOnDates(false, dates);
			dates.clear();
			lastShownOnCalendar = CalendarUtil.copyDate(datePicker.getLastDate());
		}

	}

	/**
	 * 
	 * @param datePicker
	 */
	public static void disableFutureDates(DatePicker datePicker) {

		List<Date> dates = new ArrayList<Date>();
		Date firstShownOnCalendar = CalendarUtil.copyDate(datePicker.getFirstDate());
		Date lastShownOnCalendar = CalendarUtil.copyDate(datePicker.getLastDate());
		Date today = normalizeDate(new Date());
		while ((lastShownOnCalendar.after(firstShownOnCalendar) || CalendarUtil.isSameDate(firstShownOnCalendar, lastShownOnCalendar))
				&& lastShownOnCalendar.after(today)) {
			if (datePicker.isDateVisible(CalendarUtil.copyDate(lastShownOnCalendar))) {
				dates.add(CalendarUtil.copyDate(lastShownOnCalendar));
			}
			CalendarUtil.addDaysToDate(lastShownOnCalendar, -1);
		}
		datePicker.setTransientEnabledOnDates(false, dates);

	}

	public static DateRange createRangeFromToday(int daysApart) {
		DateRange dateRange = new DateRange();
		Date from, to;

		if (daysApart > 0) {
			dateRange.setFrom(from = getFixedDate(TODAY_PARAM));
			to = CalendarUtil.copyDate(from);
			CalendarUtil.addDaysToDate(to, daysApart);
			dateRange.setTo(to);
		} else {
			dateRange.setTo(to = getFixedDate(TODAY_PARAM));
			from = CalendarUtil.copyDate(to);
			CalendarUtil.addDaysToDate(from, daysApart);
			dateRange.setFrom(from);
		}

		return dateRange;
	}

	public static DateRange createRange(Date from, Date to) {
		DateRange dateRange = new DateRange();

		if (from == null) {
			from = getFixedDate(TODAY_PARAM);
		}

		if (to == null) {
			to = getFixedDate(TODAY_PARAM);
		}

		dateRange.setFrom(from);
		dateRange.setTo(to);

		return dateRange;
	}

}
