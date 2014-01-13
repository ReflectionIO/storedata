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
		if (mFromDate == null) {
			mFromDate = new Date();
		}

		return mFromDate;
	}

	public void setFrom(Date value) {
		mFromDate = value;
	}

	public void setTo(Date value) {
		mToDate = value;
	}

	public Date getTo() {
		if (mToDate == null) {
			mToDate = new Date();
		}

		return mToDate;
	}

}
