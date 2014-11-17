//
//  SliceHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.Date;

import org.joda.time.DateTime;

import org.joda.time.DateTimeZone;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class SliceHelper {

	// private static final SimpleDateFormat SDF = new SimpleDateFormat(FormattingHelper.DATE_FORMAT_YYYY_MM_DD);

	private static final int DEFAULT_DAYS_PER_SLICE = 30;

	public static long offset(Date date) {
		return offset(date, DEFAULT_DAYS_PER_SLICE);
	}

	public static long offset(Date date, int daysPerSlice) {
		return date.getTime() / (ApiHelper.MILLIS_PER_DAY * (long) daysPerSlice);
	}

	/**
	 * This method is a really bad idea because some days are not 24 hours long because of time changes with seasons
	 * 
	 * @param date
	 * @param slicesPerDay
	 * @return
	 */
	public static long subDayOffset(Date date, int slicesPerDay) {
		return date.getTime() / (ApiHelper.MILLIS_PER_DAY / (long) slicesPerDay);
	}

	public static Date startDate(long slice) {
		return startDate(slice, DEFAULT_DAYS_PER_SLICE);
	}

	public static Date startDate(long slice, int daysPerSlice) {
		return (new DateTime(0L, DateTimeZone.UTC)).plusDays((int) (slice * daysPerSlice)).toDate();
	}

	public static Date endDate(long slice) {
		return endDate(slice, DEFAULT_DAYS_PER_SLICE);
	}

	public static Date endDate(long slice, int daysPerSlice) {
		return (new DateTime(0L, DateTimeZone.UTC)).plusDays((int) ((slice + 1L) * (long) daysPerSlice) - 1).toDate();
	}

	public static long[] offsets(Date startDate, Date endDate) {
		return offsets(startDate, endDate, DEFAULT_DAYS_PER_SLICE);
	}

	public static long[] offsets(Date startDate, Date endDate, int daysPerSlice) {
		long[] dateRangeSlices = null;

		long startDateSlice = offset(startDate, daysPerSlice);
		long endDateSlice = offset(endDate, daysPerSlice);

		int diff = ((int) (endDateSlice - startDateSlice)) + 1;
		dateRangeSlices = new long[diff];

		for (int i = 0; i < diff; i++) {
			dateRangeSlices[i] = (long) i + startDateSlice;
		}

		return dateRangeSlices;
	}
}
