//
//  SqlQueryHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 *
 */
public class SqlQueryHelper {
	private transient static final Logger LOG = Logger.getLogger(SqlQueryHelper.class.getName());

	/**
	 *
	 * @param before
	 * @param after
	 * @param dateName
	 * @return
	 */
	public static String beforeAfterQuery(Date before, Date after, String dateName) {
		StringBuffer buffer = new StringBuffer();
		if (before != null && after != null) {
			buffer.append("(`" + dateName + "` BETWEEN FROM_UNIXTIME(");
			buffer.append(after.getTime() / 1000);
			buffer.append(") AND FROM_UNIXTIME(");
			buffer.append(before.getTime() / 1000);
			buffer.append("))");
		} else if (after != null && before == null) {
			buffer.append("`" + dateName + "`>=FROM_UNIXTIME(");
			buffer.append(after.getTime() / 1000);
			buffer.append(")");
		} else if (before != null && after == null) {
			buffer.append("`" + dateName + "`<FROM_UNIXTIME(");
			buffer.append(before.getTime() / 1000);
			buffer.append(")");
		}

		return buffer.toString();
	}

	/**
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static String beforeAfterQuery(Date before, Date after) {
		return beforeAfterQuery(before, after, "date");
	}

	public static SimpleDateFormat getSqlDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * @param dateFieldName
	 * @param dateRange
	 * @return
	 */
	public static String getDateRangeCondition(String dateFieldName, String... dateRange) {
		if (dateRange == null || dateRange.length != 2) {
			LOG.log(Level.WARNING, String.format("Date range is empty or does not have exactly 2 elements %s", (Object) (dateRange)));
			return null;
		}

		return String.format("%s between '%s' and '%s'", dateFieldName, dateRange[0], dateRange[1]);
	}

	/**
	 * @param dateFieldName
	 * @param dates
	 * @return
	 */
	public static String getDateListCondition(String dateFieldName, String... dates) {
		if (dates == null || dates.length == 0) {
			LOG.log(Level.WARNING, String.format("Dates is empty or or null %s", (Object) (dates)));
			return null;
		}

		return String.format("%s in ('%s')", dateFieldName, StringUtils.join(Arrays.asList(dates), "' ,'"));
	}
}
