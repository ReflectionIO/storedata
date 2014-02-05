//
//  DateRange.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import java.util.Date;

/**
 * @author billy1380
 * 
 */
public class DateRange {

	private Date mFromDate;
	private Date mToDate;

	public Date getFrom() {

		return mFromDate;
	}

	public void setFrom(Date value) {
		mFromDate = value;
	}

	public void setTo(Date value) {
		mToDate = value;
	}

	public Date getTo() {

		return mToDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof DateRange) {
			DateRange dateRangeObj = (DateRange) obj;

			return this.getFrom().equals(dateRangeObj.getFrom()) && this.getTo().equals(dateRangeObj.getTo());
		}

		return super.equals(obj);
	}

}
