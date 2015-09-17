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

	private Date fromDate;
	private Date toDate;

	public DateRange() {

	}

	public DateRange(Date from, Date to) {
		this.fromDate = from;
		this.toDate = to;
	}

	public void setFrom(Date value) {
		fromDate = value;
	}

	public void setTo(Date value) {
		toDate = value;
	}

	public Date getFrom() {
		return CalendarUtil.copyDate(fromDate);
	}

	public Date getTo() {
		return CalendarUtil.copyDate(toDate);
	}

	public int getDaysBetween() {
		return Math.abs(CalendarUtil.getDaysBetween(fromDate, toDate));
	}

	public List<Date> getDatesBetween() {
		List<Date> dates = new ArrayList<Date>();
		Date date = CalendarUtil.copyDate(fromDate);
		CalendarUtil.addDaysToDate(date, 1);
		while (date.before(toDate)) {
			dates.add(CalendarUtil.copyDate(date));
			CalendarUtil.addDaysToDate(date, 1);
		}
		return dates;
	}

	public int getDays() {
		int days = 0;
		Date date = CalendarUtil.copyDate(fromDate);
		while (date.before(toDate) || CalendarUtil.isSameDate(date, toDate)) {
			days++;
			CalendarUtil.addDaysToDate(date, 1);
		}
		return days;
	}

	public List<Date> getDates() {
		List<Date> dates = new ArrayList<Date>();
		Date date = CalendarUtil.copyDate(fromDate);
		while (date.before(toDate) || CalendarUtil.isSameDate(date, toDate)) {
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
