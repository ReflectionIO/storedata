//
//  SliceHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.util.Calendar;
import java.util.Date;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class SliceHelper {

	// private static final SimpleDateFormat SDF = new SimpleDateFormat(FormattingHelper.DATE_FORMAT_YYYY_MM_DD);
	private static final Calendar c = Calendar.getInstance();

	private static final int DEFAULT_DAYS_PER_SLICE = 30;
	private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

	public static long offset(Date date) {
		return offset(date, DEFAULT_DAYS_PER_SLICE);
	}

	public static long offset(Date date, int sliceDays) {
		return date.getTime() / (MILLIS_PER_DAY * (long) sliceDays);
	}

	public static Date startDate(long slice) {
		return startDate(slice, DEFAULT_DAYS_PER_SLICE);
	}

	public static Date startDate(long slice, int daysPerSlice) {

		c.setTime(new Date(0));
		c.add(Calendar.DAY_OF_YEAR, (int) (slice * daysPerSlice));

		return new Date(c.getTimeInMillis());
	}

	public static Date endDate(long slice) {
		return endDate(slice, DEFAULT_DAYS_PER_SLICE);
	}

	public static Date endDate(long slice, int daysPerSlice) {
		c.setTime(new Date(0));
		c.add(Calendar.DAY_OF_YEAR, (int) ((slice + 1L) * (long) daysPerSlice) - 1);

		return new Date(c.getTimeInMillis());
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
