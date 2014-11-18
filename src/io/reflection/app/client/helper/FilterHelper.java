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
import io.reflection.app.client.part.DateSelector.PresetDateRange;
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

	private static List<PresetDateRange> defaultPreset = null;
	private static List<PresetDateRange> adminPreset = null;

	public static Date getToday() {
		return new Date();
	}

	public static Date getDaysAgo(int value) {
		Date date = getToday();
		CalendarUtil.addDaysToDate(date, value * -1);
		return date;
	}

	public static Date getWeeksAgo(int value) {
		return getDaysAgo(value * 7);
	}

	public static Date getMonthsAgo(int value) {
		Date date = getToday();
		CalendarUtil.addMonthsToDate(date, value * -1);
		return date;
	}

	public static Date getYearsAgo(int value) {
		return getMonthsAgo(value * 12);
	}

	public static void addLinkedAccounts(ListBox list) {
		List<DataAccount> linkedAccounts = LinkedAccountController.get().getAllLinkedAccounts();

		if (linkedAccounts != null) {
			for (DataAccount linkedAccount : linkedAccounts) {
				list.addItem(linkedAccount.username, linkedAccount.id.toString());
			}
		}
	}

	/**
	 * Add list of stores to ListBox
	 * 
	 * @param list
	 *            , ListBox
	 * @param isAdmin
	 *            , if false add only iPhone store
	 */
	public static void addStores(ListBox list, boolean isAdmin) {
		if (isAdmin) {
			List<Store> stores = StoreController.get().getStores();

			if (stores != null) {
				for (Store store : stores) {
					list.addItem(store.name, store.a3Code);
				}
			}
		} else {
			list.addItem("iPhone Store", "iph");
			list.setEnabled(false);
		}
	}

	public static void addStores(ListBox list) {
		addStores(list, false);
	}

	/**
	 * Add list of countries to ListBox
	 * 
	 * @param list
	 *            , ListBox
	 * @param isAdmin
	 *            , if false add only USA
	 */
	public static void addCountries(ListBox list, boolean isAdmin) {
		if (isAdmin) {
			List<Country> countries = CountryController.get().getCountries();

			if (countries != null) {
				for (Country country : countries) {
					list.addItem(country.name, country.a2Code);
				}
			}
		} else {
			Country usCountry = CountryController.get().getCountry("us");
			list.addItem(usCountry.name, usCountry.a2Code);
			list.setEnabled(false);
		}
	}

	public static void addCountries(ListBox list) {
		addCountries(list, false);
	}

	/**
	 * Add list of categories to ListBox
	 * 
	 * @param list
	 *            , ListBox
	 * @param isAdmin
	 *            , if false add only All categories
	 */
	public static void addCategories(ListBox list, boolean isAdmin) {
		if (isAdmin) {
			list.addItem("All", "24");
			list.addItem("Book", "19");
			list.addItem("Business", "1");
			list.addItem("Catalogs", "22");
			list.addItem("Education", "18");
			list.addItem("Entertainment", "17");
			list.addItem("Finance", "16");
			list.addItem("Food & Drink", "23");
			list.addItem("Games", "15");
			list.addItem("Health & Fitness", "14");
			list.addItem("Lifestyle", "13");
			list.addItem("Medical", "20");
			list.addItem("Music", "12");
			list.addItem("Navigation", "11");
			list.addItem("News", "10");
			list.addItem("Newsstand", "21");
			list.addItem("Photo & Video", "9");
			list.addItem("Productivity", "8");
			list.addItem("Reference", "7");
			list.addItem("Social Networking", "6");
			list.addItem("Sports", "5");
			list.addItem("Travel", "4");
			list.addItem("Utilities", "3");
			list.addItem("Weather", "2");
		} else {
			list.addItem("All", "24");
			list.setEnabled(false);
		}
	}

	public static void addCategories(ListBox list) {
		addCategories(list, false);
	}

	public static void addForums(ListBox list) {
		List<Forum> forums = ForumController.get().getForums();

		if (forums != null) {
			for (Forum forum : forums) {
				list.addItem(forum.title, forum.id.toString());
			}
		}
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
		Date today = new Date();
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
			dateRange.setFrom(from = getToday());
			to = CalendarUtil.copyDate(from);
			CalendarUtil.addDaysToDate(to, daysApart);
			dateRange.setTo(to);
		} else {
			dateRange.setTo(to = getToday());
			from = CalendarUtil.copyDate(to);
			CalendarUtil.addDaysToDate(from, daysApart);
			dateRange.setFrom(from);
		}

		return dateRange;
	}

	public static DateRange createRange(Date from, Date to) {
		DateRange dateRange = new DateRange();

		if (from == null) {
			from = getToday();
		}

		if (to == null) {
			to = getToday();
		}

		dateRange.setFrom(from);
		dateRange.setTo(to);

		return dateRange;
	}

	public static List<PresetDateRange> getDefaultDateRanges() {

		if (defaultPreset == null) {
			defaultPreset = new ArrayList<PresetDateRange>();

			defaultPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "1 wk";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(1), FilterHelper.getToday());
				}
			});

			defaultPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "2 wks";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(2), FilterHelper.getToday());
				}
			});

			defaultPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "4 wks";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(4), FilterHelper.getToday());
				}
			});

			defaultPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "6 wks";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(6), FilterHelper.getToday());
				}
			});

			defaultPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "8 wks";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(8), FilterHelper.getToday());
				}
			});
		}

		return defaultPreset;

	}

	public static List<PresetDateRange> getAdminDateRanges() {
		if (adminPreset == null) {
			adminPreset = new ArrayList<PresetDateRange>();

			adminPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "1 day";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getToday(), FilterHelper.getToday());
				}
			});

			adminPreset.add(new PresetDateRange() {

				@Override
				public String getName() {

					return "1 wk";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(1), FilterHelper.getToday());
				}
			});

			adminPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "2 wks";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getWeeksAgo(2), FilterHelper.getToday());
				}
			});

			adminPreset.add(new PresetDateRange() {

				@Override
				public String getName() {
					return "30 days";
				}

				@Override
				public DateRange getDateRange() {
					return FilterHelper.createRange(FilterHelper.getDaysAgo(30), FilterHelper.getToday());
				}
			});
		}

		return adminPreset;

	}

	/**
	 * @param value
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean equalDate(Date rhs, Date lhs) {
		return (lhs == rhs)
				|| (rhs != null && lhs != null && (rhs.getDate() == lhs.getDate()) && (rhs.getMonth() == lhs.getMonth()) && (rhs.getYear() == lhs.getYear()));
	}

	// TODO before / after method at day level

}
