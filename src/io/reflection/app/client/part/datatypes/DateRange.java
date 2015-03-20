//
//  DateRange.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author billy1380
 * 
 */
public class DateRange {

	private Date mFromDate;
	private Date mToDate;

	public void setFrom(Date value) {
		mFromDate = value;
	}

	public void setTo(Date value) {
		mToDate = value;
	}

	public Date getFrom() {
		return CalendarUtil.copyDate(mFromDate);
	}

	public Date getTo() {
		return CalendarUtil.copyDate(mToDate);
	}

	public int getDaysBetween() {
		return CalendarUtil.getDaysBetween(mFromDate, mToDate);
	}

	public List<Date> getDatesBetween() {
		List<Date> dates = new ArrayList<Date>();
		Date date = CalendarUtil.copyDate(mFromDate);
		CalendarUtil.addDaysToDate(date, 1);
		while (date.before(mToDate)) {
			dates.add(CalendarUtil.copyDate(date));
			CalendarUtil.addDaysToDate(date, 1);
		}
		return dates;
	}

	public int getDays() {
		int days = 0;
		Date date = CalendarUtil.copyDate(mFromDate);
		while (date.before(mToDate) || CalendarUtil.isSameDate(date, mToDate)) {
			days++;
			CalendarUtil.addDaysToDate(date, 1);
		}
		return days;
	}

	public List<Date> getDates() {
		List<Date> dates = new ArrayList<Date>();
		Date date = CalendarUtil.copyDate(mFromDate);
		while (date.before(mToDate) || CalendarUtil.isSameDate(date, mToDate)) {
			dates.add(CalendarUtil.copyDate(date));
			CalendarUtil.addDaysToDate(date, 1);
		}
		return dates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj != null && obj instanceof DateRange) {
			DateRange dateRangeObj = (DateRange) obj;

			return CalendarUtil.isSameDate(this.getFrom(), dateRangeObj.getFrom()) && CalendarUtil.isSameDate(this.getTo(), dateRangeObj.getTo());

		}

		return super.equals(obj);
	}

	public static DateRange copy(DateRange value) {
		DateRange range = null;

		if (value != null) {
			range = new DateRange();
			range.setFrom(value.getFrom());
			range.setTo(value.getTo());
		}

		return range;
	}

}
